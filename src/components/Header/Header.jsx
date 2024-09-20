import React from "react";
import { Link } from "react-router-dom";
import logo from "./logo.jpg"; // Thay đổi đường dẫn nếu cần
import "./Header.css";

const Header = () => {
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
          <li>
            <Link to="/login">Đăng nhập</Link>
            <span className="separator">|</span>
            <Link to="/register">Đăng kí</Link>
          </li>
        </ul>
      </div>
    </nav>
  );
};

export default Header;
