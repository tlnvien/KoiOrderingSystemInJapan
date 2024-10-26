import { useState } from "react";
import { GoogleLogin } from "@react-oauth/google";
import FacebookLogin from "react-facebook-login/dist/facebook-login-render-props";
// import jwtDecode from "jwt-decode";
import "./Login.css";
import logo from "./assets/logo.jpg";
import facebookLogo from "./assets/facebook-logo.png";
import { Link, useNavigate } from "react-router-dom";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import axios from "axios";
import { auth, googleProvider } from "../../config/firebase"; // Adjust the path as necessary
import { GoogleAuthProvider, signInWithPopup } from "firebase/auth";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [isGoogleLogin, setIsGoogleLogin] = useState(false);

  const navigate = useNavigate();

  const apiUrl = "http://localhost:8082/api/login";

  // Updated Google login success handler
  const handleGoogleLoginSuccess = async (response) => {
    try {
      const token = response.credential;
      console.log("Google OAuth token:", token);

      // Optionally send token to backend for further processing
      const res = await axios.post("http://localhost:8082/api/auth/google", {
        token,
      });

      const { email, userId } = res.data;

      // Store the necessary information in localStorage
      localStorage.setItem("token", token);
      localStorage.setItem("email", email);
      localStorage.setItem("userId", userId);
      localStorage.setItem("loginType", "google");

      alert("Đăng nhập Google thành công!");
      navigate("/");
    } catch (error) {
      console.error("Error logging in with Google:", error);
      alert("Có lỗi xảy ra khi đăng nhập Google. Vui lòng thử lại.");
    }
  };

  const handleGoogleLoginFailure = (error) => {
    console.error("Đăng nhập Google thất bại:", error);
  };

  const handleFacebookLogin = (response) => {
    if (response.accessToken) {
      const facebookId = response.userID;

      console.log("Người dùng Facebook:", response);

      fetch(`https://66f19ed541537919155193cf.mockapi.io/FacebookProfile`)
        .then((res) => {
          if (!res.ok) {
            throw new Error("Lấy thông tin hồ sơ Facebook thất bại");
          }
          return res.json();
        })
        .then((users) => {
          if (users.length > 0) {
            const user = users.find((user) => user.facebookId === facebookId);
            if (user) {
              localStorage.setItem("token", response.accessToken);
              localStorage.setItem("facebookId", facebookId);
              localStorage.setItem("userId", user.id);
              localStorage.setItem("loginType", "facebook");
              alert("Đăng nhập Facebook thành công!");
              navigate("/");
            } else {
              alert("Đăng nhập Facebook thất bại. Không tìm thấy người dùng.");
            }
          }
        })
        .catch((error) => {
          console.error("Lỗi đăng nhập Facebook", error);
          alert("Có lỗi xảy ra khi đăng nhập Facebook. Vui lòng thử lại.");
        });
    } else {
      console.error("Đăng nhập Facebook thất bại");
    }
  };

  const handleUsernamePasswordLogin = async (e) => {
    e.preventDefault();
    setIsGoogleLogin(false);

    try {
      const response = await axios.post(apiUrl, {
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
  );
}

export default Login;
