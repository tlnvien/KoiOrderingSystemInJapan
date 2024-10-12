import React, { useState } from "react";
import logo from "./assets/logo.jpg"; // Assuming logo path
import { Link, useNavigate } from "react-router-dom";
import "./ResetPassword.css"; // Create and import the CSS file
import { FaEye, FaEyeSlash } from "react-icons/fa"; // Importing eye icons

const ResetPassword = () => {
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const navigate = useNavigate();

  const toggleNewPasswordVisibility = () => {
    setShowNewPassword(!showNewPassword);
  };

  const toggleConfirmPasswordVisibility = () => {
    setShowConfirmPassword(!showConfirmPassword);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (newPassword === confirmPassword) {
      // Handle the password reset logic here
      console.log("Password successfully reset");
      navigate("/"); // Redirect to login or wherever you want after reset
    } else {
      alert("Mật khẩu không trùng khớp!");
    }
  };

  return (
    <div className="reset-password-container">
      {/* Right Section - Form */}
      <div className="form-section">
        <h1>Đổi mật khẩu</h1>
        <form onSubmit={handleSubmit}>
          <label>Mật khẩu cũ:</label>
          <div className="password-input-container">
            <input
              type={showNewPassword ? "text" : "password"}
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="Mật khẩu cũ"
              required
            />
            <span
              className="password-toggle-icon"
              onClick={toggleNewPasswordVisibility}
            >
              {showNewPassword ? <FaEyeSlash /> : <FaEye />}
            </span>
          </div>
          {/* New Password */}
          <label>Mật khẩu mới:</label>
          <div className="password-input-container">
            <input
              type={showNewPassword ? "text" : "password"}
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="Mật khẩu mới"
              required
            />
            <span
              className="password-toggle-icon"
              onClick={toggleNewPasswordVisibility}
            >
              {showNewPassword ? <FaEyeSlash /> : <FaEye />}
            </span>
          </div>

          {/* Confirm Password */}
          <label>Nhập lại mật khẩu:</label>
          <div className="password-input-container">
            <input
              type={showConfirmPassword ? "text" : "password"}
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Nhập lại mật khẩu mới"
              required
            />
            <span
              className="password-toggle-icon"
              onClick={toggleConfirmPasswordVisibility}
            >
              {showConfirmPassword ? <FaEyeSlash /> : <FaEye />}
            </span>
          </div>

          <button type="submit" className="reset-btn">
            Xác nhận
          </button>
          <Link to="/" className="back-link">
            &lt; Quay lại
          </Link>
        </form>
      </div>
    </div>
  );
};

export default ResetPassword;
