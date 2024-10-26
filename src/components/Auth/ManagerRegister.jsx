import React, { useState } from "react";
import { GoogleLogin } from "@react-oauth/google";
import FacebookLogin from "react-facebook-login/dist/facebook-login-render-props";
import { jwtDecode } from "jwt-decode";
import facebookLogo from "./assets/facebook-logo.png";
import logo from "./assets/logo.jpg";
import "./Auth.css";
import { Link, useNavigate } from "react-router-dom";
import { FaEye, FaEyeSlash } from "react-icons/fa";

const Register = () => {
  const [formData, setFormData] = useState({
    email: "",
    username: "",
    password: "",
    confirmPassword: "",
    phone: "",
  });
  const [errors, setErrors] = useState({});
  const [agreeToTerms, setAgreeToTerms] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const navigate = useNavigate();
  const registerApi = "http://localhost:8082/api/register/manager";
  const token = localStorage.getItem("token");

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleCheckboxChange = (e) => {
    setAgreeToTerms(e.target.checked);
  };

  const validateFullName = (value) => {
    if (!value) return "Họ và tên không được để trống";
    if (value.trimStart().length !== value.length)
      return "Ký tự đầu tiên không được có khoảng trắng";
    if (/[\d]/.test(value)) return "Không được phép có số";
    if (/[^a-zA-Z\s]/.test(value)) return "Không được phép có ký tự đặc biệt";
    return "";
  };

  const validateUsername = (value) => {
    if (!value) return "Tên đăng nhập không được để trống";
    if (value.trimStart().length !== value.length)
      return "Ký tự đầu tiên không được có khoảng trắng";
    if (value.length < 3) return "Tên đăng nhập phải có ít nhất 3 ký tự";
    if (value.length > 20) return "Tên đăng nhập không được vượt quá 20 ký tự";
    if (/[^a-zA-Z0-9]/.test(value))
      return "Tên đăng nhập chỉ được chứa chữ cái và số";
    return "";
  };

  const validatePhoneNumber = (value) => {
    if (!value) return "Số điện thoại không được để trống";
    if (/[^0-9]/.test(value)) return "Không được phép có ký tự";
    if (value.trimStart().length !== value.length)
      return "Ký tự đầu tiên không được có khoảng trắng";
    return "";
  };

  const validateEmail = (value) => {
    if (!value) return "Email không được để trống";
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regex.test(value)) return "Email không hợp lệ";
    if (value.trimStart().length !== value.length)
      return "Ký tự đầu tiên không được có khoảng trắng";
    return "";
  };

  const validatePassword = (value) => {
    if (!value) return "Mật khẩu không được để trống";
    if (value.length < 6) return "Mật khẩu phải có ít nhất 6 ký tự";
    return "";
  };

  const validateConfirmPassword = (password, confirmPassword) => {
    if (confirmPassword !== password) return "Mật khẩu không khớp!";
    return "";
  };

  const handleBlur = (e) => {
    const { name, value } = e.target;
    let error = "";

    switch (name) {
      case "phone":
        error = validatePhoneNumber(value);
        break;
      case "email":
        error = validateEmail(value);
        break;
      case "password":
        error = validatePassword(value);
        break;
      case "confirmPassword":
        error = validateConfirmPassword(formData.password, value);
        break;
      case "username":
        error = validateUsername(value);
        break;
      default:
        break;
    }

    if (error) {
      setErrors((prevErrors) => ({
        ...prevErrors,
        [name]: error,
      }));
    } else {
      setErrors((prevErrors) => {
        const { [name]: _, ...rest } = prevErrors;
        return rest;
      });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!agreeToTerms) {
      alert("Bạn phải đồng ý với các điều khoản và điều kiện.");
      return;
    }

    if (Object.keys(errors).length > 0) {
      alert("Vui lòng sửa các lỗi trước khi gửi.");
      return;
    }

    try {
      const response = await fetch(registerApi, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        navigate("/login", {});
      } else {
        alert("Đăng ký thất bại. Vui lòng thử lại.");
      }
    } catch (error) {
      console.error("Lỗi khi đăng ký:", error);
      alert("Đã xảy ra lỗi. Vui lòng thử lại sau.");
    }
  };

  const handleGoogleLoginSuccess = (credentialResponse) => {
    const decoded = jwtDecode(credentialResponse.credential);
    console.log("Người dùng Google:", decoded);
  };

  const handleGoogleLoginFailure = (error) => {
    console.error("Đăng nhập Google thất bại:", error);
  };

  const handleFacebookLogin = (response) => {
    if (response.accessToken) {
      console.log("Người dùng Facebook:", response);
    } else {
      console.error("Đăng nhập Facebook thất bại");
    }
  };

  return (
    <div className="register-container">
      <div className="form-container">
        <h1 className="heading">Đăng ký</h1>
        <div className="form-section-register">
          <form onSubmit={handleSubmit}>
            <label>Email:</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              onBlur={handleBlur}
              required
            />
            <div className="error-container">
              {errors.email && <span className="error">{errors.email}</span>}
            </div>

            <label>Tên đăng nhập:</label>
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              onBlur={handleBlur}
              required
            />
            <div className="error-container">
              {errors.username && (
                <span className="error">{errors.username}</span>
              )}
            </div>
            <label>Số điện thoại:</label>
            <input
              type="tel"
              name="phone"
              value={formData.phone}
              onChange={handleChange}
              onBlur={handleBlur}
              required
            />
            <div className="error-container">
              {errors.phone && <span className="error">{errors.phone}</span>}
            </div>

            <label>Mật khẩu:</label>
            <div className="password-field">
              <input
                type={showPassword ? "text" : "password"}
                name="password"
                value={formData.password}
                onChange={handleChange}
                onBlur={handleBlur}
                required
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="toggle-password-btn"
              >
                {showPassword ? <FaEyeSlash /> : <FaEye />}
              </button>
            </div>
            <div className="error-container">
              {errors.password && (
                <span className="error">{errors.password}</span>
              )}
            </div>

            <label>Nhập lại mật khẩu:</label>
            <div className="password-field">
              <input
                type={showConfirmPassword ? "text" : "password"}
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                onBlur={handleBlur}
                required
              />
              <button
                type="button"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                className="toggle-password-btn"
              >
                {showConfirmPassword ? <FaEyeSlash /> : <FaEye />}
              </button>
            </div>
            <div className="error-container">
              {errors.confirmPassword && (
                <span className="error">{errors.confirmPassword}</span>
              )}
            </div>

            <div className="terms-container">
              <input
                type="checkbox"
                id="agreeToTerms"
                checked={agreeToTerms}
                onChange={handleCheckboxChange}
              />
              <label htmlFor="agreeToTerms">
                Tôi đồng ý với điều khoản và chính sách sử dụng
              </label>
            </div>
            <button type="submit" className="auth-btn">
              Đăng ký
            </button>
            <div className="auth-links">
              <Link to="/login" className="auth-link1">
                Bạn đã có tài khoản? Đăng nhập ngay
              </Link>
            </div>
          </form>
        </div>
        <div className="social-login-section">
          <div className="or-login">
            <p>Hoặc đăng nhập bằng</p>
          </div>
          <div className="social-login1">
            <GoogleLogin
              onSuccess={handleGoogleLoginSuccess}
              onError={handleGoogleLoginFailure}
              useOneTap
            />
            <FacebookLogin
              appId="875093550843749"
              callback={handleFacebookLogin}
              render={(renderProps) => (
                <button onClick={renderProps.onClick} className="social-btn1">
                  <span className="social-text1">Facebook</span>
                  <img
                    src={facebookLogo}
                    alt="Facebook Logo"
                    className="social-logo1"
                  />
                </button>
              )}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;
