import { useState } from "react";
import "./SearchPage.css";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import Tour1 from "./assets/tour1.jpg";
import Tour2 from "./assets/tour2.jpg";
import Tour3 from "./assets/tour3.jpg";
import Tour4 from "./assets/tour4.jpg";
import Tour5 from "./assets/tour5.jpg";
import Tour6 from "./assets/tour6.jpg";
import Tour7 from "./assets/tour7.jpg";
import Tour8 from "./assets/tour8.jpg";
import Tour9 from "./assets/tour9.jpg";
import Tour10 from "./assets/tour10.jpg";
import Image from "./assets/image.jpg";

const SearchPage = () => {
  // Danh sách kết quả gốc (initialResults)
  const initialResults = [
    {
      id: 1,
      title: "Matsue Nishikigoi Center - Dainichi Koi Farm - Otsuka Koi Farm",
      description:
        "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
      price: 20000000, // Giá dưới dạng số
      duration: "7days", // Thời gian tour
      image: Tour1,
    },
    {
      id: 2,
      title: "Tour Nhật Bản Tiêu Chuẩn 2",
      description:
        "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
      price: 15000000, // Giá dưới dạng số
      duration: "5days", // Thời gian tour
      image: Tour2,
    },
    {
      id: 3,
      title: "Tour Khám Phá Tokyo",
      description:
        "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
      price: 18000000,
      duration: "4days",
      image: Tour3,
    },
    {
      id: 4,
      title: "Tour Kyoto Cổ Kính",
      description:
        "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
      price: 22000000,
      duration: "6days",
      image: Tour4,
    },
    {
      id: 5,
      title: "Tour Thưởng Thức Ẩm Thực Nhật Bản",
      description:
        "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
      price: 16000000,
      duration: "3days",
      image: Tour5,
    },
    {
      id: 6,
      title: "Tour Tham Quan Fuji",
      description:
        "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
      price: 12000000,
      duration: "2days",
      image: Tour6,
    },
    {
      id: 7,
      title: "Tour Châu Á Kỳ Diệu",
      description:
        "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
      price: 30000000,
      duration: "10days",
      image: Tour7,
    },
    {
      id: 8,
      title: "Tour Mùa Hoa Anh Đào",
      description:
        "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
      price: 25000000,
      duration: "5days",
      image: Tour8,
    },
    {
      id: 9,
      title: "Tour Đắm Chìm Trong Văn Hóa",
      description:
        "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
      price: 23000000,
      duration: "7days",
      image: Tour9,
    },
    {
      id: 10,
      title: "Tour Biển Okinawa",
      description:
        "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
      price: 21000000,
      duration: "5days",
      image: Tour10,
    },
  ];

  // Trạng thái cho bộ lọc và kết quả tìm kiếm
  const [filters, setFilters] = useState({
    priceRange: "",
    duration: "",
    date: "",
    farm: "",
    location: "",
  });

  const [results, setResults] = useState(initialResults);

  // Xử lý thay đổi bộ lọc
  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  // Hàm tìm kiếm và lọc kết quả
  const handleSearch = () => {
    const filteredResults = initialResults.filter((result) => {
      // Lọc kết quả dựa trên mức giá
      const isPriceMatch =
        filters.priceRange === "" ||
        (filters.priceRange === "under10" && result.price <= 10000000) ||
        (filters.priceRange === "10to20" &&
          result.price > 10000000 &&
          result.price <= 20000000) ||
        (filters.priceRange === "above20" && result.price > 20000000);

      // Lọc kết quả dựa trên thời gian tour
      const isDurationMatch =
        filters.duration === "" || result.duration === filters.duration;

      // Có thể thêm các điều kiện lọc khác...

      return isPriceMatch && isDurationMatch;
    });

    // Cập nhật kết quả mới sau khi lọc
    setResults(filteredResults);
  };

  return (
    <div className="search-page-container">
      <Header />

      {/* Section for Tham Quan Trang Trại Koi */}
      <section className="koi-farm-section">
        <h1>Tham Quan Trang Trại Koi</h1>
        <div className="koi-farm-content">
          <div className="koi-farm-text">
            <p>
              Tour tham quan trang trại cá Koi của KOITRAVEL mang đến cho bạn cơ
              hội khám phá vẻ đẹp độc đáo và quyến rũ của loài cá Koi tại các
              trang trại nổi tiếng nhất Nhật Bản. Bạn sẽ được tận mắt chiêm
              ngưỡng quy trình chăm sóc và nuôi dưỡng từ những chuyên gia hàng
              đầu. Bạn cũng có thể mua cá Koi trực tiếp từ những trang trại, đảm
              bảo chất lượng. Mỗi tour đều được thiết kế linh hoạt giúp bạn có
              thể lựa chọn trang trại, thời gian và ngân sách phù hợp. KOITRAVEL
              sẽ mang đến một hành trình tuyệt vời, đầy cảm hứng, cho những ai
              yêu thích và muốn tìm hiểu sâu về loài cá Koi tuyệt đẹp này.
            </p>
          </div>
          <div className="koi-farm-image">
            <img src={Image} alt="Koi Farm" />
          </div>
        </div>
      </section>

      {/* Main content layout */}
      <div className="search-content">
        {/* Sidebar bộ lọc */}
        <aside className="search-filters">
          <h3>Bộ lọc tìm kiếm</h3>

          {/* Bộ lọc nơi khởi hành */}
          <div className="filter-group">
            <label>Nơi khởi hành</label>
            <select name="location" onChange={handleFilterChange}>
              <option value="">Nơi khởi hành</option>
              <option value="hochiminh">Hồ Chí Minh</option>
              <option value="hanoi">Hà Nội</option>
              <option value="danang">Đà Nẵng</option>
            </select>
          </div>

          {/* Bộ lọc trang trại */}
          <div className="filter-group">
            <label>Trang trại</label>
            <select name="farm" onChange={handleFilterChange}>
              <option value="">Trang trại</option>
              <option value="trangtrai1">Trang trại 1</option>
              <option value="trangtrai2">Trang trại 2</option>
              <option value="trangtrai3">Trang trại 3</option>
            </select>
          </div>

          {/* Bộ lọc mức giá */}
          <div className="filter-group">
            <label>Mức giá</label>
            <select name="priceRange" onChange={handleFilterChange}>
              <option value="">Chọn mức giá</option>
              <option value="under10">Dưới 10 triệu</option>
              <option value="10to20">10 - 20 triệu</option>
              <option value="above20">Trên 20 triệu</option>
            </select>
          </div>

          {/* Bộ lọc thời gian tour */}
          <div className="filter-group">
            <label>Thời gian</label>
            <select name="duration" onChange={handleFilterChange}>
              <option value="">Chọn thời gian</option>
              <option value="3days">3 ngày</option>
              <option value="5days">5 ngày</option>
              <option value="7days">7 ngày</option>
            </select>
          </div>

          {/* Bộ lọc ngày khởi hành */}
          <div className="filter-group">
            <label>Ngày khởi hành</label>
            <input type="date" name="date" onChange={handleFilterChange} />
          </div>

          <button onClick={handleSearch}>Tìm kiếm</button>
        </aside>

        {/* Khu vực hiển thị kết quả */}
        <section className="search-results">
          <h2>Kết quả tìm kiếm</h2>
          <div className="results-list">
            {results.map((result) => (
              <div key={result.id} className="result-item">
                <img
                  src={result.image}
                  alt={result.title}
                  className="result-image"
                />
                <div className="result-content">
                  <h3>{result.title}</h3>
                  {/* Sử dụng .split("\n").map() để xử lý xuống dòng */}
                  {result.description.split("\n").map((line, index) => (
                    <p key={index}>{line}</p>
                  ))}
                  <span>{result.price.toLocaleString()} VND</span>
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>
      <Footer />
    </div>
  );
};

export default SearchPage;
