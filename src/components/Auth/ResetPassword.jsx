import React, { useState } from "react";
import axios from "axios";
import { useLocation, useNavigate } from "react-router-dom";
import "./ResetPassword.css";
import { FaEye, FaEyeSlash } from "react-icons/fa";

const ResetPassword = () => {
  const location = useLocation();
  const { email } = location.state || {};
  const [code, setCode] = useState(""); // New state for the code
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [showPassword, setShowPassword] = useState(false); // State for password visibility
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const navigate = useNavigate();
  const resetApi = "http://localhost:8082/api/reset-password";

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (password !== confirmPassword) {
      setErrorMessage("Mật khẩu không khớp.");
      return;
    }

    try {
      const response = await axios.post(`${resetApi}?requestCode=${code}`, {
        password,
      });

      if (response.status === 200) {
        setSuccessMessage("Mật khẩu đã được đặt lại thành công!");
        setTimeout(() => {
          navigate("/login");
        }, 2000);
      } else {
        setErrorMessage(
          "Đã xảy ra lỗi khi đặt lại mật khẩu. Vui lòng thử lại."
        );
      }
    } catch (error) {
      setErrorMessage("Đã xảy ra lỗi khi đặt lại mật khẩu. Vui lòng thử lại.");
      console.error("Reset password error:", error);
    }
  };

  return (
    <div className="reset-password-container">
      <div className="form-section">
        <h1>Đặt lại mật khẩu</h1>
        <form onSubmit={handleSubmit}>
          {/* New input field for the verification code */}
          <label>Nhập mã xác minh:</label>
          <input
            type="text"
            placeholder="* * * * * *"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            required
          />
          <div className="password-field">
            <label>Mật khẩu mới:</label>
            <input
              type={showPassword ? "text" : "password"}
              placeholder="Mật khẩu mới"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            <span
              className="password-toggle-icon"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? <FaEyeSlash /> : <FaEye />}
            </span>
          </div>
          <div className="password-field">
            <label>Xác nhận mật khẩu mới:</label>
            <input
              type={showConfirmPassword ? "text" : "password"}
              placeholder="Xác nhận mật khẩu mới"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />
            <span
              className="password-toggle-icon"
              onClick={() => setShowConfirmPassword(!showConfirmPassword)}
            >
              {showConfirmPassword ? <FaEyeSlash /> : <FaEye />}
            </span>
          </div>
          <button type="submit" className="submit-btn">
            Đặt lại mật khẩu
          </button>
        </form>
        {errorMessage && <p className="error-message">{errorMessage}</p>}
        {successMessage && <p className="success-message">{successMessage}</p>}
      </div>
    </div>
  );
};

export default ResetPassword;
