// src/screens/Register.js
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../ApiConfig';
import '../../css_styles/Register.css';
import Header from '../CommonAsset/Header';
import logo from '../../assets/register-logo.avif';

const Register = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [about, setAbout] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/api/v1/auth/register', { name, email, password, about });
      navigate('/login');
    } catch (error) {
      setError('Error registering user');
    }
  };

  return (
    <div>
      
    <Header />
    <div className='manage-items'>
      <div className='login-wala-div'>
      <center><img className='login-logo' src={logo} alt='Logo'/></center>
      </div>
    <div className="form-container">
      <h2>Register Now</h2>
      
      {error && <p className="error">{error}</p>}
      <form onSubmit={handleSubmit}>
        <label htmlFor="name">Name</label>
        <input
          type="text"
          id="name"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
        <label htmlFor="email">Email</label>
        <input
          type="email"
          id="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <label htmlFor="password">Password</label>
        <input
          type="password"
          id="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <label htmlFor="about">About</label>
        <textarea
          id="about"
          value={about}
          onChange={(e) => setAbout(e.target.value)}
        />
        <button type="submit">Register</button>
      </form>
    </div>
    </div>
    </div>
  );
};

export default Register;
