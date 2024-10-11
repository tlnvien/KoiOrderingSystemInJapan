import React, { useState } from "react";
import "./ForgotPassword.css";

const ForgotPassword = () => {
  const [verificationCode, setVerificationCode] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    // Thêm logic xử lý xác minh mã code
    console.log("Verifying code:", verificationCode);
  };

  const handleResendCode = () => {
    // Logic gửi lại mã xác minh
    console.log("Resending verification code");
  };

  return (
    <div className="forgot-password-container">
      {/* Verification Form Section */}
      <div className="form-section">
        <h2>Nhập mã xác minh</h2>
        <p>Vui lòng nhập mã gồm 6 chữ số được gửi đến email của bạn</p>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            value={verificationCode}
            onChange={(e) => setVerificationCode(e.target.value)}
            maxLength="6"
            className="verification-input"
            required
            placeholder="* * * * * *"
          />
          <button type="submit" className="submit-btn">
            Verify
          </button>
        </form>

        {/* Resend Code Option */}
        <div className="resend-section">
          <p>Không nhận được mã?</p>
          <button onClick={handleResendCode} className="search-btn">
            Gửi lại mã
          </button>
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;
