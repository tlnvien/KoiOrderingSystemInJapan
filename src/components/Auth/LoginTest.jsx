import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import "./Login.css";
import logo from "./assets/logo.jpg";
import React from "react";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [errorMessage, setErrorMessage] = useState(""); // State to hold error message

  const navigate = useNavigate();

  const handleUsernamePasswordLogin = (e) => {
    e.preventDefault();

    // Clear previous error message
    setErrorMessage("");

    // Username/password login logic
    fetch("https://66e1d268c831c8811b5672e8.mockapi.io/User")
      .then((res) => {
        if (!res.ok) {
          throw new Error("Network response was not ok");
        }
        return res.json();
      })
      .then((users) => {
        const user = users.find(
          (u) => u.username === username && u.password === password
        );
        if (user) {
          localStorage.setItem("token", user.token);
          localStorage.setItem("userId", user.id);
          localStorage.setItem("loginType", "username");
          alert("Login successful!");
          navigate("/");
        } else {
          // setErrorMessage("Invalid username or password");
          alert("Invalid username or password");
        }
      })
      .catch((error) => {
        console.error("Login error", error);
        setErrorMessage("Error during login. Please try again.");
      });
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  return (
    <div className="auth-container">
      <div className="logo-section">
        <img src={logo} alt="Logo" className="logo" />
      </div>
      <div className="login-section">
        <h1>Login</h1>
        <form onSubmit={handleUsernamePasswordLogin}>
          <div className="form-group">
            <label htmlFor="username">Username:</label>
            <input
              id="username" // Add id to match the label
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password:</label>
            <div className="password-input-container">
              <input
                id="password" // Add id to match the label
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
        {errorMessage && <p role="alert">{errorMessage}</p>}{" "}
        {/* Display error message */}
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
