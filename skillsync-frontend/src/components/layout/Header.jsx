import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';

const Header = () => {
  const username = localStorage.getItem('username') || 'User';
  const role = localStorage.getItem('role') || 'ROLE_LEARNER';
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    const isRead = localStorage.getItem('notifs_read') === 'true';
    if (!isRead) {
      if (role === 'ROLE_ADMIN') {
        Promise.all([
          api.get('/auth-service/admin/mentor-requests').catch(() => ({ data: [] }))
        ]).then(([mentorsRes]) => {
          const pending = mentorsRes.data?.length || 0;
          setUnreadCount(pending);
        });
      } else {  
        setUnreadCount(2); // Mock count for learners/mentors
      } 
    } else {
      setUnreadCount(0);
    }

    const handleRead = () => setUnreadCount(0);
    window.addEventListener('notificationsRead', handleRead);
    return () => window.removeEventListener('notificationsRead', handleRead);
  }, [role]);

  const getInitials = (name) => {
    return name
      .split(' ')
      .map(part => part[0])
      .join('')
      .toUpperCase()
      .substring(0, 2);
  };

  const handleLogout = () => {
    localStorage.clear();
    window.location.href = '/login';
  };

  return (
    <header className="header">
      <div className="header-title">
      </div>
      <div className="header-user">
        <div className="notification-bell" style={{cursor: 'pointer'}} onClick={() => navigate('/app/notifications')}>
          🔔
          {unreadCount > 0 && <span className="notification-badge">{unreadCount}</span>}
        </div>
        <div style={{position: 'relative'}}>
          <div className="user-avatar" title={username} style={{cursor: 'pointer'}} onClick={() => setDropdownOpen(!dropdownOpen)}>
            {getInitials(username)}
          </div>
          
          {dropdownOpen && (
            <div style={{position: 'absolute', right: 0, top: '50px', backgroundColor: '#fff', boxShadow: '0 4px 12px rgba(0,0,0,0.1)', borderRadius: '8px', width: '150px', zIndex: 100, overflow: 'hidden'}}>
              <div style={{padding: '10px 15px', borderBottom: '1px solid #f0f0f0', color: '#111827', fontWeight: 600}}>
                {username}
              </div>
              <div style={{padding: '10px 15px', cursor: 'pointer', color: '#4b5563', fontSize: '0.9rem'}} onClick={() => { setDropdownOpen(false); navigate('/app/settings'); }}>
                ⚙️ Settings
              </div>
              <div style={{padding: '10px 15px', cursor: 'pointer', color: '#e11d48', fontSize: '0.9rem', borderTop: '1px solid #f0f0f0'}} onClick={handleLogout}>
                🚪 Log out
              </div>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;
