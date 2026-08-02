import React, { useState, useEffect } from 'react';
import api from '../services/api';

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const role = localStorage.getItem('role') || 'ROLE_LEARNER';

  useEffect(() => {
    if (role === 'ROLE_ADMIN') {
      Promise.all([
        api.get('/auth-service/admin/mentor-requests').catch(() => ({ data: [] })),
        api.get('/auth-service/admin/users/active-learners').catch(() => ({ data: [] }))
      ])
        .then(([mentorsRes, learnersRes]) => {
          const pendingMentors = mentorsRes.data || [];

          const dynamicNotifs = pendingMentors.map(m => ({
            id: `pending-${m.userId}`,
            title: 'Pending Mentor Approval',
            message: `${m.username} (${m.email}) has requested to become a mentor. Please review their application.`,
            time: 'Just now',
            read: localStorage.getItem('notifs_read') === 'true',
            type: 'alert'
          }));

          setNotifications(dynamicNotifs);
        })
        .catch(err => {
          console.error(err);
          setNotifications([{ id: 'err-1', title: 'Connection Error', message: 'Could not fetch live notifications.', time: 'Just now', read: false, type: 'alert' }]);
        })
        .finally(() => setLoading(false));
    } else {
      setNotifications([
        { id: 1, title: 'New Platform Update', message: 'SkillSync v2.0 is now live with real-time dashboards!', time: '2 hours ago', read: localStorage.getItem('notifs_read') === 'true', type: 'system' },
        { id: 2, title: 'Session Reminder', message: 'Your upcoming session with Alex starts in 30 minutes.', time: '5 hours ago', read: localStorage.getItem('notifs_read') === 'true', type: 'alert' },
        { id: 3, title: 'Account Verified', message: 'Your account has been verified.', time: '1 day ago', read: true, type: 'success' },
      ]);
      setLoading(false);
    }
  }, [role]);

  const markAllAsRead = () => {
    setNotifications(notifications.map(n => ({ ...n, read: true })));
    localStorage.setItem('notifs_read', 'true');
    window.dispatchEvent(new Event('notificationsRead'));
  };

  const getIcon = (type) => {
    switch(type) {
      case 'system': return '⚙️';
      case 'alert': return '⚠️';
      case 'success': return '✅';
      case 'info': return 'ℹ️';
      default: return '🔔';
    }
  };

  if (loading) return <div style={{padding: '2rem'}}>Loading notifications...</div>;

  return (
    <div className="dashboard-container">
      <div className="dashboard-header" style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
        <div>
          <h2>Notifications 🔔</h2>
          <p className="date-subtitle">Stay updated with your latest alerts and messages.</p>
        </div>
        <button onClick={markAllAsRead} style={{backgroundColor: '#f3f4f6', color: '#374151', border: '1px solid #d1d5db', padding: '0.5rem 1rem', borderRadius: '6px', fontWeight: 500, cursor: 'pointer', transition: 'all 0.2s'}}>
          Mark all as read
        </button>
      </div>

      <div style={{backgroundColor: '#fff', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.05)', overflow: 'hidden'}}>
        {notifications.length === 0 ? (
          <div style={{padding: '3rem', textAlign: 'center', color: '#6b7280'}}>
            <span style={{fontSize: '3rem', display: 'block', marginBottom: '1rem'}}>📭</span>
            You're all caught up! No new notifications.
          </div>
        ) : (
          <div style={{display: 'flex', flexDirection: 'column'}}>
            {notifications.map((notif, index) => (
              <div key={notif.id} style={{
                display: 'flex', gap: '1rem', padding: '1.5rem', 
                borderBottom: index < notifications.length - 1 ? '1px solid #f0f0f0' : 'none',
                backgroundColor: notif.read ? '#fff' : '#f8fafc',
                transition: 'background 0.2s'
              }}>
                <div style={{
                  width: '40px', height: '40px', borderRadius: '50%', 
                  backgroundColor: notif.read ? '#f3f4f6' : '#e0e7ff', 
                  display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.2rem'
                }}>
                  {getIcon(notif.type)}
                </div>
                <div style={{flex: 1}}>
                  <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '0.25rem'}}>
                    <h4 style={{margin: 0, fontSize: '1rem', color: '#111827', fontWeight: notif.read ? 500 : 600}}>
                      {notif.title}
                    </h4>
                    <span style={{fontSize: '0.8rem', color: '#9ca3af'}}>{notif.time}</span>
                  </div>
                  <p style={{margin: 0, color: '#4b5563', fontSize: '0.9rem', lineHeight: 1.5}}>
                    {notif.message}
                  </p>
                </div>
                {!notif.read && (
                  <div style={{display: 'flex', alignItems: 'center'}}>
                    <div style={{width: '10px', height: '10px', borderRadius: '50%', backgroundColor: '#4f46e5'}}></div>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Notifications;
