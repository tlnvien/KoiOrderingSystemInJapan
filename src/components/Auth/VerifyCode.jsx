import React, { useState } from "react";
import "./ForgotPassword.css";
import { Link } from "react-router-dom";
import axios from "axios";

const VerifyCode = () => {
  const [verificationCode, setVerificationCode] = useState("");
  const [errorMessage, setErrorMessage] = useState(""); // State for error messages
  const [successMessage, setSuccessMessage] = useState(""); // State for success messages

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post("/api/verify-code", {
        code: verificationCode,
      });
      if (response.data.success) {
        setSuccessMessage("Xác minh thành công!");
        // Redirect to the reset password page or perform the necessary action
      } else {
        setErrorMessage("Mã xác minh không hợp lệ. Vui lòng thử lại.");
      }
    } catch (error) {
      setErrorMessage("Đã xảy ra lỗi. Vui lòng thử lại.");
      console.error("Verification error:", error);
    }
  };

  const handleResendCode = async () => {
    try {
      const response = await axios.post("/api/resend-code", {
        email: "user@example.com",
      }); // Replace with the actual email
      if (response.data.success) {
        setSuccessMessage("Mã xác minh đã được gửi lại!");
      } else {
        setErrorMessage(
          "Đã xảy ra lỗi khi gửi lại mã xác minh. Vui lòng thử lại."
        );
      }
    } catch (error) {
      setErrorMessage(
        "Đã xảy ra lỗi khi gửi lại mã xác minh. Vui lòng thử lại."
      );
      console.error("Resend code error:", error);
    }
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
            onChange={(e) => {
              setVerificationCode(e.target.value);
              setErrorMessage(""); // Clear error message on input change
              setSuccessMessage(""); // Clear success message on input change
            }}
            maxLength="6"
            className="verification-input"
            required
            placeholder="* * * * * *"
          />
          <button type="submit" className="submit-btn">
            Xác minh
          </button>
        </form>
        {errorMessage && <p className="error-message">{errorMessage}</p>}
        {successMessage && <p className="success-message">{successMessage}</p>}

        {/* Resend Code Option */}
        <div className="resend-section">
          <p>Không nhận được mã?</p>
          <button onClick={handleResendCode} className="search-btn">
            Gửi lại mã
          </button>
        </div>
        <Link to="/register" className="back-link">
          Quay lại trang đăng ký
        </Link>
      </div>
    </div>
  );
};

export default VerifyCode;
