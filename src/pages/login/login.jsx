import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import axios from "axios";
import "./Login.css";
import api from "../../config/axios";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const navigate = useNavigate();
  const apiUrl = "login";

  const handleUsernamePasswordLogin = async (e) => {
    e.preventDefault();

    try {
      const response = await api.post(apiUrl, {
        username: username, // Payload with username
        password: password, // Payload with password
      });

      // Assuming response data contains the token, userId, and role
      localStorage.setItem("userId", response.data.userId);
      localStorage.setItem("role", response.data.role);
      localStorage.setItem("loginType", "username");

      const { role, token } = response.data;
      localStorage.setItem("token", response.data.token);

      if (role === "SALES") {
        navigate("/dashboard/sale");
      } else if (role === "CONSULTING") {
        navigate("/dashboard/consulting");
      }
    } catch (error) {
      console.error("Login error", error);
      alert("Invalid username or password");
    }
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  return (
    <div className="auth-container">
      <div className="login-section">
        <h1 className="heading">Login</h1>
        <form onSubmit={handleUsernamePasswordLogin}>
          <div className="form-group">
            <label>Username:</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label>Password:</label>
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
            Login
          </button>
        </form>
        <div className="auth-links">
          <Link to="/forgot-password" className="auth-link">
            Forgot Password?
          </Link>
          <Link to="/register" className="auth-link1">
            Do not have an account? Sign up now
          </Link>
        </div>
      </div>
    </div>
  );
}

export default Login;
