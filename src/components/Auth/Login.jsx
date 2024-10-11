import { useState } from "react";
import { GoogleLogin } from "@react-oauth/google";
import FacebookLogin from "react-facebook-login/dist/facebook-login-render-props";
import { jwtDecode } from "jwt-decode";
import "./Login.css";
import logo from "./assets/logo.jpg";
import facebookLogo from "./assets/facebook-logo.png";
import { Link, useNavigate } from "react-router-dom";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import axios from "axios";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [isGoogleLogin, setIsGoogleLogin] = useState(false); // New state for login type

  const navigate = useNavigate();

  const apiUrl = "http://localhost:8082/api/login";

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
      const facebookId = response.userID;

      console.log("Facebook user:", response);

      // Fetch the user from your API using facebookId
      fetch(`https://66f19ed541537919155193cf.mockapi.io/FacebookProfile`)
        .then((res) => {
          if (!res.ok) {
            throw new Error("Failed to fetch Facebook profile");
          }
          return res.json();
        })
        .then((users) => {
          if (users.length > 0) {
            const user = users.find((user) => user.facebookId === facebookId);
            if (user) {
              localStorage.setItem("token", response.accessToken);
              localStorage.setItem("facebookId", facebookId);
              localStorage.setItem("userId", user.id); // Store user ID
              localStorage.setItem("loginType", "facebook");
              alert("Facebook login successful!");
              navigate("/"); // Navigate to Facebook profile page
            } else {
              alert("Facebook login failed. No user found.");
            }
          }
        })
        .catch((error) => {
          console.error("Facebook login error", error);
          alert("Error during Facebook login. Please try again.");
        });
    } else {
      console.error("Facebook login failed");
    }
  };

  const handleUsernamePasswordLogin = async (e) => {
    e.preventDefault();
    setIsGoogleLogin(false);

    try {
      const response = await axios.post(apiUrl, {
        username: username, // Payload with username
        password: password, // Payload with password
      });

      // Assuming response data contains the token, userId, and role
      localStorage.setItem("token", response.data.token);
      localStorage.setItem("userId", response.data.userId);
      localStorage.setItem("role", response.data.role);
      localStorage.setItem("loginType", "username");
      navigate("/");
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