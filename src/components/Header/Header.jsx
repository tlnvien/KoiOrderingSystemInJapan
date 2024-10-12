import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import logo from "./logo.jpg"; // Change the path if needed
import "./Header.css";
import { FaUserCircle } from "react-icons/fa";

const Header = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    // Check for token in localStorage
    const token = localStorage.getItem("token");
    if (token) {
      setIsLoggedIn(true);
    }
  }, []);

  const handleLogout = () => {
    // Remove token and navigate to homepage
    localStorage.removeItem("token");
    localStorage.removeItem("userId"); // Optional: clear userId if necessary
    localStorage.removeItem("googleId"); // Optional: clear googleId if necessary
    localStorage.removeItem("loginType"); // Clear login type
    setIsLoggedIn(false);
    navigate("/");
  };

  const handleUserIconClick = () => {
    const loginType = localStorage.getItem("loginType");
    if (loginType === "google") {
      navigate("/google-profile"); // Navigate to Google profile
    } else if (loginType === "username") {
      navigate("/view-profile"); // Navigate to user profile
    } else {
      navigate("/facebook-profile"); // Navigate to Facebook profile
    }
  };

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <img src={logo} alt="Logo" className="logo" />
      </div>
      <div className="navbar-right">
        <ul>
          <li>
            <Link to="/">Trang chủ</Link>
          </li>
          <li>
            <a href="/aboutUs">Giới thiệu</a>
          </li>
          <li>
            <Link to="/farm">Trang trại</Link>
          </li>
          <li>
            <Link to="/koi-fish">Cá Koi</Link>
          </li>
          <li>
            <a href="#dich-vu">Dịch vụ</a>
          </li>
          <li>
            <Link to="/contact">Liên hệ</Link>
          </li>
          {isLoggedIn ? (
            <>
              <li>
                <button
                  className="user-icon"
                  onClick={handleUserIconClick} // Use the modified click handler
                >
                  <FaUserCircle size={30} />
                </button>
                <span className="separator">|</span>
                <Link to="/" onClick={handleLogout} className="logout-link">
                  Đăng xuất
                </Link>
              </li>
            </>
          ) : (
            <li>
              <Link to="/login">Đăng nhập</Link>
              <span className="separator">|</span>
              <Link to="/register">Đăng kí</Link>
            </li>
          )}
        </ul>
      </div>
    </nav>
  );
};

export default Header;
