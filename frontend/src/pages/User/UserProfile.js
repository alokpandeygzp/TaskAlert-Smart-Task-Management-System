import React, { useState, useEffect, useCallback } from 'react';
import api from '../../ApiConfig';
import '../../css_styles/Profile.css';
import Header from '../CommonAsset/Header';

const Profile = () => {
  const [user, setUser] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [error, setError] = useState('');
  const [editData, setEditData] = useState({ password: '', about: '' });

  const userId = localStorage.getItem('user_id');

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
  }, [fetchUser]);

  const handleEditClick = () => {
    setIsEditing(true);
    setEditData({ password: '', about: user.about });
  };

  const handleSaveClick = async () => {
    try {
      await api.put(`/api/users/${userId}`, {
        name: user.name,
        email: user.email,
        password: editData.password,
        about: editData.about
      });
      setUser({ ...user, about: editData.about });
      setIsEditing(false);
      setError(''); // Reset the error message when the update is successful
    } catch (error) {
      // Set the error state with the message from the backend
      if (error.response && error.response.data && error.response.data.password) {
        setError(error.response.data.password); // Assuming the error is in the `password` field
      } else {
        setError('An error occurred while updating the profile.');
      }
    }
  };
  

  const handleCancelClick = () => {
    setIsEditing(false);
    setEditData({ password: '', about: user.about });
  };

  if (!user) return <div>Loading...</div>;

  return (
    <div>
      <Header />
      <div className="profile-container">
        <h2>My Profile</h2>
        
        {/* Display error message */}
        {error && <div className="error-message">{error}</div>} 


        <div className="profile-details">
          <p><strong>Name:</strong> {user.name}</p>
          <p><strong>Email:</strong> {user.email}</p>
          <p><strong>Role:</strong> {user.roles[0].name}</p>

          {isEditing ? (
            <div className="profile-edit">
              <div className="user-form">
                <input
                  type="password"
                  value={editData.password}
                  onChange={(e) => setEditData({ ...editData, password: e.target.value })}
                  placeholder="Password"
                />
                <input
                  type="text"
                  value={editData.about}
                  onChange={(e) => setEditData({ ...editData, about: e.target.value })}
                  placeholder="About"
                />
                <div className="button-container">
                  <button onClick={handleSaveClick}>Save</button>
                  <button onClick={handleCancelClick}>Cancel</button>
                </div>
              </div>
            </div>
          ) : (
            <div className="profile-edit">
              <p><strong>About:</strong> {user.about}</p>
              <button className="edit-btn"  onClick={handleEditClick}>Edit</button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Profile;
