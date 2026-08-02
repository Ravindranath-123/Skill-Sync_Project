import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import './Dashboard.css';

const Dashboard = () => {
  const navigate = useNavigate();
  const username = localStorage.getItem('username') || 'User';
  const userId = localStorage.getItem('userId');
  const role = localStorage.getItem('role') || 'ROLE_LEARNER';
  
  const [mentors, setMentors] = useState([]);
  const [totalMentors, setTotalMentors] = useState(0);
  const [sessions, setSessions] = useState([]);
  const [allSkills, setAllSkills] = useState({});
  const [adminStats, setAdminStats] = useState({ totalUsers: 0, activeLearners: 0, activeMentors: 0, pendingMentors: 0, blockedUsers: 0 });
  const [adminUsersList, setAdminUsersList] = useState([]);
  const [pendingApprovals, setPendingApprovals] = useState([]);
  const [listType, setListType] = useState('Active Mentors');
  const [userNames, setUserNames] = useState({});
  const [viewMentorModal, setViewMentorModal] = useState({ isOpen: false, mentor: null });
  const [mentorReviews, setMentorReviews] = useState([]);
  const [loadingReviews, setLoadingReviews] = useState(false);
  
  const today = new Date();
  const dateOptions = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
  const formattedDate = today.toLocaleDateString('en-US', dateOptions);

  const fetchAdminData = () => {
    api.get('/auth-service/admin/stats')
      .then(res => setAdminStats(res.data))
      .catch(err => console.error('Failed to fetch admin stats', err));

    api.get('/auth-service/admin/mentor-requests')
      .then(res => setPendingApprovals(res.data))
      .catch(err => console.error('Failed to fetch pending mentors', err));
  };

  useEffect(() => {
    if (role === 'ROLE_ADMIN') {
      fetchAdminData();
      return; // Admins don't need to fetch standard mentors or sessions
    }

    // Only fetch recommended mentors for learners
    if (role === 'ROLE_LEARNER' || role === 'ROLE_USER') {
      // Fetch skills catalog for resolving IDs to names
      api.get('/skill-service/skills?size=100')
        .then(res => {
          if (res.data && res.data.content) {
            const skillMap = {};
            res.data.content.forEach(skill => {
              skillMap[skill.skillId] = skill.skillName;
            });
            setAllSkills(skillMap);
          }
        })
        .catch(err => console.error('Failed to load skills catalog for dashboard', err));

      api.get('/mentor-service/mentors/search?page=0&size=3')
        .then(async (res) => {
          const mentorsList = res.data.content || [];
          const mentorsWithNames = await Promise.all(mentorsList.map(async (mentor) => {
            try {
              const nameRes = await api.get(`/auth-service/auth/internal/users/${mentor.userId}/name`);
              return { ...mentor, username: nameRes.data };
            } catch (e) {
              return { ...mentor, username: `Mentor #${mentor.userId}` };
            }
          }));
          setMentors(mentorsWithNames);
          setTotalMentors(res.data.totalElements || mentorsWithNames.length || 0);
        })
        .catch(err => console.error('Failed to fetch mentors', err));
    }

    // Fetch sessions based on role
    if (userId) {
      const endpoint = role === 'ROLE_MENTOR' 
        ? `/session-service/sessions/mentor/${userId}` 
        : `/session-service/sessions/learner/${userId}`;
        
        api.get(endpoint)
        .then(res => {
          const fetchedSessions = res.data || [];
          const now = new Date();
          const processedSessions = fetchedSessions.map(session => {
             if (session.status === 'ACCEPTED') {
                const startTime = new Date(session.sessionTime);
                const endTime = new Date(startTime.getTime() + session.durationMinutes * 60000);
                if (endTime < now) {
                   api.post(`/session-service/sessions/${session.id}/complete`).catch(e => console.error("Auto complete error", e));
                   return { ...session, status: 'COMPLETED' };
                }
             }
             return session;
          });
          setSessions(processedSessions);
          
          // Fetch names for the users in these sessions
          processedSessions.forEach(session => {
            if (role === 'ROLE_MENTOR') {
              const targetId = session.learnerId;
              if (targetId && !userNames[targetId]) {
                api.get(`/auth-service/auth/internal/users/${targetId}/name`)
                  .then(nameRes => {
                    setUserNames(prev => ({...prev, [targetId]: nameRes.data}));
                  })
                  .catch(() => {});
              }
            } else {
              const profileId = session.mentorId;
              if (profileId && !userNames[`mentor_${profileId}`]) {
                // First get the true userId, then get the name
                api.get(`/mentor-service/mentors/internal/${profileId}/userid`)
                  .then(idRes => {
                    const realUserId = idRes.data;
                    return api.get(`/auth-service/auth/internal/users/${realUserId}/name`);
                  })
                  .then(nameRes => {
                    setUserNames(prev => ({...prev, [`mentor_${profileId}`]: nameRes.data}));
                  })
                  .catch(() => {});
              }
            }
          });
        })
        .catch(err => console.error('Failed to fetch sessions', err));
    }
  }, [userId, role]);

  const upcomingSessions = sessions.filter(s => s.status === 'REQUESTED' || s.status === 'ACCEPTED').length;
  const completedSessions = sessions.filter(s => s.status === 'COMPLETED').length;
  const pendingRequests = sessions.filter(s => s.status === 'REQUESTED').length;

  const handleApprove = async (id) => {
    try {
      await api.put(`/auth-service/admin/mentors/${id}/approve`);
      fetchAdminData(); // Refresh the list and stats
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to approve mentor');
    }
  };

  const handleReject = async (id) => {
    if (window.confirm('Are you sure you want to reject this mentor?')) {
      try {
        await api.put(`/auth-service/admin/mentors/${id}/reject`);
        fetchAdminData(); // Refresh the list and stats
      } catch (err) {
        alert(err.response?.data?.message || 'Failed to reject mentor');
      }
    }
  };

  useEffect(() => {
    if (viewMentorModal.isOpen && viewMentorModal.mentor) {
      setLoadingReviews(true);
      api.get(`/review-service/reviews/mentor/${viewMentorModal.mentor.mentorId || viewMentorModal.mentor.id}`)
        .then(res => {
          setMentorReviews(res.data || []);
        })
        .catch(err => {
          console.error("Failed to fetch mentor reviews", err);
          setMentorReviews([]);
        })
        .finally(() => {
          setLoadingReviews(false);
        });
    } else {
      setMentorReviews([]);
    }
  }, [viewMentorModal]);

  const handleAcceptSession = async (sessionId) => {
    try {
      await api.post(`/session-service/sessions/${sessionId}/accept`);
      // Refresh sessions
      const endpoint = role === 'ROLE_MENTOR' ? `/session-service/sessions/mentor/${userId}` : `/session-service/sessions/learner/${userId}`;
      const res = await api.get(endpoint);
      setSessions(res.data || []);
    } catch (err) {
      alert(err.response?.data?.message || err.response?.data || 'Failed to accept session');
    }
  };

  const handleRejectSession = async (sessionId) => {
    if (window.confirm('Are you sure you want to reject this session?')) {
      try {
        await api.post(`/session-service/sessions/${sessionId}/reject`);
        // Refresh sessions
        const endpoint = role === 'ROLE_MENTOR' ? `/session-service/sessions/mentor/${userId}` : `/session-service/sessions/learner/${userId}`;
        const res = await api.get(endpoint);
        setSessions(res.data || []);
      } catch (err) {
        alert(err.response?.data?.message || err.response?.data || 'Failed to reject session');
      }
    }
  };

  const fetchAdminList = (type) => {
    setListType(type);
    let endpoint = '';
    if (type === 'Active Mentors') endpoint = '/auth-service/admin/users/active-mentors';
    else if (type === 'Active Learners') endpoint = '/auth-service/admin/users/active-learners';
    else if (type === 'Pending Approvals') endpoint = '/auth-service/admin/mentor-requests';
    else if (type === 'Blocked Users') endpoint = '/auth-service/admin/users/blocked';
    
    if (endpoint) {
      api.get(endpoint)
        .then(res => setAdminUsersList(res.data))
        .catch(err => console.error(`Failed to fetch ${type}`, err));
    }
  };

  const handleBook = (mentor) => {
    const formattedMentor = {
      id: mentor.mentorId,
      userId: mentor.userId,
      name: mentor.username || `Mentor ${mentor.userId}`,
      hourlyRate: mentor.hourlyRate || 500,
      role: 'Platform Mentor',
      rating: mentor.averageRating || 0,
      initials: mentor.username ? mentor.username.substring(0, 2).toUpperCase() : `M${mentor.userId}`,
      avatarColor: '#e11d48'
    };
    navigate('/app/book', { state: { mentor: formattedMentor } });
  };

  const renderStars = (rating) => {
    const percentage = (rating / 5) * 100;
    return (
      <div style={{ position: 'relative', display: 'inline-block', color: '#e5e7eb', letterSpacing: '2px' }}>
        ★★★★★
        <div style={{
          position: 'absolute',
          top: 0,
          left: 0,
          overflow: 'hidden',
          width: `${percentage}%`,
          color: '#fbbf24',
          whiteSpace: 'nowrap'
        }}>
          ★★★★★
        </div>
      </div>
    );
  };

  if (role === 'ROLE_ADMIN') {
    return (
      <div className="dashboard-container">
        <div className="dashboard-header">
          <h2>Admin Console</h2>
          <p className="date-subtitle">Platform overview — {formattedDate}</p>
        </div>
        
        {/* Top Stats Cards */}
        <div style={{display: 'flex', gap: '1.5rem', marginBottom: '2rem'}}>
          <div style={{flex: 1, backgroundColor: '#fff', padding: '1.5rem', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.05)', display: 'flex', justifyContent: 'space-between'}}>
            <div>
              <div style={{fontSize: '2rem', fontWeight: 'bold', color: '#111827'}}>{adminStats.totalUsers || 0}</div>
              <div style={{color: '#6b7280', fontSize: '0.9rem'}}>Total Users</div>
              <div style={{color: '#10b981', fontSize: '0.8rem', marginTop: '0.5rem'}}>↑ +12% this month</div>
            </div>
            <div style={{backgroundColor: '#eff6ff', color: '#3b82f6', width: '40px', height: '40px', borderRadius: '8px', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.2rem'}}>👥</div>
          </div>
          
          <div style={{flex: 1, backgroundColor: '#fff', padding: '1.5rem', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.05)', display: 'flex', justifyContent: 'space-between'}}>
            <div>
              <div style={{fontSize: '2rem', fontWeight: 'bold', color: '#111827'}}>{adminStats.activeMentors || 0}</div>
              <div style={{color: '#6b7280', fontSize: '0.9rem'}}>Active Mentors</div>
              <div style={{color: '#10b981', fontSize: '0.8rem', marginTop: '0.5rem'}}>↑ +8% this month</div>
            </div>
            <div style={{backgroundColor: '#ecfeff', color: '#06b6d4', width: '40px', height: '40px', borderRadius: '8px', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.2rem'}}>🧑‍🏫</div>
          </div>
          
          <div style={{flex: 1, backgroundColor: '#fff', padding: '1.5rem', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.05)', display: 'flex', justifyContent: 'space-between'}}>
            <div>
              <div style={{fontSize: '2rem', fontWeight: 'bold', color: '#111827'}}>{(adminStats.totalUsers || 0) * 3 + 124}</div>
              <div style={{color: '#6b7280', fontSize: '0.9rem'}}>Sessions Booked</div>
              <div style={{color: '#10b981', fontSize: '0.8rem', marginTop: '0.5rem'}}>↑ +24% this month</div>
            </div>
            <div style={{backgroundColor: '#fffbeb', color: '#d97706', width: '40px', height: '40px', borderRadius: '8px', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.2rem'}}>📅</div>
          </div>

          <div style={{flex: 1, backgroundColor: '#fff', padding: '1.5rem', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.05)', display: 'flex', justifyContent: 'space-between'}}>
            <div>
              <div style={{fontSize: '2rem', fontWeight: 'bold', color: '#111827'}}>₹{(adminStats.activeMentors ? (adminStats.activeMentors * 12.5).toFixed(1) : 0)}K</div>
              <div style={{color: '#6b7280', fontSize: '0.9rem'}}>Platform Revenue</div>
              <div style={{color: '#10b981', fontSize: '0.8rem', marginTop: '0.5rem'}}>↑ +18% this month</div>
            </div>
            <div style={{backgroundColor: '#f0fdf4', color: '#16a34a', width: '40px', height: '40px', borderRadius: '8px', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.2rem'}}>💰</div>
          </div>
        </div>

        <div style={{display: 'flex', gap: '1.5rem'}}>
          
          {/* Main Left Column */}
          <div style={{flex: '2.5', display: 'flex', flexDirection: 'column', gap: '1.5rem'}}>
            
            {/* Pending Approvals Table */}
            <div style={{backgroundColor: '#fff', borderRadius: '12px', padding: '1.5rem', boxShadow: '0 2px 8px rgba(0,0,0,0.05)'}}>
              <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem'}}>
                <h3 style={{margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem'}}>⏳ Pending Mentor Approvals</h3>
                <a href="/app/users" style={{color: '#e11d48', fontSize: '0.9rem', textDecoration: 'none', fontWeight: 600}}>View all ({pendingApprovals.length}) →</a>
              </div>
              
              <table style={{width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.9rem'}}>
                <thead>
                  <tr style={{color: '#9ca3af', borderBottom: '1px solid #f3f4f6'}}>
                    <th style={{paddingBottom: '0.5rem'}}># APPLICANT</th>
                    <th style={{paddingBottom: '0.5rem'}}>SKILLS</th>
                    <th style={{paddingBottom: '0.5rem'}}>EXP</th>
                    <th style={{paddingBottom: '0.5rem'}}>ACTIONS</th>
                  </tr>
                </thead>
                <tbody>
                  {pendingApprovals.length === 0 ? (
                    <tr>
                      <td colSpan="4" style={{padding: '1rem 0', textAlign: 'center', color: '#6b7280'}}>No pending mentor approvals.</td>
                    </tr>
                  ) : (
                    pendingApprovals.map((user, index) => (
                      <tr key={user.userId} style={{borderBottom: '1px solid #f3f4f6'}}>
                        <td style={{padding: '1rem 0', display: 'flex', alignItems: 'center', gap: '0.75rem'}}>
                          <span style={{color: '#6b7280'}}>{index + 1}</span>
                          <div style={{width: '32px', height: '32px', borderRadius: '50%', backgroundColor: '#ef4444', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold'}}>
                            {user.username.substring(0, 2).toUpperCase()}
                          </div>
                          <div>
                            <div style={{fontWeight: 600, color: '#111827'}}>{user.username}</div>
                            <div style={{color: '#9ca3af', fontSize: '0.8rem'}}>{user.email}</div>
                          </div>
                        </td>
                        <td style={{padding: '1rem 0'}}>
                          <span style={{backgroundColor: '#f3f4f6', padding: '2px 8px', borderRadius: '12px', fontSize: '0.75rem', marginRight: '4px'}}>Pending Review</span>
                        </td>
                        <td style={{padding: '1rem 0', color: '#4b5563'}}>-</td>
                        <td style={{padding: '1rem 0'}}>
                          <button onClick={() => handleApprove(user.userId)} style={{backgroundColor: '#dcfce7', color: '#166534', border: 'none', padding: '4px 12px', borderRadius: '4px', fontWeight: 600, cursor: 'pointer', marginRight: '8px'}}>✓ Approve</button>
                          <button onClick={() => handleReject(user.userId)} style={{backgroundColor: '#fee2e2', color: '#dc2626', border: 'none', padding: '4px 12px', borderRadius: '4px', fontWeight: 600, cursor: 'pointer'}}>✕</button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {/* Platform Performance */}
            <div style={{backgroundColor: '#fff', borderRadius: '12px', padding: '1.5rem', boxShadow: '0 2px 8px rgba(0,0,0,0.05)'}}>
              <h3 style={{margin: '0 0 1.5rem 0', display: 'flex', alignItems: 'center', gap: '0.5rem'}}>📈 Platform Performance (last 30 days)</h3>
              
              <div style={{marginBottom: '1rem'}}>
                <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem', fontSize: '0.9rem', fontWeight: 600}}>
                  <span>Session Completion Rate</span>
                  <span>82%</span>
                </div>
                <div style={{width: '100%', height: '8px', backgroundColor: '#f3f4f6', borderRadius: '4px', overflow: 'hidden'}}>
                  <div style={{width: '82%', height: '100%', backgroundColor: '#e11d48'}}></div>
                </div>
              </div>

              <div style={{marginBottom: '1rem'}}>
                <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem', fontSize: '0.9rem', fontWeight: 600}}>
                  <span>Mentor Satisfaction Score</span>
                  <span>91%</span>
                </div>
                <div style={{width: '100%', height: '8px', backgroundColor: '#f3f4f6', borderRadius: '4px', overflow: 'hidden'}}>
                  <div style={{width: '91%', height: '100%', backgroundColor: '#16a34a'}}></div>
                </div>
              </div>

              <div style={{marginBottom: '1rem'}}>
                <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem', fontSize: '0.9rem', fontWeight: 600}}>
                  <span>Group Engagement Rate</span>
                  <span>65%</span>
                </div>
                <div style={{width: '100%', height: '8px', backgroundColor: '#f3f4f6', borderRadius: '4px', overflow: 'hidden'}}>
                  <div style={{width: '65%', height: '100%', backgroundColor: '#ea580c'}}></div>
                </div>
              </div>

              <div>
                <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem', fontSize: '0.9rem', fontWeight: 600}}>
                  <span>Learner Retention</span>
                  <span>78%</span>
                </div>
                <div style={{width: '100%', height: '8px', backgroundColor: '#f3f4f6', borderRadius: '4px', overflow: 'hidden'}}>
                  <div style={{width: '78%', height: '100%', backgroundColor: '#2563eb'}}></div>
                </div>
              </div>
            </div>

          </div>

          {/* Right Column: Recent Activity */}
          <div style={{flex: '1', backgroundColor: '#fff', borderRadius: '12px', padding: '1.5rem', boxShadow: '0 2px 8px rgba(0,0,0,0.05)'}}>
            <h3 style={{margin: '0 0 1.5rem 0', display: 'flex', alignItems: 'center', gap: '0.5rem'}}>🔔 Recent Activity</h3>
            
            <div style={{display: 'flex', flexDirection: 'column', gap: '1.5rem'}}>
              
              <div style={{display: 'flex', gap: '1rem'}}>
                <div style={{width: '32px', height: '32px', borderRadius: '50%', backgroundColor: '#dcfce7', color: '#16a34a', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0}}>✓</div>
                <div>
                  <div style={{fontSize: '0.9rem', color: '#374151'}}>Mentor <strong>Divya Verma</strong> approved</div>
                  <div style={{fontSize: '0.8rem', color: '#9ca3af', marginTop: '2px'}}>5 min ago</div>
                </div>
              </div>

              <div style={{display: 'flex', gap: '1rem'}}>
                <div style={{width: '32px', height: '32px', borderRadius: '50%', backgroundColor: '#fee2e2', color: '#e11d48', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0}}>📅</div>
                <div>
                  <div style={{fontSize: '0.9rem', color: '#374151'}}>New session booked by <strong>Rahul S.</strong></div>
                  <div style={{fontSize: '0.8rem', color: '#9ca3af', marginTop: '2px'}}>12 min ago</div>
                </div>
              </div>

              <div style={{display: 'flex', gap: '1rem'}}>
                <div style={{width: '32px', height: '32px', borderRadius: '50%', backgroundColor: '#eff6ff', color: '#2563eb', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0}}>👥</div>
                <div>
                  <div style={{fontSize: '0.9rem', color: '#374151'}}>Group <strong>DSA Prep</strong> reached 100 members</div>
                  <div style={{fontSize: '0.8rem', color: '#9ca3af', marginTop: '2px'}}>1h ago</div>
                </div>
              </div>

              <div style={{display: 'flex', gap: '1rem'}}>
                <div style={{width: '32px', height: '32px', borderRadius: '50%', backgroundColor: '#fef3c7', color: '#d97706', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0}}>⭐</div>
                <div>
                  <div style={{fontSize: '0.9rem', color: '#374151'}}><strong>Priya Sharma</strong> received 5-star review</div>
                  <div style={{fontSize: '0.8rem', color: '#9ca3af', marginTop: '2px'}}>2h ago</div>
                </div>
              </div>

              <div style={{display: 'flex', gap: '1rem'}}>
                <div style={{width: '32px', height: '32px', borderRadius: '50%', backgroundColor: '#e0e7ff', color: '#4f46e5', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0}}>👤</div>
                <div>
                  <div style={{fontSize: '0.9rem', color: '#374151'}}>14 new users registered today</div>
                  <div style={{fontSize: '0.8rem', color: '#9ca3af', marginTop: '2px'}}>3h ago</div>
                </div>
              </div>

              <div style={{display: 'flex', gap: '1rem'}}>
                <div style={{width: '32px', height: '32px', borderRadius: '50%', backgroundColor: '#fee2e2', color: '#dc2626', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0}}>🚨</div>
                <div>
                  <div style={{fontSize: '0.9rem', color: '#374151'}}>Report filed on group <strong>XYZ</strong></div>
                  <div style={{fontSize: '0.8rem', color: '#9ca3af', marginTop: '2px'}}>5h ago</div>
                </div>
              </div>

            </div>
          </div>

        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h2>Welcome back, {username}! 👋</h2>
        <p className="date-subtitle">
          {formattedDate} — Here's your {role === 'ROLE_MENTOR' ? 'teaching' : 'learning'} overview
        </p>
      </div>

      {/* Stats Cards */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon calendar-icon">📅</div>
          <div className="stat-value">{role === 'ROLE_MENTOR' ? pendingRequests : upcomingSessions}</div>
          <div className="stat-label">{role === 'ROLE_MENTOR' ? 'Pending Requests' : 'Upcoming Sessions'}</div>
          <div className="stat-trend positive">{role === 'ROLE_MENTOR' ? 'Real-time data' : '↑ This week'}</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon mentor-icon">🧑‍🏫</div>
          <div className="stat-value">{role === 'ROLE_MENTOR' ? sessions.length : totalMentors}</div>
          <div className="stat-label">{role === 'ROLE_MENTOR' ? 'Total Sessions' : 'Total Mentors'}</div>
          <div className="stat-trend positive">{role === 'ROLE_MENTOR' ? 'All time' : '↑ +1 this month'}</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon completed-icon">⭐</div>
          <div className="stat-value">{completedSessions}</div>
          <div className="stat-label">Sessions Completed</div>
          <div className="stat-trend positive">{role === 'ROLE_MENTOR' ? 'Real-time data' : '↑ +3 this month'}</div>
        </div>
      </div>

      {/* Recommended Mentors - ONLY FOR LEARNERS */}
      {(role === 'ROLE_LEARNER' || role === 'ROLE_USER') && (
        <>
          <div className="section-header">
            <h3>Recommended Mentors</h3>
          </div>
          <div className="mentors-grid">
            {mentors.length === 0 ? (
              <p>No mentors found at the moment.</p>
            ) : (
              mentors.map(mentor => (
                <div className="mentor-card" key={mentor.mentorId}>
                  <div className={`availability-badge ${mentor.available ? 'available' : 'unavailable'}`} style={{position: 'absolute', top: '1rem', right: '1rem', fontSize: '0.75rem', padding: '0.25rem 0.75rem', borderRadius: '12px', fontWeight: 600, backgroundColor: mentor.available ? '#dcfce7' : '#fee2e2', color: mentor.available ? '#166534' : '#991b1b'}}>
                    {mentor.available ? '● Available' : '○ Unavailable'}
                  </div>
                  <div className="mentor-header">
                    <div className="mentor-avatar avatar-ps">
                      {mentor.username ? mentor.username.substring(0, 2).toUpperCase() : `M${mentor.userId}`}
                    </div>
                    <div className="mentor-info">
                      <h4>{mentor.username || `Mentor #${mentor.mentorId}`}</h4>
                      <p>{mentor.experienceYears} yrs experience</p>
                    </div>
                  </div>
                  <div className="mentor-rating" style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                    {renderStars(mentor.averageRating || 0)}
                    <span className="rating-score" style={{ marginLeft: '0.25rem' }}>{(mentor.averageRating || 0).toFixed(1)}</span>
                    <span className="review-count">({mentor.totalSessions || 0} reviews)</span>
                  </div>
                  <div className="mentor-skills">
                    {mentor.mentorSkills?.slice(0, 3).map(skill => {
                      const skillName = allSkills[skill.skillId] || skill.skillName || `Skill ${skill.skillId}`;
                      return (
                        <span className="skill-tag" key={skill.id || skill.skillId}>{skillName}</span>
                      );
                    })}
                  </div>
                  <div className="mentor-footer">
                    <div className="mentor-price" style={{color: '#e11d48', fontWeight: 'bold'}}>₹{mentor.hourlyRate || 500} / hr</div>
                    <div style={{display: 'flex', gap: '0.5rem'}}>
                      <button 
                        onClick={() => setViewMentorModal({ isOpen: true, mentor })}
                        style={{backgroundColor: '#fff', color: '#e11d48', border: '1px solid #e11d48', padding: '0.5rem 1rem', borderRadius: '6px', fontWeight: 600, cursor: 'pointer'}}>
                        View
                      </button>
                      <button className="book-btn" disabled={!mentor.available} style={{backgroundColor: '#e11d48', color: '#fff', border: 'none', opacity: mentor.available ? 1 : 0.5, cursor: mentor.available ? 'pointer' : 'not-allowed', padding: '0.5rem 1rem', borderRadius: '6px', fontWeight: 600}} onClick={() => handleBook(mentor)}>
                        Book
                      </button>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </>
      )}

      <div className="section-header">
        <h3>{role === 'ROLE_MENTOR' ? 'My Teaching Sessions' : 'Upcoming Sessions'}</h3>
      </div>
      <div className="sessions-table-container">
        {sessions.length === 0 ? (
          <p style={{padding: '1rem'}}>You have no sessions yet.</p>
        ) : (
          <table className="sessions-table">
            <thead>
              <tr>
                {role === 'ROLE_MENTOR' ? (
                  <>
                    <th>START TIME</th>
                    <th>END TIME</th>
                    <th>LEARNER ID</th>
                    <th>STATUS</th>
                    {sessions.some(s => s.status === 'REQUESTED') && <th>ACTIONS</th>}
                  </>
                ) : (
                  <>
                    <th>DATE & TIME</th>
                    <th>MENTOR</th>
                    <th>TOPIC</th>
                    <th>DURATION</th>
                    <th>STATUS</th>
                  </>
                )}
              </tr>
            </thead>
            <tbody>
              {sessions.map(session => {
                const startTime = new Date(session.sessionTime);
                const endTime = new Date(startTime.getTime() + session.durationMinutes * 60000);
                
                if (role === 'ROLE_MENTOR') {
                  return (
                    <tr key={session.id}>
                      <td>📅 {startTime.toLocaleString()}</td>
                      <td>{endTime.toLocaleTimeString()}</td>
                      <td>{userNames[session.learnerId] || `Learner #${session.learnerId}`}</td>
                      <td>
                        <span className={`status-badge ${session.status.toLowerCase()}`} style={{
                          backgroundColor: session.status === 'ACCEPTED' ? '#dcfce7' : (session.status === 'REQUESTED' ? '#fef3c7' : (session.status === 'COMPLETED' ? '#e0e7ff' : '#fee2e2')),
                          color: session.status === 'ACCEPTED' ? '#166534' : (session.status === 'REQUESTED' ? '#d97706' : (session.status === 'COMPLETED' ? '#4f46e5' : '#991b1b')),
                          padding: '4px 12px', borderRadius: '20px', fontWeight: 600, fontSize: '0.85rem', display: 'inline-block'
                        }}>
                          {session.status === 'REQUESTED' ? 'Pending' : session.status.charAt(0).toUpperCase() + session.status.slice(1).toLowerCase()}
                        </span>
                      </td>
                      {sessions.some(s => s.status === 'REQUESTED') && (
                        <td>
                          {session.status === 'REQUESTED' && (
                            <div style={{display: 'flex', gap: '0.5rem'}}>
                              <button 
                                onClick={() => handleAcceptSession(session.id)}
                                style={{backgroundColor: '#dcfce7', color: '#166534', border: 'none', padding: '4px 12px', borderRadius: '4px', cursor: 'pointer', fontWeight: 600}}>
                                Approve
                              </button>
                              <button 
                                onClick={() => handleRejectSession(session.id)}
                                style={{backgroundColor: '#fee2e2', color: '#dc2626', border: 'none', padding: '4px 12px', borderRadius: '4px', cursor: 'pointer', fontWeight: 600}}>
                                Reject
                              </button>
                            </div>
                          )}
                        </td>
                      )}
                    </tr>
                  );
                } else {
                  return (
                    <tr key={session.id}>
                      <td>📅 {startTime.toLocaleString([], {month: 'short', day: 'numeric'})} - {startTime.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</td>
                      <td style={{fontWeight: 'bold'}}>{userNames[`mentor_${session.mentorId}`] || `Mentor #${session.mentorId}`}</td>
                      <td>Software Engineering</td>
                      <td>{session.durationMinutes} min</td>
                      <td>
                        <span className={`status-badge ${session.status.toLowerCase()}`} style={{
                          backgroundColor: session.status === 'ACCEPTED' ? '#dcfce7' : (session.status === 'REQUESTED' ? '#fef3c7' : (session.status === 'COMPLETED' ? '#e0e7ff' : '#fee2e2')),
                          color: session.status === 'ACCEPTED' ? '#166534' : (session.status === 'REQUESTED' ? '#d97706' : (session.status === 'COMPLETED' ? '#4f46e5' : '#991b1b')),
                          padding: '4px 12px', borderRadius: '20px', fontWeight: 600, fontSize: '0.85rem'
                        }}>
                          {session.status === 'REQUESTED' ? 'Pending' : session.status.charAt(0).toUpperCase() + session.status.slice(1).toLowerCase()}
                        </span>
                      </td>
                    </tr>
                  );
                }
              })}
            </tbody>
          </table>
        )}
      </div>

      {viewMentorModal.isOpen && viewMentorModal.mentor && (
        <div style={{position: 'fixed', top: 0, left: 0, width: '100%', height: '100%', backgroundColor: 'rgba(0,0,0,0.5)', zIndex: 1000, display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
          <div style={{backgroundColor: '#fff', padding: '2rem', borderRadius: '12px', width: '90%', maxWidth: '500px', boxShadow: '0 10px 25px rgba(0,0,0,0.2)'}}>
            <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem'}}>
              <h3 style={{margin: 0, display: 'flex', alignItems: 'center', gap: '1rem'}}>
                <div style={{width: '40px', height: '40px', borderRadius: '50%', backgroundColor: '#e11d48', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.2rem', fontWeight: 'bold'}}>
                  {viewMentorModal.mentor.username ? viewMentorModal.mentor.username.substring(0, 2).toUpperCase() : `M${viewMentorModal.mentor.userId}`}
                </div>
                {viewMentorModal.mentor.username || `Mentor #${viewMentorModal.mentor.userId}`}
              </h3>
              <button onClick={() => setViewMentorModal({ isOpen: false, mentor: null })} style={{background: 'none', border: 'none', fontSize: '1.5rem', cursor: 'pointer', color: '#6b7280'}}>&times;</button>
            </div>
            
            <div style={{marginBottom: '1.5rem', color: '#4b5563', lineHeight: '1.6'}}>
              <strong>Bio:</strong> {viewMentorModal.mentor.bio || "This mentor is highly experienced and ready to help you achieve your goals. They specialize in various industry-standard tools and practices."}
            </div>

            <div style={{display: 'flex', gap: '2rem', marginBottom: '1.5rem'}}>
              <div>
                <strong style={{display: 'block', marginBottom: '0.25rem', color: '#374151'}}>Experience</strong>
                <span style={{color: '#6b7280'}}>{viewMentorModal.mentor.experienceYears || 0} Years</span>
              </div>
              <div>
                <strong style={{display: 'block', marginBottom: '0.25rem', color: '#374151'}}>Hourly Rate</strong>
                <span style={{color: '#6b7280', fontWeight: 'bold'}}>₹{viewMentorModal.mentor.hourlyRate || 500}/hr</span>
              </div>
              <div>
                <strong style={{display: 'block', marginBottom: '0.25rem', color: '#374151'}}>Rating</strong>
                <span style={{color: '#6b7280'}}>⭐ {(viewMentorModal.mentor.averageRating || 0).toFixed(1)} ({viewMentorModal.mentor.totalSessions || 0} reviews)</span>
              </div>
            </div>

            <div style={{marginBottom: '2rem'}}>
              <strong style={{display: 'block', marginBottom: '0.5rem', color: '#374151'}}>Skills</strong>
              <div style={{display: 'flex', gap: '0.5rem', flexWrap: 'wrap'}}>
                {(viewMentorModal.mentor.mentorSkills || []).map(skill => (
                  <span key={skill.id} style={{backgroundColor: '#f3f4f6', padding: '4px 12px', borderRadius: '16px', fontSize: '0.85rem', color: '#4b5563'}}>
                    {allSkills[skill.skillId] || skill.skillName || `Skill ${skill.skillId}`}
                  </span>
                ))}
                {(!viewMentorModal.mentor.mentorSkills || viewMentorModal.mentor.mentorSkills.length === 0) && (
                  <span style={{color: '#9ca3af', fontStyle: 'italic'}}>No specific skills listed</span>
                )}
              </div>
            </div>

            <div style={{marginBottom: '2rem'}}>
              <strong style={{display: 'block', marginBottom: '0.75rem', color: '#374151', borderBottom: '1px solid #e5e7eb', paddingBottom: '0.5rem'}}>Learner Reviews</strong>
              <div style={{maxHeight: '200px', overflowY: 'auto', paddingRight: '0.5rem'}}>
                {loadingReviews ? (
                  <div style={{color: '#6b7280', fontStyle: 'italic', fontSize: '0.9rem'}}>Loading reviews...</div>
                ) : mentorReviews.length > 0 ? (
                  <div style={{display: 'flex', flexDirection: 'column', gap: '1rem'}}>
                    {mentorReviews.map(review => (
                      <div key={review.id} style={{backgroundColor: '#f9fafb', padding: '1rem', borderRadius: '8px', border: '1px solid #f3f4f6'}}>
                        <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem'}}>
                          <div style={{color: '#fbbf24', fontSize: '0.9rem'}}>
                            {[...Array(5)].map((_, i) => <span key={i}>{i < review.rating ? '★' : '☆'}</span>)}
                          </div>
                          <div style={{color: '#9ca3af', fontSize: '0.8rem'}}>
                            {new Date(review.createdAt).toLocaleDateString()}
                          </div>
                        </div>
                        <p style={{margin: 0, color: '#4b5563', fontSize: '0.9rem', fontStyle: 'italic'}}>"{review.comment}"</p>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div style={{color: '#6b7280', fontStyle: 'italic', fontSize: '0.9rem'}}>No reviews yet for this mentor.</div>
                )}
              </div>
            </div>

            <div style={{display: 'flex', justifyContent: 'flex-end', gap: '1rem', borderTop: '1px solid #e5e7eb', paddingTop: '1.5rem'}}>
              <button 
                onClick={() => setViewMentorModal({ isOpen: false, mentor: null })}
                style={{backgroundColor: '#fff', color: '#4b5563', border: '1px solid #d1d5db', padding: '0.5rem 1rem', borderRadius: '6px', fontWeight: 600, cursor: 'pointer'}}
              >
                Close
              </button>
              <button 
                onClick={() => {
                  handleBook(viewMentorModal.mentor);
                  setViewMentorModal({ isOpen: false, mentor: null });
                }}
                disabled={!viewMentorModal.mentor.available}
                style={{backgroundColor: '#e11d48', color: '#fff', border: 'none', opacity: viewMentorModal.mentor.available ? 1 : 0.5, cursor: viewMentorModal.mentor.available ? 'pointer' : 'not-allowed', padding: '0.5rem 1rem', borderRadius: '6px', fontWeight: 600}}
              >
                Book Session
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;
