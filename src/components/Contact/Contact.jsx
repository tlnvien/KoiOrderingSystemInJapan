import React from "react";
import GoogleMap from "./GoogleMap"; // Adjust the path as needed
import "./Contact.css";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";

const Contact = () => {
  return (
    <div className="contact-page">
      <Header />
      <div className="contact-content">
        <h1>Liên Hệ Chúng Tôi</h1>

        <section className="contact-details">
          {/* <h2>Thông Tin Liên Hệ</h2> */}
          <p>
            Địa chỉ: Lô E2a-7, Đường D1, Đ. D1, Long Thạnh Mỹ, Thành Phố Thủ
            Đức, Hồ Chí Minh 700000, Vietnam
          </p>
          <p>Email: contact@koibookingsystem.com</p>
          <p>Điện thoại: +84 123 456 789</p>
        </section>

        <section className="contact-map">
          <h2>Bản Đồ</h2>
          <GoogleMap />
        </section>
      </div>
    </div>
  );
};

export default Contact;
