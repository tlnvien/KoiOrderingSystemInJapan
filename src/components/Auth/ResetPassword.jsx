import React, { useState } from "react";
import logo from "./assets/logo.jpg"; // Assuming logo path
import { useNavigate } from "react-router-dom";
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
      alert("Passwords do not match!");
    }
  };

  return (
    <div className="reset-password-container">
      {/* Right Section - Form */}
      <div className="form-section">
        <h2>Reset Your Password</h2>
        <form onSubmit={handleSubmit}>
          {/* New Password */}
          <label>New Password:</label>
          <div className="password-input-container">
            <input
              type={showNewPassword ? "text" : "password"}
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="New Password"
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
          <label>Confirm Password:</label>
          <div className="password-input-container">
            <input
              type={showConfirmPassword ? "text" : "password"}
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Confirm New Password"
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
            Change Password
          </button>
        </form>
      </div>
    </div>
  );
};

export default ResetPassword;
