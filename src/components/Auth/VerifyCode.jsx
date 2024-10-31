import React, { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import "./ForgotPassword.css";

const VerifyCode = () => {
  const location = useLocation();
  const { email } = location.state || {}; // Lấy email từ state nếu có
  const [code, setVerificationCode] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!code) {
      setErrorMessage("Vui lòng nhập mã xác minh.");
      return;
    }

    if (!/^\d{6}$/.test(code)) {
      setErrorMessage("Mã xác minh phải gồm 6 chữ số.");
      return;
    }

    if (!email) {
      setErrorMessage("Không tìm thấy email. Vui lòng thử lại.");
      return;
    }

    try {
      const verifyApi = `register/confirm/${encodeURIComponent(
        email
      )}?code=${code}`;

      const response = await axios.post(verifyApi);

      if (response.status === 200) {
        setSuccessMessage("Xác minh thành công!");

        setTimeout(() => {
          navigate("/login");
        }, 2000);
      } else {
        setErrorMessage("Mã xác minh không hợp lệ. Vui lòng thử lại.");
      }
    } catch (error) {
      setErrorMessage("Đã xảy ra lỗi. Vui lòng thử lại.");
      console.error("Verification error:", error);
    }
  };

  // const handleResendCode = async () => {
  //   const resendApi = "http://localhost:8082/api/register/resend";

  //   try {
  //     const response = await axios.post(resendApi, { email });
  //     if (response.data.success) {
  //       setSuccessMessage("Mã xác minh đã được gửi lại!");
  //     } else {
  //       setErrorMessage("Đã xảy ra lỗi khi gửi mã xác minh. Vui lòng thử lại.");
  //     }
  //   } catch (error) {
  //     setErrorMessage("Đã xảy ra lỗi khi gửi mã xác minh. Vui lòng thử lại.");
  //     console.error("Resend code error:", error);
  //   }
  // };

  return (
    <div className="forgot-password-container">
      <div className="form-section">
        <h1>Nhập mã xác minh</h1>
        <p>Vui lòng nhập mã gồm 6 chữ số được gửi đến email của bạn</p>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            value={code}
            onChange={(e) => {
              setVerificationCode(e.target.value);
              setErrorMessage("");
              setSuccessMessage("");
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
        {errorMessage && (
          <p className="error-message" aria-live="polite">
            {errorMessage}
          </p>
        )}
        {successMessage && (
          <p className="success-message" aria-live="polite">
            {successMessage}
          </p>
        )}

        {/* <p>Không nhận được mã?</p>
        <button onClick={handleResendCode} className="search-btn">
          Gửi lại mã
        </button> */}

        <Link to="/register/customer" className="back-link">
          Quay lại trang đăng ký
        </Link>
      </div>
    </div>
  );
};

export default VerifyCode;
