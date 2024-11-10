import React, { useState, useEffect, useCallback } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import NotificationDropdown from './NotificationDropdown'; // Import the new NotificationDropdown component
import api from '../../ApiConfig';
import "../../css_styles/Header.css";
import logo from "../../assets/logo.png"; // Update this path as needed

const Header = () => {
  const navigate = useNavigate();
  const location = useLocation(); // Get the current route

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  const userId = localStorage.getItem('user_id');
  const userRole = localStorage.getItem('user_role');
  const [user, setUser] = useState(null);

  // Memoize fetchUser to avoid re-creating it on every render
  const fetchUser = useCallback(async () => {
    try {
      const response = await api.get(`/api/users/${userId}`);
      setUser(response.data);
    } catch (error) {
      console.error('Error fetching user:', error);
    }
  }, [userId]);

  useEffect(() => {
    fetchUser();
  }, [fetchUser]); // Now we can safely add fetchUser to the dependency array

  // Determine if current route matches
  const isHome = location.pathname === "/";
  const isLogin = location.pathname === "/login";
  const isRegister = location.pathname === "/register";
  const isAdminDashboard = location.pathname === "/admin/dashboard";
  const isUserDashboard = location.pathname === "/dashboard";
  const isMyProfile = location.pathname === "/myProfile";

  return (
    <header className="header">
      <div className="header-left">
        <img src={logo} alt="Logo" className="logo" />
        <h1 className="site-name">Task Alert - Smart Task Management System</h1>
      </div>
      <nav className="header-right">
        {localStorage.getItem("token") ? (
          <div className="user-menu">
            {user && (
              <div className="user-greeting">
                Hello, {user.name}...
              </div>
            )}
            {userRole === "ADMIN_USER" ? (
              <div className="admin-links">
                {!isAdminDashboard && (
                  <Link className="menu-link" to="/admin/dashboard">
                    Dashboard
                  </Link>
                )}
                {!isMyProfile && (
                  <Link className="menu-link" to="/myProfile">
                    My Profile
                  </Link>
                )}
              </div>
            ) : (
              <div className="user-links">
                {!isUserDashboard && (
                  <Link className="menu-link" to="/dashboard">
                    Dashboard
                  </Link>
                )}
                {!isMyProfile && (
                  <Link className="menu-link" to="/myProfile">
                    My Profile
                  </Link>
                )}
              </div>
            )}
            {/* Notification Dropdown */}
            <NotificationDropdown userId={userId} />
            <button className="logout-button" onClick={handleLogout}>
              Logout
            </button>
          </div>
        ) : (
          <div className="auth-links">
            {!isHome && (
              <Link className="menu-link" to="/">
                Home
              </Link>
            )}
            {!isLogin && (
              <Link className="menu-link" to="/login">
                <b>Login</b>
              </Link>
            )}
            {!isRegister && (
              <Link className="menu-link" to="/register">
                <b>Register</b>
              </Link>
            )}
          </div>
        )}
      </nav>
    </header>
  );
};

export default Header;
