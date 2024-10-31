import React from "react";
import "./AboutUs.css";
import Tour1 from "./assets/tour1.jpg";
import Tour2 from "./assets/tour2.jpg";
import Tour3 from "./assets/tour3.jpg";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
const AboutUs = () => {
  return (
    <div className="about-us">
      <Header />
      <header className="header">
        <h1>Giới Thiệu Về Chúng Tôi</h1>
        <p>
          Khám phá vẻ đẹp thiên nhiên với dịch vụ đặt tour và trải nghiệm mua cá
          chuyên nghiệp.
        </p>
      </header>

      {/* Section 1 - Đặt Tour Theo Nhu Cầu */}
      <section className="section">
        <h2>Đặt Tour Theo Nhu Cầu</h2>
        <p>
          Chúng tôi cung cấp dịch vụ đặt tour linh hoạt, đáp ứng mọi nhu cầu của
          bạn. Bạn có thể tự do lựa chọn điểm đến, thời gian, và các hoạt động
          ưa thích để có chuyến đi độc đáo và đáng nhớ.
        </p>
        <img src={Tour1} alt="Đặt tour theo nhu cầu" />
      </section>

      {/* Section 2 - Đặt Tour Trọn Gói */}
      <section className="section">
        <h2>Dịch Vụ Đặt Tour Trọn Gói</h2>
        <p>
          Dành cho những ai yêu thích sự tiện lợi, chúng tôi mang đến các tour
          trọn gói với hành trình chi tiết, bao gồm tất cả dịch vụ từ phương
          tiện di chuyển, ăn uống, đến trải nghiệm du lịch. Tất cả những gì bạn
          cần làm là thư giãn và tận hưởng.
        </p>
        <img src={Tour2} alt="Đặt tour trọn gói" />
      </section>

      {/* Section 3 - Mua Cá Trên Chuyến Tham Quan */}
      <section className="section">
        <h2>Mua Cá Trên Chuyến Tham Quan</h2>
        <p>
          Ngoài các hoạt động tham quan, khách hàng còn có thể mua các loài cá
          cảnh tuyệt đẹp tại các trang trại cá nổi tiếng mà chúng tôi giới thiệu
          trong chuyến đi. Đây là cơ hội để bạn mang về nhà một phần thiên nhiên
          tươi đẹp từ các chuyến du lịch.
        </p>
        <img src={Tour3} alt="Mua cá trên chuyến tham quan" />
      </section>

      {/* Section 4 - Tại Sao Chọn Chúng Tôi? */}
      <section className="section">
        <h2>Tại Sao Chọn Chúng Tôi?</h2>
        <p>
          Với kinh nghiệm và uy tín, chúng tôi cam kết mang đến trải nghiệm tốt
          nhất với dịch vụ linh hoạt, hướng dẫn viên nhiệt tình và nhiều ưu đãi
          hấp dẫn cho khách hàng.
        </p>
        <ul>
          <li>Đội ngũ chuyên nghiệp</li>
          <li>Tour linh hoạt</li>
          <li>Giá cả hợp lý</li>
          <li>Dịch vụ hỗ trợ tận tâm</li>
        </ul>
      </section>

      <Footer />
    </div>
  );
};

export default AboutUs;
