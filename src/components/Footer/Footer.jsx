import React from "react";
import "./Footer.css";

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-container">
        <div className="footer-info">
          <h3>Liên hệ với chúng tôi</h3>
          <p>Địa chỉ: 123 Đường ABC, Thành phố XYZ, Nhật Bản</p>
          <p>Email: contact@koibookingsystem.com</p>
          <p>Điện thoại: +81 123 456 789</p>
        </div>
        <div className="footer-copyright">
          <p>&copy; 2024 Koi Booking System. Tất cả các quyền được bảo lưu.</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
