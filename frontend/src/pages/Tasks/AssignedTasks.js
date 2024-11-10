import React, { useState, useEffect } from 'react';
import api from '../../ApiConfig';
import Header from '../CommonAsset/Header';
import '../../css_styles/AssignedTasks.css';

const AssignedTasks = () => {
  const [tasks, setTasks] = useState([]);
  const userId = localStorage.getItem('user_id'); // Get userId from local storage

  // Fetch the user's tasks
  useEffect(() => {
    const fetchData = async () => {
      if (!userId) return; // Ensure there's a userId

      try {
        const tasksResponse = await api.get('http://localhost:8080/api/tasks/');
        console.log('Fetched tasks:', tasksResponse.data);

        // Filter tasks assigned to the user
        const userTasks = tasksResponse.data.filter(task => {
          if (task.assignedUser) {
            return String(task.assignedUser.id) === String(userId); // Convert both to string for comparison
          }
          return false; // Handle tasks without assignedUser
        });

        setTasks(userTasks);
      } catch (error) {
        console.error('Error fetching tasks:', error);
      }
    };

    fetchData();
  }, [userId]);


  const handleMarkAsCompleted = async (taskId) => {
    try {
      const taskToUpdate = tasks.find(task => task.id === taskId);

      if (!taskToUpdate || !taskToUpdate.assignedUser || !taskToUpdate.assignedUser.id) {
        console.error("Invalid task or missing assigned user data");
        return;
      }

      const updatedTask = {
        status: 'COMPLETED',
        user_id: taskToUpdate.assignedUser.id,
        deadline: taskToUpdate.deadline,
        description: taskToUpdate.description,
        priority: taskToUpdate.priority,
        title: taskToUpdate.title,
      };

      const response = await api.put(
        `http://localhost:8080/api/tasks/${taskId}?userId=${userId}`,
        updatedTask
      );

      console.log('Task updated:', response.data);

      setTasks(prevTasks =>
        prevTasks.map(task =>
          task.id === taskId ? { ...task, status: 'COMPLETED' } : task
        )
      );
    } catch (error) {
      console.error('Error marking task as completed:', error);
    }
  };

  return (
    <div>
      <Header />
      <div className="assigned-tasks-container">
        <h2>Your Assigned Tasks</h2>

       
        {/* Display Tasks */}
        {tasks.length > 0 ? (
          <table>
            <thead>
              <tr>
                <th>S No.</th>
                <th>Title</th>
                <th>Description</th>
                <th>Status</th>
                <th>Priority</th>
                <th>Deadline</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {tasks.map((task, index) => (
                <tr key={task.id}>
                  <td>{index + 1}</td>
                  <td>{task.title}</td>
                  <td>{task.description}</td>
                  <td>{task.status}</td>
                  <td>{task.priority}</td>
                  <td>{new Date(task.deadline).toLocaleDateString('en-US')}</td>
                  <td>
                    {task.status !== 'COMPLETED' ? (
                      <button
                        className="mark-completed-btn"
                        onClick={() => handleMarkAsCompleted(task.id)}
                      >
                        Mark as Completed
                      </button>
                    ) : (
                      <span className="completed-status">Completed</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p>No tasks assigned yet.</p>
        )}
      </div>
    </div>
  );
};

export default AssignedTasks;
