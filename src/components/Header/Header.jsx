import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import logo from "./logo.jpg"; // Change the path if needed
import "./Header.css";
import { FaUserCircle } from "react-icons/fa";

const Header = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      setIsLoggedIn(true);
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    localStorage.removeItem("googleId");
    localStorage.removeItem("loginType");
    localStorage.removeItem("bookingId");
    setIsLoading(false);
    setIsLoggedIn(false);
    navigate("/");
  };

  const handleUserIconClick = () => {
    const loginType = localStorage.getItem("loginType");
    if (loginType === "google") {
      navigate("/google-profile");
    } else if (loginType === "username") {
      navigate("/view-profile");
    } else {
      navigate("/facebook-profile");
    }
  };

  const handleServiceChange = (event) => {
    const value = event.target.value;
    if (value) {
      navigate(value);
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

          {/* Dịch vụ dropdown */}
          <li>
            <select onChange={handleServiceChange} className="service-select">
              <option value="">Dịch vụ</option>
              <option value="/list-tour">Tour trọn gói</option>
              <option value="/booking">Đặt tour</option>
            </select>
          </li>

          <li>
            <Link to="/contact">Liên hệ</Link>
          </li>
          {isLoggedIn ? (
            <>
              <li>
                <button className="user-icon" onClick={handleUserIconClick}>
                  <FaUserCircle size={30} />
                </button>
                <span className="separator">|</span>
                <Link
                  to="/login"
                  onClick={handleLogout}
                  className="logout-link"
                >
                  Đăng xuất
                </Link>
                {isLoading && <p>Đang đăng xuất...</p>}
              </li>
            </>
          ) : (
            <li>
              <Link to="/login">Đăng nhập</Link>
              <span className="separator">|</span>
              <Link to="/register/customer">Đăng kí</Link>
            </li>
          )}
        </ul>
      </div>
    </nav>
  );
};

export default Header;
