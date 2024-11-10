import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../css_styles/Login.css';
import api from '../../ApiConfig';
import Header from '../CommonAsset/Header';
import logo from '../../assets/login-logo.avif';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await api.post('/api/v1/auth/login', {
        username,
        password,
      }, {
        headers: {
          'Content-Type': 'application/json'
        },
      });

      localStorage.setItem('token', response.data.token);

      // Fetch user role after login
      const userResponse = await api.get(`/api/users/email/${username}`);

      const userRole = userResponse.data.roles[0].name; // Accessing the first role's name
      
      localStorage.setItem('user_role', userRole);
      localStorage.setItem('user_id', userResponse.data.id);

      if (userRole === 'NORMAL_USER') {
        navigate('/dashboard');
      } else if (userRole === 'ADMIN_USER') {
        navigate('/admin/dashboard');
      }
    } catch (error) {
      setError('Invalid username or password');
      console.error('Error logging in or fetching user role:', error);
    }
  };

  return (
    <div>
      <Header />
      <div className="login-form-container">
      <center><img className='login-logo' src={logo} alt='Logo'/></center>
        <form onSubmit={handleSubmit} className="login-form">
          
          <h2 align="center">Login Now !</h2>
          <div>
            <label>Username:</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          <div>
            <label>Password:</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          {error && <p className="error">{error}</p>}
          <br/>
          <button type="submit">Login</button>
        </form>
      </div>
    </div>
  );
};

export default Login;
