import React, { useState } from "react";
import "./ForgotPassword.css";
import { Navigate } from "react-router-dom";

const ForgotPassword = () => {
  const [identifier, setIdentifier] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    // Thêm logic gửi mã xác minh qua email/điện thoại
    console.log("Sending reset code to:", identifier);
    Navigate("/verify-code");
  };

  return (
    <div className="forgot-password-container">
      <div className="form-section">
        <h2>Quên mật khẩu</h2>
        <p>Vui lòng nhập email đã đăng ký tài khoản của bạn.</p>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            value={identifier}
            onChange={(e) => setIdentifier(e.target.value)}
            placeholder="Email"
            required
          />
          <button type="submit" className="submit-btn">
            Gửi mã
          </button>
        </form>
        <div className="help-text">
          <p>Không nhận được mã? Kiểm tra lại thông tin hoặc thử lại sau.</p>
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;
