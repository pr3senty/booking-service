import React, { useState } from 'react';
import { signUp } from '../api/auth';
import { useNavigate, Link } from 'react-router-dom';
import "./AuthForm.css"

export default function RegisterPage() {
  const [form, setForm] = useState({
    role: 'GUEST',
    surname: '',
    personName: '',
    patronymic: '',
    username: '',
    password: '',
  });

  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const result = await signUp(form);

      if (result && result.jwtToken) {
        localStorage.setItem("jwtToken", result.jwtToken);
        navigate('/');
      }
    } catch (err) {
      setError(`Ошибка при регистрации: ${err.response?.data?.errorMessage}`);
      console.error(err);
    }
  };

  return (
    <div className="auth-container">
      <h2>Регистрация</h2>
      {error && <p className="error">{error}</p>}
      <form onSubmit={handleSubmit}>
        <label>
          Роль:
          <select name="role" value={form.role} onChange={handleChange} required>
            <option value="GUEST">Гость</option>
            <option value="STUDENT">Студент</option>
            <option value="STAFF">Сотрудник</option>
            <option value="ADMIN">Администратор</option>
          </select>
        </label>

        <label>
          Фамилия:
          <input type="text" name="surname" value={form.surname} onChange={handleChange} required />
        </label>

        <label>
          Имя:
          <input type="text" name="personName" value={form.personName} onChange={handleChange} required />
        </label>

        <label>
          Отчество:
          <input type="text" name="patronymic" value={form.patronymic} onChange={handleChange} required />
        </label>

        <label>
          Логин:
          <input type="text" name="username" value={form.username} onChange={handleChange} required />
        </label>

        <label>
          Пароль:
          <input type="password" name="password" value={form.password} onChange={handleChange} required />
        </label>

        <button type="submit">Зарегистрироваться</button>
      </form>

      <div className="link">
        Уже есть аккаунт? <Link to="/login">Войти</Link>
      </div>
    </div>
  );
}

