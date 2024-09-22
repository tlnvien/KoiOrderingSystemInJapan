import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import "./Profile.css";

const UpdateProfile = ({ user, updateUser }) => {
  const [username, setUsername] = useState(user.username);
  const [email, setEmail] = useState(user.email);
  const [yearOfBirth, setYearOfBirth] = useState(user.yearOfBirth);
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    // Cập nhật thông tin người dùng
    updateUser({ username, email, yearOfBirth });
    navigate("/view-profile"); // Quay lại trang xem profile
  };

  return (
    <div className="update-profile-container">
      <Header />
      <div className="update-profile-content">
        <h1>Update Profile</h1>
        <form onSubmit={handleSubmit} className="update-profile-form">
          <label>
            Username:
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </label>
          <label>
            Email:
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </label>
          <label>
            Year of Birth:
            <input
              type="number"
              value={yearOfBirth}
              onChange={(e) => setYearOfBirth(e.target.value)}
              required
            />
          </label>
          <button type="submit" className="update-button">
            Update
          </button>
        </form>
      </div>
      <Footer />
    </div>
  );
};

export default UpdateProfile;
