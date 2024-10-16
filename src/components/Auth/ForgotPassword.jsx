import React, { useState } from "react";
import "./ForgotPassword.css";
import axios from "axios"; // Import axios for making API calls
import { useNavigate } from "react-router-dom"; // Import useNavigate for navigation

const ForgotPassword = () => {
  const [email, setEmail] = useState(""); // Changed identifier to email
  const [errorMessage, setErrorMessage] = useState(""); // State for error messages
  const [successMessage, setSuccessMessage] = useState(""); // State for success messages
  const navigate = useNavigate(); // Initialize useNavigate hook
  const forgotApi = "http://localhost:8082/api/forgot-password"; // API endpoint for sending the verification code
  const token = localStorage.getItem("token"); // Get token from local storage

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post(forgotApi, { email });

      if (response.status === 200) {
        setSuccessMessage("Mã xác minh đã được gửi!");
        navigate("/reset-password", {
          state: { email },
        });
      } else {
        setErrorMessage("Đã xảy ra lỗi khi gửi mã xác minh. Vui lòng thử lại.");
      }
    } catch (error) {
      setErrorMessage("Đã xảy ra lỗi khi gửi mã xác minh. Vui lòng thử lại.");
      console.error("Send reset code error:", error);
    }
  };

  return (
    <div className="forgot-password-container">
      <div className="form-section">
        <h1>Quên mật khẩu</h1>
        <p>Vui lòng nhập email đã đăng ký tài khoản của bạn.</p>
        <form onSubmit={handleSubmit}>
          <input
            type="email" // Changed to email type for better validation
            className="forgot-password-input"
            value={email} // Use email state instead of identifier
            onChange={(e) => setEmail(e.target.value)} // Set email state
            placeholder="Email"
            required
          />
          <button type="submit" className="submit-btn">
            Gửi mã
          </button>
        </form>
        {errorMessage && <p className="error-message">{errorMessage}</p>}
        {successMessage && <p className="success-message">{successMessage}</p>}
        <div className="help-text">
          <p>Không nhận được mã? Kiểm tra lại thông tin hoặc thử lại sau.</p>
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;
