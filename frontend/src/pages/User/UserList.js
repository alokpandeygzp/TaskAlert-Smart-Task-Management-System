import React, { useState, useEffect } from 'react';
import api from '../../ApiConfig';
import '../../css_styles/UserList.css';

import Header from '../CommonAsset/Header';

const UserList = () => {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');
  const [newUser, setNewUser] = useState({ name: '', email: '', password: '', about: '' });
  const [editUser, setEditUser] = useState(null);
  const [editUserData, setEditUserData] = useState({ name: '', email: '', password: '', about: '' });

  const fetchUsers = async () => {
    try {
      const response = await api.get('/api/users/');
      setUsers(response.data);
      setError(''); // Reset error on successful fetch
    } catch (error) {
      setError('Error fetching users. Please try again later.');
      console.error('Error fetching users:', error);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleAddUser = async (e) => {
    e.preventDefault();
    try {
      const response = await api.post('/api/v1/auth/register', newUser);
      setUsers([...users, response.data]);
      setNewUser({ name: '', email: '', password: '', about: '' });
      setError(''); // Reset error on successful user addition
    } catch (error) {
      setError('Error adding user. Please check the input data.');
      console.error('Error adding user:', error);
    }
  };

  const handleUpdateUser = async (userId) => {
    try {
      const response = await api.put(`/api/users/${userId}`, editUserData);
      setUsers(users.map(user => (user.id === userId ? response.data : user)));
      setEditUser(null);
      setEditUserData({ name: '', email: '', password: '', about: '' });
      setError(''); // Reset error on successful update
    } catch (error) {
      // setError('Error updating user. Please check the input data.');
      if (error.response && error.response.data && error.response.data.password) {
        setError(error.response.data.password); // Assuming the error is in the `password` field
      } else {
        setError('Error updating user. Please check the input data.');
      }
      console.error('Error updating user:', error);
    }
  };

  const handleDeleteUser = async (userId) => {
    try {
      await api.delete(`/api/users/${userId}`);
      setUsers(users.filter(user => user.id !== userId));
      setError(''); // Reset error on successful deletion
    } catch (error) {
      setError('Error deleting user. Please try again later.');
      console.error('Error deleting user:', error);
    }
  };

  return (
    <div>
      <Header />
      <div className="user-list-container">
        <h2>Users</h2>

        {/* Display error message if it exists */}
        {error && <div className="error-message">{error}</div>}


        <form onSubmit={handleAddUser} className="user-form">
          <input
            type="text"
            value={newUser.name}
            onChange={(e) => setNewUser({ ...newUser, name: e.target.value })}
            placeholder="Name"
            required
          />
          <input
            type="email"
            value={newUser.email}
            onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
            placeholder="Email"
            required
          />
          <input
            type="password"
            value={newUser.password}
            onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
            placeholder="Password"
            required
          />
          <input
            type="text"
            value={newUser.about}
            onChange={(e) => setNewUser({ ...newUser, about: e.target.value })}
            placeholder="About"
            required
          />
          <button type="submit">Add</button>
        </form>
        <ul className="user-list">
          {users.map((user) => (
            <li key={user.id} className="user-item">
              {editUser === user.id ? (
                <div className="user-edit">
                  
                  <input
                    type="text"
                    value={editUserData.name}
                    onChange={(e) => setEditUserData({ ...editUserData, name: e.target.value })}
                    required
                  />
                  <input
                    type="email"
                    value={editUserData.email}
                    onChange={(e) => setEditUserData({ ...editUserData, email: e.target.value })}
                    required
                  />
                  <input
                    type="password"
                    value={editUserData.password}
                    onChange={(e) => setEditUserData({ ...editUserData, password: e.target.value })}
                    required
                  />
                  <input
                    type="text"
                    value={editUserData.about}
                    onChange={(e) => setEditUserData({ ...editUserData, about: e.target.value })}
                    required
                  />
                  <button onClick={() => handleUpdateUser(user.id)}>Save</button>
                  <button onClick={() => setEditUser(null)}>Cancel</button>
                </div>
              ) : (
                <div className="user-view">
                  <table>
                  <tbody>
                    <tr>
                      <td><h3>{user.name}</h3></td>
                      <td><p>{user.email}<br /><i>{user.about}</i></p></td>
                      <td>
                        <button onClick={() => {
                          setEditUser(user.id);
                          setEditUserData({ name: user.name, email: user.email, password: '', about: user.about });
                        }}>
                          Edit
                        </button>
                      </td>
                      <td><button onClick={() => handleDeleteUser(user.id)}>Delete</button></td>
                    </tr>
                  </tbody>
                </table>
                </div>
              )}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default UserList;