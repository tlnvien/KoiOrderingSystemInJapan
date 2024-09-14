// import React, { useState } from "react";
// import googleLogo from "./assets/google-logo.png";
// import facebookLogo from "./assets/facebook-logo.png";
// import logo from "./assets/logo.jpg";
// import "./Auth.css";

// const Register = () => {
//   const [formData, setFormData] = useState({
//     email: "",
//     username: "",
//     password: "",
//     confirmPassword: "",
//   });

//   const [agreeToTerms, setAgreeToTerms] = useState(false);

//   const handleChange = (e) => {
//     setFormData({
//       ...formData,
//       [e.target.name]: e.target.value,
//     });
//   };

//   const handleCheckboxChange = (e) => {
//     setAgreeToTerms(e.target.checked);
//   };

//   const handleSubmit = (e) => {
//     e.preventDefault();
//     if (!agreeToTerms) {
//       alert("You must agree to the terms and conditions.");
//       return;
//     }
//     // Handle form submission
//   };

//   return (
//     <div className="register-container">
//       <div className="logo-section">
//         <img src={logo} alt="Logo" className="logo" />
//       </div>
//       <div className="form-section">
//         <h2>Register</h2>
//         <form onSubmit={handleSubmit}>
//           <label>Email:</label>
//           <input
//             type="email"
//             name="email"
//             placeholder="Email"
//             value={formData.email}
//             onChange={handleChange}
//             required
//           />
//           <label>Username:</label>
//           <input
//             type="text"
//             name="username"
//             placeholder="Username"
//             value={formData.username}
//             onChange={handleChange}
//             required
//           />
//           <label>Password:</label>
//           <input
//             type="password"
//             name="password"
//             placeholder="Password"
//             value={formData.password}
//             onChange={handleChange}
//             required
//           />
//           <label>Confirm Password:</label>
//           <input
//             type="password"
//             name="confirmPassword"
//             placeholder="Confirm Password"
//             value={formData.confirmPassword}
//             onChange={handleChange}
//             required
//           />
//           <div className="terms-container">
//             <input
//               type="checkbox"
//               id="agreeToTerms"
//               checked={agreeToTerms}
//               onChange={handleCheckboxChange}
//             />
//             <label htmlFor="agreeToTerms">
//               I agree to the terms of service and user policy
//             </label>
//           </div>
//           <button type="submit" className="submit-btn">
//             Register
//           </button>
//         </form>
//         <div className="or-login">
//           <p>Or login with</p>
//         </div>
//         <div className="social-login">
//           <img src={googleLogo} alt="Google" className="social-icon" />
//           <img src={facebookLogo} alt="Facebook" className="social-icon" />
//         </div>
//       </div>
//     </div>
//   );
// };

// export default Register;

import React, { useState } from "react";
import googleLogo from "./assets/google-logo.png";
import facebookLogo from "./assets/facebook-logo.png";
import logo from "./assets/logo.jpg";
import "./Auth.css";

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

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!agreeToTerms) {
      alert("You must agree to the terms and conditions.");
      return;
    }
    // Handle form submission logic
    console.log("Form Data:", formData);
  };

  return (
    <div className="register-container">
      {/* Logo section */}
      <div className="logo-section">
        <img src={logo} alt="Logo" className="logo" />
      </div>
      <h2>Register</h2>

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
            <img src={googleLogo} alt="Google" className="social-icon" />
            <img src={facebookLogo} alt="Facebook" className="social-icon" />
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
