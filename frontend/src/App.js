// src/App.js
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
// In your index.js or App.js
import '@fortawesome/fontawesome-free/css/all.min.css';

import Login from './pages/Authentication/Login';
import Register from './pages/Authentication/Register';
import Home from './pages/Authentication/Home';
import PrivateRoute from './PrivateRoute';

import AdminDashboard from './pages/AdminDashboard';
import UserDashboard from './pages/UserDashboard';

import UserList from './pages/User/UserList';
import UserProfile from './pages/User/UserProfile';
import TaskManagementAdd from './pages/Tasks/TaskManagementAdd';
import TaskManagementView from './pages/Tasks/TaskManagementView';
import AssignedTasks from './pages/Tasks/AssignedTasks';

function App() {
  return (
    <Router>
      <div className="App">
        <main className="App-main">
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/admin/dashboard" element={<PrivateRoute element={<AdminDashboard />} />} />
        <Route path="/dashboard" element={<PrivateRoute element={<UserDashboard />} />} />

      
        <Route path="/users" element={<PrivateRoute element={<UserList />} />} />
        <Route path="/myProfile" element={<PrivateRoute element={<UserProfile />} />} />
        
        <Route path="/tasks" element={<PrivateRoute element={<TaskManagementAdd />} />} />
        <Route path="/tasksview" element={<PrivateRoute element={<TaskManagementView />} />} />
        <Route path="/assignedTasks" element={<PrivateRoute element={<AssignedTasks />} />} />

      </Routes>
      </main>
      </div>
    </Router>
  );
}

export default App;
