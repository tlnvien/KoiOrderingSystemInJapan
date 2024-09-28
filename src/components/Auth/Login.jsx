import { useState } from "react";
import { GoogleLogin } from "@react-oauth/google";
import FacebookLogin from "react-facebook-login/dist/facebook-login-render-props";
import { jwtDecode } from "jwt-decode";
import "./Login.css";
import logo from "./assets/logo.jpg";
import facebookLogo from "./assets/facebook-logo.png";
import { Link, useNavigate } from "react-router-dom";
import { FaEye, FaEyeSlash } from "react-icons/fa";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [isGoogleLogin, setIsGoogleLogin] = useState(false); // New state for login type

  const navigate = useNavigate();

  const handleGoogleLoginSuccess = (credentialResponse) => {
    const decoded = jwtDecode(credentialResponse.credential);
    const googleId = decoded.sub;

    console.log("Google user:", decoded);

    // Fetch the user from your API using googleId
    fetch(`https://66f19ed541537919155193cf.mockapi.io/Login`)
      .then((res) => {
        if (!res.ok) {
          throw new Error("Failed to fetch Google profile");
        }
        return res.json();
      })
      .then((users) => {
        if (users.length > 0) {
          const user = users.find((user) => user.googleId === googleId);
          localStorage.setItem("token", credentialResponse.credential);
          localStorage.setItem("googleId", googleId);
          localStorage.setItem("userId", user.id); // Store user ID
          localStorage.setItem("loginType", "google");
          alert("Google login successful!");
          navigate("/");
        } else {
          alert("Google login failed.");
        }
      })
      .catch((error) => {
        console.error("Google login error", error);
        alert("Error during Google login. Please try again.");
      });
  };

  const handleGoogleLoginFailure = (error) => {
    console.error("Google login failed:", error);
  };

  const handleFacebookLogin = (response) => {
    if (response.accessToken) {
      console.log("Facebook user:", response);
      localStorage.setItem("token", response.accessToken);
      localStorage.setItem("userId", response.userID);
      navigate("/"); // Change to your desired route
    } else {
      console.error("Facebook login failed");
    }
  };

  const handleUsernamePasswordLogin = (e) => {
    e.preventDefault();
    setIsGoogleLogin(false); // Set to false for username/password login

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
          alert("Login successful!");
          navigate("/");
        } else {
          alert("Invalid username or password");
        }
      })
      .catch((error) => {
        console.error("Login error", error);
        alert("Error during login. Please try again.");
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
        <div className="or-login">
          <p>Or login with</p>
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
                <span className="social-text1">Login with Facebook</span>
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
  );
}

export default Login;
