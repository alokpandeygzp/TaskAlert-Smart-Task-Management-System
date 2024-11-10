import React, { useState, useEffect } from 'react';
import api from '../../ApiConfig'; // Import your API configuration
import '../../css_styles/TaskManagement.css';
import Header from '../CommonAsset/Header';

const TaskManagement = () => {
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [taskDetails, setTaskDetails] = useState({
    title: '',
    description: '',
    priority: 'LOW',
    deadline: '',
  });

  // Fetch users when the component is mounted
  useEffect(() => {
    fetchUsers();
  }, []);

  // Fetch users from the API
  const fetchUsers = async () => {
    try {
      const response = await api.get('/api/users/');
      setUsers(response.data);
    } catch (error) {
      console.error('Error fetching users:', error);
    }
  };

  // Handle form submission for assigning a task
  const handleTaskSubmit = async (e) => {
    e.preventDefault();

    if (!selectedUser) {
      alert('Please select a user to assign the task');
      return;
    }

    try {
      const taskData = { 
        ...taskDetails, 
        assignedUser: { id: selectedUser.id }, 
        status: 'PENDING'  // Set status to Pending when saving
      };
      
      await api.post('/api/tasks/', taskData, {
        params: { userId: selectedUser.id },
      });

      alert('Task assigned successfully!');
      
      // Clear form fields after successful task assignment
      setTaskDetails({
        title: '',
        description: '',
        priority: 'LOW',
        deadline: '',
      });
      setSelectedUser(null); // Clear selected user
    } catch (error) {
      console.error('Error assigning task:', error);
    }
  };

  return (
    <div>
      <Header />
      <div className="task-management-container">
        <h2 className="task-management-header">Assign Task</h2>
        <form className="task-management-form" onSubmit={handleTaskSubmit}>
          <label className="task-management-label">
            Title:
            <input
              className="task-management-input"
              type="text"
              value={taskDetails.title}
              onChange={(e) => setTaskDetails({ ...taskDetails, title: e.target.value })}
              required
            />
          </label>
          <label className="task-management-label">
            Description:
            <textarea
              className="task-management-textarea"
              value={taskDetails.description}
              onChange={(e) => setTaskDetails({ ...taskDetails, description: e.target.value })}
              required
            />
          </label>
          <label className="task-management-label">
            Priority:
            <select
              className="task-management-select"
              value={taskDetails.priority}
              onChange={(e) => setTaskDetails({ ...taskDetails, priority: e.target.value })}
            >
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </label>
          <label className="task-management-label">
            Deadline:
            <input
              className="task-management-date"
              type="date"
              value={taskDetails.deadline}
              onChange={(e) => setTaskDetails({ ...taskDetails, deadline: e.target.value })}
              required
            />
          </label>
          <label className="task-management-label">
            Assign to:
            <select
              className="task-management-select"
              onChange={(e) => setSelectedUser(users.find(user => user.id === parseInt(e.target.value)))}
              value={selectedUser ? selectedUser.id : ''}
            >
              <option value="">Select User</option>
              {users.map((user) => (
                <option key={user.id} value={user.id}>
                  {user.name}
                </option>
              ))}
            </select>
          </label>
          <button className="task-management-button" type="submit">
            Assign Task
          </button>
        </form>
      </div>
    </div>
  );
};

export default TaskManagement;
