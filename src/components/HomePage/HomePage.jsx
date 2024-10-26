import "./HomePage.css"; // Import your CSS file
import Header from "../Header/Header"; // Import Header component
import Footer from "../Footer/Footer";
// import logo from "./assets/logo.jpg";
import HeroImage from "./assets/hero-image.jpg";
import aboutImage1 from "./assets/office.jpg";
import aboutImage2 from "./assets/team.jpg";
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
          <Link to="/search" className="tour-label">
            Tour trọn gói
          </Link>
          <Link to="/search" className="tour-label1">
            Đặt tour
          </Link>
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

          <Link to="/search" className="search">
            Tìm kiếm
          </Link>
        </div>
      </div>

      <div className="about-us-section">
        <div className="about-us-content">
          <div className="about-us-text">
            <h2>Về chúng tôi</h2>
            <p>
              Chào mừng bạn đến với Nền Tảng Đặt Tour và Du Lịch của chúng tôi!
              Chúng tôi cam kết mang đến những trải nghiệm du lịch tốt nhất với
              đa dạng các gói tour phù hợp cho mọi nhu cầu của khách hàng. Từ
              những kỳ nghỉ thư giãn tại vùng nông thôn yên bình đến những
              chuyến phiêu lưu khám phá quốc tế, các tour của chúng tôi được
              thiết kế để tạo nên những kỷ niệm khó quên.
            </p>
            <p>
              Sứ mệnh của chúng tôi là làm cho việc du lịch trở nên dễ dàng và
              thú vị cho mọi người bằng cách cung cấp các gói tour linh hoạt và
              hợp lý. Cho dù bạn đang lên kế hoạch cho kỳ nghỉ gia đình, chuyến
              đi lãng mạn hay cuộc phiêu lưu cùng bạn bè, đội ngũ tận tâm của
              chúng tôi luôn sẵn sàng biến hành trình của bạn thành hiện thực.
            </p>
          </div>
          <div className="about-us-images">
            <img
              src={aboutImage1}
              alt="Office"
              className="about-image first-image"
            />
            <img
              src={aboutImage2}
              alt="Team"
              className="about-image second-image"
            />
          </div>
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
