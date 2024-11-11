import { useState } from "react";
import "./Login.css";
import logo from "./assets/logo.jpg";
import { Link, useNavigate } from "react-router-dom";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import api from "../../config/axios";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const navigate = useNavigate();

  const handleUsernamePasswordLogin = async (e) => {
    e.preventDefault();

    try {
      const response = await api.post("login", {
        username: username,
        password: password,
      });

      const { token, userId, role } = response.data;

      localStorage.setItem("token", token);
      localStorage.setItem("userId", userId);
      localStorage.setItem("role", role);
      localStorage.setItem("loginType", "username");
      alert("Đăng nhập thành công!");

      if (role === "MANAGER") {
        navigate("/admin");
      } else if (role === "CUSTOMER") {
        navigate("/");
      } else if (role === "FARM_HOST") {
        navigate("/farm-host");
      } else if (role === "SALES") {
        navigate("/dashboard/sale");
      } else if (role === "CONSULTING") {
        navigate("/dashboard/consulting");
      } else if (role === "DELIVERING") {
        navigate("/dashboard/delivering");
      } else {
        navigate("/");
      }
    } catch (error) {
      console.error("Lỗi đăng nhập", error);
      alert("Tên người dùng hoặc mật khẩu sai");
    }
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  return (
    <div className="auth-container">
      <div className="login-section">
        <h1 className="heading">Đăng nhập</h1>
        <form onSubmit={handleUsernamePasswordLogin}>
          <div className="form-group">
            <label>Tên đăng nhập:</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label>Mật khẩu:</label>
            <div className="password-input-container">
              <input
                type={showPassword ? "text" : "password"}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              <span
                className="password-toggle-icon"
                onClick={togglePasswordVisibility}
              >
                {showPassword ? <FaEyeSlash /> : <FaEye />}
              </span>
            </div>
          </div>
          <button type="submit" className="auth-btn">
            Đăng nhập
          </button>
        </form>
        <div className="auth-links">
          <Link to="/forgot-password" className="auth-link">
            Quên mật khẩu?
          </Link>
          <Link to="/register/customer" className="auth-link1">
            Bạn không có tài khoản? Đăng ký ngay
          </Link>
        </div>
      </div>
    </div>
  );
}

export default Login;
