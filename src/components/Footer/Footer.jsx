import React from "react";
import { Link } from "react-router-dom";
import "./Footer.css";

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-container container">
        <div className="row">
          {/* Contact Information Column */}
          <div className="col-md-4">
            <h3>Liên hệ với chúng tôi</h3>
            <p>Địa chỉ: Đại học FPT Hồ Chí Minh</p>
            <p>Email: contact@koibookingsystem.com</p>
            <p>Điện thoại: +84 123 456 789</p>
          </div>

          {/* Information Column */}
          <div className="col-md-4">
            <h3>Thông tin</h3>
            <ul className="footer-links">
              <li>
                <Link to="/aboutUs">Giới thiệu</Link>
              </li>
              <li>
                <Link to="/farm">Trang trại</Link>
              </li>
              <li>
                <Link to="/koi-fish">Cá koi</Link>
              </li>
              <li>
                <Link to="/contact">Liên hệ</Link>
              </li>
            </ul>
          </div>

          {/* Services Column */}
          <div className="col-md-4">
            <h3>Dịch vụ</h3>
            <ul className="footer-links">
              <li>
                <Link to="/booking">Tour theo yêu cầu</Link>
              </li>
              <li>
                <Link to="/list-tour">Tour theo thiết kế</Link>
              </li>
            </ul>
          </div>
        </div>

        {/* Copyright Section */}
        <div className="footer-bottom text-center mt-3">
          <p>&copy; 2024 Koi Booking System. Tất cả các quyền được bảo lưu.</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
