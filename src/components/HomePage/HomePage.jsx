import "./HomePage.css"; // Import your CSS file
import Header from "../Header/Header"; // Import Header component
import Footer from "../Footer/Footer";
import koiFarmImage from "./assets/koi-farm.jpg"; // Example image for farms
import koiVarietyImage1 from "./assets/koi-fish.jpg"; // Example image for Koi variety 1
import koiVarietyImage2 from "./assets/koi-fish1.jpg"; // Example image for Koi variety 2
import koiVarietyImage3 from "./assets/koi-fish2.jpg"; // Example image for Koi variety 3
// import logo from "./assets/logo.jpg"; // Example logo
import HeroImage from "./assets/hero-image.jpg";
import aboutImage1 from "./assets/about-us1.jpg";
import aboutImage2 from "./assets/about-us2.jpg";
import { Link } from "react-router-dom";
// import { Link } from "react-router-dom";

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
      <div className="tour-form-container">
        <div className="tour-form-row">
          <strong className="tour-label">Tour trọn gói</strong>
          <strong className="tour-label1">Đặt tour</strong>
        </div>

        <div className="tour-form-row">
          {/* Trang trại Input with Select */}
          <div className="dropdown-container">
            <select className="form-input">
              <option value="">Chọn trang trại</option>
              <option value="farm1">Trang trại 1</option>
              <option value="farm2">Trang trại 2</option>
              <option value="farm3">Trang trại 3</option>
            </select>
          </div>

          {/* Giống cá Input with Select */}
          <div className="dropdown-container">
            <select className="form-input">
              <option value="">Chọn giống cá</option>
              <option value="variety1">Kohaku Koi</option>
              <option value="variety2">Showa Koi</option>
              <option value="variety3">Senke Koi</option>
            </select>
          </div>

          {/* Mức giá Input with Select */}
          <div className="dropdown-container">
            <select className="form-input">
              <option value="">Chọn mức giá</option>
              <option value="under10">Dưới 10 triệu</option>
              <option value="10to20">10 - 20 triệu</option>
              <option value="above20">Trên 20 triệu</option>
            </select>
          </div>

          {/* Thời gian Input with Select */}
          <div className="dropdown-container">
            <select className="form-input">
              <option value="">Chọn thời gian</option>
              <option value="1day">1 ngày</option>
              <option value="3days">3 ngày</option>
              <option value="7days">7 ngày</option>
            </select>
          </div>

          <button className="search">Tìm kiếm</button>
        </div>
      </div>

      <div className="about-us-section">
        <div className="about-us-content">
          <div className="about-us-text">
            <h2>About Us</h2>
            <p>
              Matsue Nishikigoi Center là một trong các trại cá Koi Nhật Bản nổi
              tiếng với quy mô lớn. Matsue Nishikigoi được thành lập bởi ông
              Shoichi Iizuka vào tháng 4 năm 1996. Các dòng cá Koi tại trung tâm
              Matsue rất đa dạng như Kohaku, Showa, Sanke, Doitsu. Nhưng trung
              tâm được biết đến rộng rãi là nhờ dòng cá Koi Jumbo Kohaku.
            </p>
            <p>
              Nhờ kinh nghiệm và nỗ lực không ngừng của ông Iizuka, Matsue được
              mọi người biết đến rộng rãi với dòng Jumbo Kohaku và trở thành nơi
              đáng tin cậy cho những người yêu cá Koi.
            </p>
          </div>
          <div className="about-us-images">
            <img
              src={aboutImage1}
              alt="Koi Farm"
              className="about-image first-image"
            />
            <img
              src={aboutImage2}
              alt="Koi Farm"
              className="about-image second-image"
            />
          </div>
        </div>
      </div>

      <div className="famous-koi-farms-section">
        <h2>Các Trang Trại Cá Koi Nổi Tiếng</h2>
        <div className="farms-gallery">
          <div className="farm-item">
            <img src={koiFarmImage} alt="Farm 1" />
            <h3>Trang Trại 1</h3>
          </div>
          <div className="farm-item">
            <img src={aboutImage1} alt="Farm 2" />
            <h3>Trang Trại 2</h3>
          </div>
          <div className="farm-item">
            <img src={aboutImage2} alt="Farm 3" />
            <h3>Trang Trại 3</h3>
          </div>
        </div>
        <Link to="/farm" className="see-more-btn">
          Xem thêm
        </Link>
      </div>

      <div className="famous-koi-varieties-section">
        <h2>Các Giống Cá Koi Nổi Tiếng</h2>
        <div className="varieties-gallery">
          <div className="variety-item">
            <img src={koiVarietyImage1} alt="Variety 1" />
            <h3>Kohaku Koi</h3>
          </div>
          <div className="variety-item">
            <img src={koiVarietyImage2} alt="Variety 2" />
            <h3>Showa Koi</h3>
          </div>
          <div className="variety-item">
            <img src={koiVarietyImage3} alt="Variety 3" />
            <h3>Senke Koi</h3>
          </div>
        </div>
        <Link to="/koi-fish" className="see-more-btn">
          Xem thêm
        </Link>
      </div>

      <Footer />
    </div>
  );
};

export default HomePage;
