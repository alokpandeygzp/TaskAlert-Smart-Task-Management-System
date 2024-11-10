import React from 'react';
import { useNavigate } from 'react-router-dom';
import Header from './CommonAsset/Header';
import '../css_styles/Dashboard.css';
import { FaUser, FaTasks } from 'react-icons/fa';

const Dashboard = () => {
  const navigate = useNavigate();

  // Navigate to different sections
  const handleBoxClick = (path) => {
    navigate(path);
  };

  return (
    <div>
      <Header />
      <div className="dashboard-container">
        <h2 className="dashboard-title">Welcome to Your Dashboard</h2>
        <p className="dashboard-description">
          This is your personal dashboard where you can view your profile and see the tasks assigned to you. 
          Stay organized and track your progress efficiently.
        </p>

        {/* Dashboard Overview Section */}
        <div className="dashboard-grid">
          {/* My Profile Section */}
          <div className="dashboard-box" onClick={() => handleBoxClick('/myProfile')}>
            <FaUser className="dashboard-icon" />
            <h3>My Profile</h3>
            <p>View and update your profile details and regularly update your password.</p>
          </div>

          {/* Assigned Tasks Section */}
          <div className="dashboard-box" onClick={() => handleBoxClick('/assignedTasks')}>
            <FaTasks className="dashboard-icon" />
            <h3>Assigned Tasks</h3>
            <p>Track the tasks assigned to you, including their deadlines, priorities, and progress.</p>
          </div>
        </div>

        {/* Additional Information Section */}
        <div className="dashboard-footer">
          <h3>Your Responsibilities</h3>
          <p>
            As a user, you are responsible for completing the tasks assigned to you by the admin. Ensure that you meet your deadlines and report your progress regularly.
          </p>

          <h3>Important Reminders</h3>
          <ul>
            <li>Check your assigned tasks regularly to stay up to date.</li>
            <li>Make sure to update the task status once completed.</li>
            <li>If you're facing issues with a task, communicate with the admin or team lead promptly.</li>
          </ul>

          <h3>Quick Tips</h3>
          <ul>
            <li>Use the "Assigned Tasks" section to track upcoming deadlines and priorities.</li>
            <li>Keep your profile updated to ensure accurate information is shared across the system.</li>
            <li>If you need to ask for an extension or clarification, be proactive in reaching out.</li>
          </ul>
        </div>

      </div>
    </div>
  );
};

export default Dashboard;
