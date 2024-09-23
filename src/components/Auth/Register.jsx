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

  const [errors, setErrors] = useState({});
  const [agreeToTerms, setAgreeToTerms] = useState(false);
  const navigate = useNavigate();

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
    if (!value) return "Customer name must not be blank";
    if (value.trimStart().length !== value.length)
      return "First character cannot have space";
    if (/[\d]/.test(value)) return "Numbers are not allowed";
    if (/[^a-zA-Z\s]/.test(value)) return "Special characters are not allowed";
    return "";
  };

  const validateUsername = (value) => {
    if (!value) return "Username must not be blank";
    if (value.trimStart().length !== value.length)
      return "First character cannot have space";
    if (value.length < 3) return "Username must be at least 3 characters long";
    if (value.length > 20) return "Username must not exceed 20 characters";
    if (/[^a-zA-Z0-9]/.test(value))
      return "Username can only contain letters and numbers";
    return "";
  };

  const validateAddress = (value) => {
    if (!value) return "Address field must not be blank";
    if (value.trimStart().length !== value.length)
      return "First character cannot have space";
    if (/[^a-zA-Z0-9\s]/.test(value))
      return "Special characters are not allowed";
    return "";
  };

  const validatePhoneNumber = (value) => {
    if (!value) return "Mobile number must not be blank";
    if (/[^0-9]/.test(value)) return "Characters are not allowed";
    if (value.trimStart().length !== value.length)
      return "First character cannot have space";
    return "";
  };

  const validateEmail = (value) => {
    if (!value) return "Email ID must not be blank";
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regex.test(value)) return "Email ID is not valid";
    if (value.trimStart().length !== value.length)
      return "First character cannot have space";
    return "";
  };

  const validatePassword = (value) => {
    if (!value) return "Password must not be blank";
    if (value.length < 8) return "Password must be at least 8 characters long";
    if (!/[A-Za-z]/.test(value))
      return "Password must contain at least one letter";
    if (!/[0-9]/.test(value))
      return "Password must contain at least one number";
    if (!/[!@#$%^&*]/.test(value))
      return "Password must contain at least one special character";
    return "";
  };

  const validateConfirmPassword = (password, confirmPassword) => {
    if (confirmPassword !== password) return "Passwords do not match!";
    return "";
  };

  const validateDOB = (value) => {
    const today = new Date();
    const dob = new Date(value);
    if (!value) return "Date of birth must not be blank";
    if (dob >= today) return "Date of birth cannot be in the future";
    return "";
  };

  const validateGender = (value) => {
    if (!value) return "Gender must be selected";
    return "";
  };

  const handleBlur = (e) => {
    const { name, value } = e.target;
    let error = "";

    switch (name) {
      case "fullName":
        error = validateFullName(value);
        break;
      case "address":
        error = validateAddress(value);
        break;
      case "phoneNumber":
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
      case "dob":
        error = validateDOB(value);
        break;
      case "gender":
        error = validateGender(value);
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
      alert("You must agree to the terms and conditions.");
      return;
    }

    // Kiểm tra lỗi trước khi gửi
    if (Object.keys(errors).length > 0) {
      alert("Please fix the errors before submitting.");
      return;
    }

    try {
      const response = await fetch(
        "https://66e1d268c831c8811b5672e8.mockapi.io/User",
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
      <div className="logo-section">
        <img src={logo} alt="Logo" className="logo" />
      </div>
      <h1>Register</h1>
      <div className="form-container">
        <div className="form-left">
          <form onSubmit={handleSubmit}>
            <label>Email:</label>
            <input
              type="email"
              name="email"
              placeholder="Email"
              value={formData.email}
              onChange={handleChange}
              onBlur={handleBlur}
              required
            />
            {errors.email && <span className="error">{errors.email}</span>}

            <label>Username:</label>
            <input
              type="text"
              name="username"
              placeholder="Username"
              value={formData.username}
              onChange={handleChange}
              onBlur={handleBlur}
              required
            />
            {errors.username && (
              <span className="error">{errors.username}</span>
            )}

            <label>Password:</label>
            <input
              type="password"
              name="password"
              placeholder="Password"
              value={formData.password}
              onChange={handleChange}
              onBlur={handleBlur}
              required
            />
            {errors.password && (
              <span className="error">{errors.password}</span>
            )}

            <label>Confirm Password:</label>
            <input
              type="password"
              name="confirmPassword"
              placeholder="Confirm Password"
              value={formData.confirmPassword}
              onChange={handleChange}
              onBlur={handleBlur}
              required
            />
            {errors.confirmPassword && (
              <span className="error">{errors.confirmPassword}</span>
            )}

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
            <GoogleLogin
              onSuccess={handleGoogleLoginSuccess}
              onError={handleGoogleLoginFailure}
              useOneTap
            />
            <FacebookLogin
              appId="875093550843749"
              callback={handleFacebookLogin}
              render={(renderProps) => (
                <button onClick={renderProps.onClick} className="social-btn">
                  <img
                    src={facebookLogo}
                    alt="Facebook Logo"
                    className="social-logo"
                  />
                  Login with Facebook
                </button>
              )}
            />
          </div>
        </div>
        <div className="form-right">
          <label>Full Name:</label>
          <input
            type="text"
            name="fullName"
            placeholder="Full Name"
            value={formData.fullName}
            onChange={handleChange}
            onBlur={handleBlur}
            required
          />
          {errors.fullName && <span className="error">{errors.fullName}</span>}

          <label>Phone Number:</label>
          <input
            type="tel"
            name="phoneNumber"
            placeholder="Phone Number"
            value={formData.phoneNumber}
            onChange={handleChange}
            onBlur={handleBlur}
            required
          />
          {errors.phoneNumber && (
            <span className="error">{errors.phoneNumber}</span>
          )}

          <label>Address:</label>
          <input
            type="text"
            name="address"
            placeholder="Address"
            value={formData.address}
            onChange={handleChange}
            onBlur={handleBlur}
            required
          />
          {errors.address && <span className="error">{errors.address}</span>}

          <label>Gender:</label>
          <select
            name="gender"
            value={formData.gender}
            onChange={handleChange}
            onBlur={handleBlur}
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
            onBlur={handleBlur}
            required
          />
        </div>
      </div>
    </div>
  );
};

export default Register;
