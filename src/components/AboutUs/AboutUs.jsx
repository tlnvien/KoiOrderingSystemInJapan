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
      <h1>About Us</h1>

      <section className="company-intro">
        <h2>Our Company</h2>
        <img src={companyImage} alt="Our Company" className="about-image1" />
        <p>
          We are a leading travel agency with a passion for providing
          unforgettable experiences. Founded in [Year], we have been dedicated
          to helping travelers explore the world.
        </p>
      </section>

      <section className="mission-vision">
        <h2>Our Mission & Vision</h2>
        <p>
          Our mission is to make travel accessible and enjoyable for everyone.
          We envision a world where every traveler can experience the beauty and
          diversity of our planet.
        </p>
      </section>

      <section className="services">
        <h2>Our Services</h2>
        <ul>
          <li>Customized Tour Packages</li>
          <li>Hotel Bookings</li>
          <li>Transportation Services</li>
          <li>Experienced Tour Guides</li>
        </ul>
      </section>

      <section className="team">
        <h2>Meet Our Team</h2>
        <img src={teamImage} alt="Our Team" className="about-image1" />
        <p>
          Our team consists of experienced travel experts who are dedicated to
          providing the best service. Each member is passionate about travel and
          is here to assist you.
        </p>
      </section>

      <section className="testimonials">
        <h2>What Our Customers Say</h2>
        <blockquote>
          "The best travel experience I've ever had! Highly recommend!" -{" "}
          <strong>Customer Name</strong>
        </blockquote>
      </section>

      <section className="contact-info">
        <h2>Contact Us</h2>
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
