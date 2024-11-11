import React from "react";
import "./AboutUs.css";
import companyImage from "./assets/company.jpg";
import auctionImage from "./assets/auctionImage.jpg";
import teamImage from "./assets/teamImage.jpg";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";

const AboutUs = () => {
  return (
    <div className="about-us-container">
      <Header />

      <main>
        {/* Giới thiệu ngắn gọn */}
        <section className="intro">
          <h1 className="title">Giới thiệu về Koi Farm Travel</h1>
          <p className="intro-text">
            Trải nghiệm tuyệt vời cho những người yêu thích cá Koi với những chuyến tham quan độc đáo đến các trang trại cá Koi và tham gia buổi đấu giá danh tiếng tại Nhật Bản. Chúng tôi cung cấp một cơ hội hiếm hoi để kết nối với văn hóa và nghệ thuật nuôi cá Koi qua các chương trình đặc biệt và chuyên nghiệp, giúp bạn hiểu thêm về nguồn gốc và giá trị tinh hoa của cá Koi.
          </p>
        </section>

        {/* Phần giới thiệu về công ty */}
        <section className="company-intro">
          <h2 className="section-title">Về Chúng Tôi</h2>
          <div className="content-wrapper">
            <img
              src={companyImage}
              alt="Trang trại cá Koi"
              className="about-image"
            />
            <p className="description">
              Chúng tôi chuyên cung cấp những trải nghiệm du lịch độc đáo dành cho những người yêu thích cá Koi. Với hơn 10 năm kinh nghiệm, chúng tôi đã giúp hàng ngàn khách hàng từ khắp nơi trên thế giới đến với Nhật Bản để trải nghiệm văn hóa cá Koi đậm đà bản sắc. Đội ngũ của chúng tôi bao gồm những chuyên gia nhiệt huyết, am hiểu sâu sắc về nghệ thuật nuôi cá Koi, sẵn sàng mang đến cho bạn hành trình khám phá văn hóa Nhật Bản với những giá trị độc đáo và chân thực nhất.
            </p>
          </div>
        </section>

        {/* Phần sứ mệnh và tầm nhìn */}
        <section className="mission-vision">
          <h2 className="section-title">Sứ mệnh & Tầm nhìn</h2>
          <p className="description">
            Sứ mệnh của chúng tôi là xây dựng một cầu nối giữa cộng đồng yêu thích cá Koi trên toàn cầu và các trang trại cá Koi uy tín tại Nhật Bản. Chúng tôi mong muốn không chỉ giới thiệu cá Koi đến mọi người, mà còn truyền tải tinh thần yêu mến, chăm sóc, và bảo tồn cá Koi như một phần quan trọng của văn hóa Nhật Bản. Chúng tôi tin tưởng rằng, qua những chuyến tham quan đặc sắc, bạn sẽ cảm nhận được vẻ đẹp và giá trị vô song của cá Koi, đồng thời góp phần gìn giữ và phát triển nghệ thuật này.
          </p>
        </section>

        {/* Phần dịch vụ */}
        <section className="services">
          <h2 className="section-title">Dịch vụ của chúng tôi</h2>
          <ul className="service-list">
            <li>
              <strong>Tham quan trang trại cá Koi tại Nhật Bản:</strong> Khám phá quá trình nuôi dưỡng cá Koi, từ giống cá đặc biệt đến các kỹ thuật chăm sóc truyền thống của người Nhật.
            </li>
            <li>
              <strong>Tham gia các buổi đấu giá cá Koi tại trang trại:</strong> Cơ hội sở hữu những chú cá Koi đẳng cấp, được chọn lựa kỹ lưỡng từ các trang trại uy tín.
            </li>
            <li>
              <strong>Đặt và vận chuyển cá Koi về Việt Nam:</strong> Chúng tôi đảm bảo quá trình vận chuyển an toàn, hiệu quả, và chuyên nghiệp từ Nhật Bản về Việt Nam.
            </li>
            <li>
              <strong>Trải nghiệm văn hóa Nhật Bản:</strong> Khám phá những nét đẹp truyền thống Nhật Bản như trà đạo, nghệ thuật Ikebana, cùng các hoạt động liên quan đến cá Koi.
            </li>
          </ul>
        </section>

        {/* Phần đấu giá */}
        <section className="auctions">
          <h2 className="section-title">Tham gia Đấu Giá Cá Koi</h2>
          <div className="content-wrapper">
            <img
              src={auctionImage}
              alt="Buổi đấu giá cá Koi"
              className="about-image"
            />
            <p className="description">
              Những buổi đấu giá cá Koi tại Nhật Bản là sự kiện được nhiều người mong đợi, thu hút các nhà sưu tầm và người yêu cá Koi từ khắp nơi trên thế giới. Chúng tôi giúp bạn tiếp cận với các buổi đấu giá cá Koi độc quyền, mang lại cơ hội sở hữu những giống cá Koi đẹp và độc nhất. Với sự hỗ trợ của chúng tôi, bạn có thể tham gia đấu giá và lựa chọn cho mình những chú cá Koi ưng ý từ các trang trại hàng đầu.
            </p>
          </div>
        </section>

        {/* Phần đội ngũ */}
        <section className="team">
          <h2 className="section-title">Đội ngũ của chúng tôi</h2>
          <div className="content-wrapper">
            <img
              src={teamImage}
              alt="Đội ngũ của chúng tôi"
              className="about-image"
            />
            <p className="description">
              Đội ngũ của chúng tôi bao gồm những chuyên gia đam mê về cá Koi và văn hóa Nhật Bản, với nhiều năm kinh nghiệm trong lĩnh vực này. Chúng tôi sẵn sàng hỗ trợ bạn trong mọi khâu của chuyến đi, từ thông tin về cá Koi, hướng dẫn đấu giá đến hỗ trợ vận chuyển và chăm sóc cá Koi khi về đến Việt Nam. Mỗi thành viên trong đội ngũ đều là những người yêu thích và am hiểu sâu sắc về văn hóa cá Koi, mong muốn mang đến cho bạn trải nghiệm tuyệt vời và khó quên.
            </p>
          </div>
        </section>

        {/* Phần đánh giá từ khách hàng */}
        <section className="testimonials">
          <h2 className="section-title">Khách hàng nói gì về chúng tôi</h2>
          <blockquote className="testimonial">
            "Chuyến tham quan cá Koi tại Nhật Bản là một trải nghiệm tuyệt vời! Đội ngũ tận tình và dịch vụ chuyên nghiệp đã giúp tôi tiếp cận với những giống cá Koi quý giá. Cảm ơn Koi Tour!" - <strong>Nguyễn Văn A</strong>
          </blockquote>
          <blockquote className="testimonial">
            "Đội ngũ Koi Tour đã giúp tôi sở hữu một chú cá Koi đặc biệt từ một trang trại nổi tiếng. Sự tận tâm và chuyên nghiệp của họ đã để lại ấn tượng sâu sắc." - <strong>Trần Thị B</strong>
          </blockquote>
        </section>
      </main>

      <Footer />
    </div>
  );
};

export default AboutUs;
