import "./HomePage.css";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import HeroImage from "./assets/hero-image.jpg";
import { Link } from "react-router-dom";
import SliderFarm from "./SliderFarm";
import SliderKoi from "./SliderKoi";

const HomePage = () => {
  return (
    <div className="homepage-container">
      <Header />

      {/* Hero Section */}
      <section className="hero-section">
        <div className="hero-image">
          <img src={HeroImage} alt="About us" />
        </div>
      </section>

      {/* Form Section */}
      <div className="tour-form-container" id="dich-vu">
        <div className="tour-form-row">
          <Link to="/list-tour" className="tour-label">
            Tour trọn gói
          </Link>
          <Link to="/booking" className="tour-label1">
            Đặt tour
          </Link>
        </div>
      </div>

      <div className="famous-koi-farms-section">
        <h2>Các Trang Trại Cá Koi Nổi Tiếng</h2>
        <SliderFarm />
        <Link to="/farm" className="see-more-btn">
          Xem thêm
        </Link>
      </div>

      <div className="famous-koi-varieties-section">
        <h2>Các Giống Cá Koi Nổi Tiếng</h2>
        <SliderKoi />
        <Link to="/koi-fish" className="see-more-btn">
          Xem thêm
        </Link>
      </div>

      <Footer />
    </div>
  );
};

export default HomePage;
