import { useState } from "react";
import "./Login.css";
import logo from "./assets/logo.jpg";
import googleLogo from "./assets/google-logo.png";
import facebookLogo from "./assets/facebook-logo.png";
import { Link } from "react-router-dom";
import { FaEye, FaEyeSlash } from "react-icons/fa"; // Importing eye icons

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const handleLogin = (e) => {
    e.preventDefault();
    console.log("Logging in with", { email, password });
  };

  const handleGoogleLogin = () => {
    console.log("Logging in with Google");
  };

  const handleFacebookLogin = () => {
    console.log("Logging in with Facebook");
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
        <h2>Login</h2>
        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label>Username/Email/Phone:</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
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
        <div className="or-login">
          <p>Or login with</p>
        </div>
        <div className="social-login">
          <button onClick={handleGoogleLogin} className="social-btn">
            <img src={googleLogo} alt="Google Logo" className="social-logo" />
          </button>
          <button onClick={handleFacebookLogin} className="social-btn">
            <img
              src={facebookLogo}
              alt="Facebook Logo"
              className="social-logo"
            />
          </button>
        </div>
      </div>
    </div>
  );
}

export default Login;
