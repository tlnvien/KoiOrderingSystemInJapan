import React from "react";
import "./AboutUs.css";
import companyImage from "./assets/office.jpg";
import teamImage from "./assets/team.jpg";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";

const AboutUs = () => {
  return (
    <div className="about-us-container">
      <Header />
      <h1>Giới thiệu về công ty</h1>

      <section className="company-intro">
        <h2>Công ty của chúng tôi</h2>
        <img
          src={companyImage}
          alt="Công ty chúng tôi"
          className="about-image1"
        />
        <p>
          Chúng tôi là một công ty du lịch hàng đầu với niềm đam mê mang đến
          những trải nghiệm đáng nhớ. Được thành lập từ năm 2024, chúng tôi cam
          kết hỗ trợ khách hàng khám phá thế giới.
        </p>
      </section>

      <section className="mission-vision">
        <h2>Sứ mệnh & Tầm nhìn</h2>
        <p>
          Sứ mệnh của chúng tôi là mang lại trải nghiệm du lịch dễ dàng và thú
          vị cho mọi người. Chúng tôi mong muốn mỗi người đều có cơ hội chiêm
          ngưỡng vẻ đẹp và sự đa dạng của hành tinh này.
        </p>
      </section>

      <section className="services">
        <h2>Dịch vụ của chúng tôi</h2>
        <ul>
          <li>Đặt tour theo nhu cầu</li>
          <li>Gói tour trọn gói</li>
          <li>Dịch vụ vận chuyển</li>
          <li>Hướng dẫn viên giàu kinh nghiệm</li>
        </ul>
      </section>

      <section className="team">
        <h2>Đội ngũ của chúng tôi</h2>
        <img
          src={teamImage}
          alt="Đội ngũ của chúng tôi"
          className="about-image1"
        />
        <p>
          Đội ngũ của chúng tôi bao gồm những chuyên gia du lịch tận tâm. Mỗi
          thành viên đều có niềm đam mê với du lịch và luôn sẵn sàng hỗ trợ bạn.
        </p>
      </section>

      <section className="testimonials">
        <h2>Khách hàng nói gì về chúng tôi</h2>
        <blockquote>
          "Trải nghiệm du lịch tuyệt vời nhất mà tôi từng có! Rất đáng để thử!"
          - <strong>Tên khách hàng</strong>
        </blockquote>
      </section>

      <section className="contact-info">
        <h2>Liên hệ</h2>
        <p>
          <p>Địa chỉ: 123 Đường ABC, Thành phố XYZ, Nhật Bản</p>
          <p>Email: contact@koibookingsystem.com</p>
          <p>Điện thoại: +81 123 456 789</p>
        </p>
      </section>
      {/* <Footer /> */}
    </div>
  );
};

export default AboutUs;
