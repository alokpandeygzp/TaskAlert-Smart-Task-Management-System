import React from 'react';
import { useNavigate } from 'react-router-dom';
import Header from './CommonAsset/Header';
import '../css_styles/Dashboard.css';
import { FaUser, FaTasks, FaEye } from 'react-icons/fa';

const AdminDashboard = () => {
  const navigate = useNavigate();

  const handleBoxClick = (path) => {
    navigate(path);
  };

  return (
    <div>
      {localStorage.getItem("user_role") === "ADMIN_USER" ? (
        <div>
          <Header />
          <div className="dashboard-container">
            <h2 className="dashboard-title">Welcome to the Admin Dashboard</h2>
            <p className="dashboard-description">
              As an Admin, you have the ability to manage users, assign tasks to team members, and view all tasks in the system. 
              This dashboard gives you quick access to all the administrative functionalities you need.
            </p>
            <div className="dashboard-grid">
              {/* Manage Users Box */}
              <div className="dashboard-box" onClick={() => handleBoxClick('/users')}>
                <FaUser className="dashboard-icon" />
                <h3>Manage Users</h3>
                <p>Here, you can view, add, and manage users who are part of the task management system. Ensure that users are properly assigned tasks based on their roles.</p>
              </div>

              {/* Assign Tasks Box */}
              <div className="dashboard-box" onClick={() => handleBoxClick('/tasks')}>
                <FaTasks className="dashboard-icon" />
                <h3>Assign Tasks</h3>
                <p>Assign tasks to users and set priorities and deadlines. Easily track progress and ensure work is completed on time.</p>
              </div>

              {/* View Tasks Box */}
              <div className="dashboard-box" onClick={() => handleBoxClick('/tasksView')}>
                <FaEye className="dashboard-icon" />
                <h3>View Tasks</h3>
                <p>Monitor the status of all tasks in the system. Check which tasks are pending, completed, or overdue. Stay updated with real-time task progress.</p>
              </div>
            </div>

            {/* Section Below Buttons */}
            <div className="dashboard-footer">
              <h3>Admin's Responsibilities</h3>
              <p>
                As an admin, you are responsible for ensuring that tasks are assigned and completed on time. You must manage user roles,
                check the status of tasks regularly, and ensure that no tasks are left unattended.
              </p>
              <h3>Important Reminders</h3>
              <ul>
                <li>Review task deadlines regularly to avoid delays.</li>
                <li>Ensure all users are assigned to the correct tasks based on their skills and availability.</li>
                <li>Check for overdue tasks and take necessary actions.</li>
              </ul>
              <h3>Quick Tips</h3>
              <ul>
                <li>Click on the "View Tasks" section to see all tasks at a glance and track their status.</li>
                <li>You can assign tasks with deadlines and priorities to help manage workloads efficiently.</li>
                <li>If you need help, consult the user guide or contact support.</li>
              </ul>
            </div>

          </div>
        </div>
      ) : (
        <p className="error">Hello, <br />You are not an Admin! Please login as an admin to access this page.</p>
      )}
    </div>
  );
};

export default AdminDashboard;
