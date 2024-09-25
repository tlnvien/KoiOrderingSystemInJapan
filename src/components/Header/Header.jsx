import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import logo from "./logo.jpg"; // Thay đổi đường dẫn nếu cần
import "./Header.css";
import { FaUserCircle } from "react-icons/fa";

const Header = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    // Kiểm tra token trong localStorage
    const token = localStorage.getItem("token");
    if (token) {
      setIsLoggedIn(true);
    }
  }, []);

  const handleLogout = () => {
    // Xóa token và điều hướng đến trang đăng nhập
    localStorage.removeItem("token");
    setIsLoggedIn(false);
    navigate("/");
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
            <a href="#gioi-thieu">Giới thiệu</a>
          </li>
          <li>
            <li>
              <Link to="/farm">Trang trại</Link>
            </li>
          </li>
          <li>
            <Link to="/koi-fish">Cá Koi</Link>
          </li>
          <li>
            <a href="#dich-vu">Dịch vụ</a>
          </li>
          <li>
            <Link to="/footer">Liên hệ</Link>
          </li>
          <li>
            <a href="#khac">Khác</a>
          </li>
          {isLoggedIn ? (
            <>
              <li>
                <button
                  className="user-icon"
                  onClick={() => navigate("/view-profile")}
                >
                  {/* Sử dụng icon FaUserCircle từ react-icons */}
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
