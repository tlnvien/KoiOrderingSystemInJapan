import React, { useState } from "react";
import "./ForgotPassword.css";
import axios from "axios"; // Import axios for making API calls
import { useNavigate } from "react-router-dom";
import api from "../../config/axios";

const ForgotPassword = () => {
  const [email, setEmail] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await api.post("forgot-password", { email });

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
