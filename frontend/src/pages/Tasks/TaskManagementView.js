import React, { useState, useEffect } from 'react';
import api from '../../ApiConfig'; // Import your API configuration
import '../../css_styles/TaskManagementView.css';
import Header from '../CommonAsset/Header';

const TaskManagement = () => {
  const [tasks, setTasks] = useState([]);
  const [searchTitle, setSearchTitle] = useState('');
  const [searchDescription, setSearchDescription] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [filterPriority, setFilterPriority] = useState('');
  const [searchFilterType, setSearchFilterType] = useState(''); // Store selected search/filter type

  // Fetch tasks when the component is mounted
  useEffect(() => {
    fetchTasks();
  }, []);

  // Fetch all tasks from the API
  const fetchTasks = async () => {
    try {
      const response = await api.get('/api/tasks/');
      setTasks(response.data);
    } catch (error) {
      console.error('Error fetching tasks:', error);
    }
  };

  // Handle search by title
  const handleSearchByTitle = async () => {
    if (searchTitle) {
      try {
        const response = await api.get(`/api/tasks/searchByTitle?title=${searchTitle}`);
        setTasks(response.data);
      } catch (error) {
        console.error('Error searching tasks by title:', error);
      }
    } else {
      fetchTasks(); // Reset to all tasks if title is empty
    }
  };

  // Handle search by description
  const handleSearchByDescription = async () => {
    if (searchDescription) {
      try {
        const response = await api.get(`/api/tasks/searchByDesc?description=${searchDescription}`);
        setTasks(response.data);
      } catch (error) {
        console.error('Error searching tasks by description:', error);
      }
    } else {
      fetchTasks(); // Reset to all tasks if description is empty
    }
  };

  // Handle filter by status
  const handleFilterByStatus = async () => {
    if (filterStatus) {
      try {
        const response = await api.get(`/api/tasks/filterByStatus?status=${filterStatus}`);
        setTasks(response.data);
      } catch (error) {
        console.error('Error filtering tasks by status:', error);
      }
    } else {
      fetchTasks(); // Reset to all tasks if status is empty
    }
  };

  // Handle filter by priority
  const handleFilterByPriority = async () => {
    if (filterPriority) {
      try {
        const response = await api.get(`/api/tasks/filterByPriority?priority=${filterPriority}`);
        setTasks(response.data);
      } catch (error) {
        console.error('Error filtering tasks by priority:', error);
      }
    } else {
      fetchTasks(); // Reset to all tasks if priority is empty
    }
  };

  // Handle task deletion
  const handleDeleteTask = async (taskId) => {
    try {
      await api.delete(`/api/tasks/${taskId}`);
      alert('Task deleted successfully!');
      fetchTasks(); // Refresh the task list after deletion
    } catch (error) {
      console.error('Error deleting task:', error);
      alert('Failed to delete the task.');
    }
  };

  return (
    <div>
      <Header />
      <div className="task-management-container">
        <div className="task-management-header">
          <h2>Assigned Tasks</h2>
        </div>

        {/* Search and Filter Section */}
        <div className="search-filter-section">
          <div className="select-box">
            <select
              className="search-filter-select"
              onChange={(e) => setSearchFilterType(e.target.value)}
              value={searchFilterType}
            >
              <option value="">Filter/Search by</option>
              <option value="searchByTitle">Title</option>
              <option value="searchByDescription">Description</option>
              <option value="filterByStatus">Status</option>
              <option value="filterByPriority">Priority</option>
            </select>
          </div>

          {/* Conditionally render search/filter inputs based on selected type */}
          {searchFilterType === 'searchByTitle' && (
            <div className="search-box">
              <input
                className="search-input"
                type="text"
                placeholder="Enter Title"
                value={searchTitle}
                onChange={(e) => setSearchTitle(e.target.value)}
              />
              <button className="search-btn" onClick={handleSearchByTitle}>Search</button>
            </div>
          )}

          {searchFilterType === 'searchByDescription' && (
            <div className="search-box">
              <input
                className="search-input"
                type="text"
                placeholder="Enter Description"
                value={searchDescription}
                onChange={(e) => setSearchDescription(e.target.value)}
              />
              <button className="search-btn" onClick={handleSearchByDescription}>Search</button>
            </div>
          )}

          {searchFilterType === 'filterByStatus' && (
            <div className="filter-box">
              <select
                className="filter-select"
                onChange={(e) => setFilterStatus(e.target.value)}
                value={filterStatus}
              >
                <option value="">Select Status</option>
                <option value="PENDING">Pending</option>
                <option value="COMPLETED">Completed</option>
              </select>
              <button className="apply-filter-btn" onClick={handleFilterByStatus}>Apply</button>
            </div>
          )}

          {searchFilterType === 'filterByPriority' && (
            <div className="filter-box">
              <select
                className="filter-select"
                onChange={(e) => setFilterPriority(e.target.value)}
                value={filterPriority}
              >
                <option value="">Select Priority</option>
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </select>
              <button className="apply-filter-btn" onClick={handleFilterByPriority}>Apply</button>
            </div>
          )}
        </div>

        {/* Display Tasks */}
        {tasks.length > 0 ? (
          <table className="task-management-table">
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
                    <button
                      className="delete-button-task"
                      onClick={() => handleDeleteTask(task.id)}
                    >
                      Delete
                    </button>
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

export default TaskManagement;






// import React, { useState, useEffect } from 'react';
// import api from '../../ApiConfig'; // Import your API configuration
// import '../../css_styles/TaskManagementView.css';
// import Header from '../CommonAsset/Header';

// const TaskManagement = () => {
//   const [tasks, setTasks] = useState([]);
//   const [searchTitle, setSearchTitle] = useState('');
//   const [searchDescription, setSearchDescription] = useState('');
//   const [filterStatus, setFilterStatus] = useState('');
//   const [filterPriority, setFilterPriority] = useState('');

//   // Fetch tasks when the component is mounted
//   useEffect(() => {
//     fetchTasks();
//   }, []);

//   // Fetch all tasks from the API
//   const fetchTasks = async () => {
//     try {
//       const response = await api.get('/api/tasks/');
//       setTasks(response.data);
//     } catch (error) {
//       console.error('Error fetching tasks:', error);
//     }
//   };

//   // Search tasks by title
//   const handleSearchByTitle = async () => {
//     if (searchTitle) {
//       try {
//         const response = await api.get(`/api/tasks/searchByTitle?title=${searchTitle}`);
//         setTasks(response.data);
//       } catch (error) {
//         console.error('Error searching tasks by title:', error);
//       }
//     } else {
//       fetchTasks(); // Reset to all tasks if title is empty
//     }
//   };

//   // Search tasks by description
//   const handleSearchByDescription = async () => {
//     if (searchDescription) {
//       try {
//         const response = await api.get(`/api/tasks/searchByDesc?description=${searchDescription}`);
//         setTasks(response.data);
//       } catch (error) {
//         console.error('Error searching tasks by description:', error);
//       }
//     } else {
//       fetchTasks(); // Reset to all tasks if description is empty
//     }
//   };

//   // Filter tasks by status
//   const handleFilterByStatus = async () => {
//     if (filterStatus) {
//       try {
//         const response = await api.get(`/api/tasks/filterByStatus?status=${filterStatus}`);
//         setTasks(response.data);
//       } catch (error) {
//         console.error('Error filtering tasks by status:', error);
//       }
//     } else {
//       fetchTasks(); // Reset to all tasks if status is empty
//     }
//   };

//   // Filter tasks by priority
//   const handleFilterByPriority = async () => {
//     if (filterPriority) {
//       try {
//         const response = await api.get(`/api/tasks/filterByPriority?priority=${filterPriority}`);
//         setTasks(response.data);
//       } catch (error) {
//         console.error('Error filtering tasks by priority:', error);
//       }
//     } else {
//       fetchTasks(); // Reset to all tasks if priority is empty
//     }
//   };

//   // Handle task deletion
//   const handleDeleteTask = async (taskId) => {
//     try {
//       await api.delete(`/api/tasks/${taskId}`);
//       alert('Task deleted successfully!');
//       fetchTasks(); // Refresh the task list after deletion
//     } catch (error) {
//       console.error('Error deleting task:', error);
//       alert('Failed to delete the task.');
//     }
//   };

//   return (
//     <div>
//       <Header />
//       <div className="task-management-container">
//         <div className="task-management-header">
//           <h2>Check Assigned Tasks</h2>
//         </div>

//         {/* Search and Filter Section */}
//         <div className="search-filter-section">
//           {/* Search Box */}
//           <div className="search-box">
//             <input
//               className="search-input"
//               type="text"
//               placeholder="Search by Title"
//               value={searchTitle}
//               onChange={(e) => setSearchTitle(e.target.value)}
//             />
//             <input
//               className="search-input"
//               type="text"
//               placeholder="Search by Description"
//               value={searchDescription}
//               onChange={(e) => setSearchDescription(e.target.value)}
//             />
//             <button className="search-btn" onClick={handleSearchByTitle}>Search by Title</button>
//             <button className="search-btn" onClick={handleSearchByDescription}>Search by Description</button>
//           </div>

//           {/* Filter Box */}
//           <div className="filter-box">
//             <select
//               className="filter-select"
//               onChange={(e) => setFilterStatus(e.target.value)}
//               value={filterStatus}
//             >
//               <option value="">Filter by Status</option>
//               <option value="PENDING">Pending</option>
//               <option value="COMPLETED">Completed</option>
//             </select>

//             <select
//               className="filter-select"
//               onChange={(e) => setFilterPriority(e.target.value)}
//               value={filterPriority}
//             >
//               <option value="">Filter by Priority</option>
//               <option value="LOW">Low</option>
//               <option value="MEDIUM">Medium</option>
//               <option value="HIGH">High</option>
//             </select>

//             <button className="apply-filter-btn" onClick={handleFilterByStatus}>Apply Status Filter</button>
//             <button className="apply-filter-btn" onClick={handleFilterByPriority}>Apply Priority Filter</button>
//           </div>
//         </div>

//         {/* Display Tasks */}
//         {tasks.length > 0 ? (
//           <table className="task-management-table">
//             <thead>
//               <tr>
//                 <th>S No.</th>
//                 <th>Title</th>
//                 <th>Description</th>
//                 <th>Status</th>
//                 <th>Priority</th>
//                 <th>Deadline</th>
//                 <th>Action</th>
//               </tr>
//             </thead>
//             <tbody>
//               {tasks.map((task, index) => (
//                 <tr key={task.id}>
//                   <td>{index + 1}</td>
//                   <td>{task.title}</td>
//                   <td>{task.description}</td>
//                   <td>{task.status}</td>
//                   <td>{task.priority}</td>
//                   <td>{new Date(task.deadline).toLocaleDateString('en-US')}</td>
//                   <td>
//                     <button
//                       className="delete-button-task"
//                       onClick={() => handleDeleteTask(task.id)}
//                     >
//                       Delete
//                     </button>
//                   </td>
//                 </tr>
//               ))}
//             </tbody>
//           </table>
//         ) : (
//           <p>No tasks assigned yet.</p>
//         )}
//       </div>
//     </div>
//   );
// };

// export default TaskManagement;
