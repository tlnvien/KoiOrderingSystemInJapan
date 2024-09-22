import React, { useState } from "react";
import { GoogleLogin } from "@react-oauth/google";
import FacebookLogin from "react-facebook-login/dist/facebook-login-render-props";
import { jwtDecode } from "jwt-decode";
import facebookLogo from "./assets/facebook-logo.png";
import logo from "./assets/logo.jpg";
import "./Auth.css";
import { useNavigate } from "react-router-dom";

const Register = () => {
  const [formData, setFormData] = useState({
    email: "",
    username: "",
    password: "",
    confirmPassword: "",
    fullName: "",
    phoneNumber: "",
    address: "",
    gender: "",
    dob: "",
  });

  const navigate = useNavigate();

  const [agreeToTerms, setAgreeToTerms] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleCheckboxChange = (e) => {
    setAgreeToTerms(e.target.checked);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!agreeToTerms) {
      alert("You must agree to the terms and conditions.");
      return;
    }

    // Kiểm tra nếu mật khẩu và xác nhận mật khẩu không trùng khớp
    if (formData.password !== formData.confirmPassword) {
      alert("Passwords do not match!");
      return;
    }

    // Gửi dữ liệu đến MockAPI
    try {
      const response = await fetch(
        "https://66e1d268c831c8811b5672e8.mockapi.io/User", // Thay bằng endpoint MockAPI của bạn
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            email: formData.email,
            username: formData.username,
            password: formData.password,
            fullName: formData.fullName,
            phoneNumber: formData.phoneNumber,
            address: formData.address,
            gender: formData.gender,
            dob: formData.dob,
          }),
        }
      );

      if (response.ok) {
        const data = await response.json();
        // console.log("User registered successfully:", data);
        // alert("Registration successful!");
        navigate("/verify-account");
      } else {
        alert("Registration failed. Please try again.");
      }
    } catch (error) {
      console.error("Error registering user:", error);
      alert("An error occurred. Please try again later.");
    }
  };

  const handleGoogleLoginSuccess = (credentialResponse) => {
    const decoded = jwtDecode(credentialResponse.credential);
    console.log("Google user:", decoded);
  };

  const handleGoogleLoginFailure = (error) => {
    console.error("Google login failed:", error);
  };

  const handleFacebookLogin = (response) => {
    if (response.accessToken) {
      console.log("Facebook user:", response);
    } else {
      console.error("Facebook login failed");
    }
  };

  return (
    <div className="register-container">
      {/* Logo section */}
      <div className="logo-section">
        <img src={logo} alt="Logo" className="logo" />
      </div>
      <h1>Register</h1>

      {/* Form section with two parts */}
      <div className="form-container">
        {/* Left side - Existing Registration Fields */}
        <div className="form-left">
          <form onSubmit={handleSubmit}>
            {/* Registration Information */}
            <label>Email:</label>
            <input
              type="email"
              name="email"
              placeholder="Email"
              value={formData.email}
              onChange={handleChange}
              required
            />
            <label>Username:</label>
            <input
              type="text"
              name="username"
              placeholder="Username"
              value={formData.username}
              onChange={handleChange}
              required
            />
            <label>Password:</label>
            <input
              type="password"
              name="password"
              placeholder="Password"
              value={formData.password}
              onChange={handleChange}
              required
            />
            <label>Confirm Password:</label>
            <input
              type="password"
              name="confirmPassword"
              placeholder="Confirm Password"
              value={formData.confirmPassword}
              onChange={handleChange}
              required
            />
            <div className="terms-container">
              <input
                type="checkbox"
                id="agreeToTerms"
                checked={agreeToTerms}
                onChange={handleCheckboxChange}
              />
              <label htmlFor="agreeToTerms">
                I agree to the terms of service and user policy
              </label>
            </div>
            <button type="submit" className="submit-btn">
              Register
            </button>
          </form>
          <div className="or-login">
            <p>Or login with</p>
          </div>
          <div className="social-login">
            {/* Google Login */}
            <GoogleLogin
              onSuccess={handleGoogleLoginSuccess}
              onError={handleGoogleLoginFailure}
              useOneTap
            />
            {/* Facebook Login */}
            <FacebookLogin
              appId="875093550843749"
              callback={handleFacebookLogin}
              render={(renderProps) => (
                <button onClick={renderProps.onClick} className="social-btn">
                  <span className="social-text">Login with Facebook</span>
                  <img
                    src={facebookLogo}
                    alt="Facebook Logo"
                    className="social-logo"
                  />
                </button>
              )}
            />
          </div>
        </div>

        {/* Right side - Personal Info */}
        <div className="form-right">
          <form onSubmit={handleSubmit}>
            {/* Personal Information */}
            <label>Full Name:</label>
            <input
              type="text"
              name="fullName"
              placeholder="Full Name"
              value={formData.fullName}
              onChange={handleChange}
              required
            />
            <label>Phone Number:</label>
            <input
              type="tel"
              name="phoneNumber"
              placeholder="Phone Number"
              value={formData.phoneNumber}
              onChange={handleChange}
              required
            />
            <label>Address:</label>
            <input
              type="text"
              name="address"
              placeholder="Address"
              value={formData.address}
              onChange={handleChange}
              required
            />
            <label>Gender:</label>
            <select
              name="gender"
              value={formData.gender}
              onChange={handleChange}
              required
            >
              <option value="">Select Gender</option>
              <option value="male">Male</option>
              <option value="female">Female</option>
              <option value="other">Other</option>
            </select>
            <label>Date of Birth:</label>
            <input
              type="date"
              name="dob"
              value={formData.dob}
              onChange={handleChange}
              required
            />
          </form>
        </div>
      </div>
    </div>
  );
};

export default Register;
