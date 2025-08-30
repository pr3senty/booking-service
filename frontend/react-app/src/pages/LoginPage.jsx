import React, { useState, useEffect } from 'react';
import { signIn } from '../api/auth';
import { useNavigate, Link } from 'react-router-dom';
import "./AuthForm.css"

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const msg = localStorage.getItem('authMessage');
    if (msg) {
      setMessage(msg);
      localStorage.removeItem('authMessage'); 
    }
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setMessage('');

    try {
      const result = await signIn(username, password);

      if (result && result.jwtToken) {
        localStorage.setItem('jwtToken', result.jwtToken);
        navigate('/');
      } else {
        setError("Ошибка входа!");
      }
    } catch (err) {
      console.error(err);
      if (err.response) {
        const status = err.response.status;
        if (status === 404) {
          setError('Пользователь с таким логином не существует!');
        } else if (status === 400) {
          setError('Неправильный пароль!');
        } else {
          setError('Произошла внутренняя ошибка!');
        }
      } else {
        setError('Произошла внутренняя ошибка!');
      }
    }
  };

  return (
    <div className="auth-container">
      <h2>Вход</h2>

      {message && <p className="notification">{message}</p>}

      {error && <p className="error">{error}</p>}

      <form onSubmit={handleSubmit}>
        <label>
          Логин:
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </label>

        <label>
          Пароль:
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>

        <button type="submit">Войти</button>
      </form>

      <div className="link">
        Нет аккаунта? <Link to="/register">Зарегистрироваться</Link>
      </div>
    </div>
  );
}
