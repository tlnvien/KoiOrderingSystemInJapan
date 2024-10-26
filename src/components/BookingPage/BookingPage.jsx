import React, { useState } from "react";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import "./BookingPage.css";
import axios from "axios";
import { FaCreditCard, FaMoneyBill } from "react-icons/fa";

const BookingPage = () => {
  const [formData, setFormData] = useState({
    fullName: "",
    phone: "",
    email: "",
    address: "",
    numberOfAdults: 1,
    numberOfChildren: 0,
    paymentMethod: "vnpay", // Default to VNPay
    notes: "",
  });

  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({ ...prevState, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (
      !formData.fullName ||
      !formData.phone ||
      !formData.email ||
      !formData.address
    ) {
      alert("Vui lòng nhập đầy đủ thông tin!");
      return;
    }

    setLoading(true);

    try {
      // Call the backend API to get VNPay URL
      const response = await axios.post(
        "http://localhost:8082/api/booking/paymentUrl",
        { bookingId: "someBookingId" } // Replace with actual booking ID handling
      );

      const vnpayUrl = response.data; // API should return the VNPay URL
      if (vnpayUrl) {
        // Redirect to VNPay for payment
        window.location.href = vnpayUrl;
      } else {
        throw new Error("VNPay URL not found.");
      }
    } catch (error) {
      console.error("Error initiating payment", error);
      alert("Có lỗi xảy ra khi khởi tạo thanh toán. Vui lòng thử lại.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="booking-page">
      <Header />
      <h1>Đặt Tour</h1>

      <div className="booking-process">
        <div className="process-steps">
          <div className="process-step">
            <div className="circle-icon">
              <span className="icon-text">📝</span>
            </div>
            <p>Nhập thông tin</p>
          </div>
          <span className="arrow">→</span>
          <div className="process-step">
            <div className="circle-icon">
              <span className="icon-text">💳</span>
            </div>
            <p>Thanh toán</p>
          </div>
          <span className="arrow">→</span>
          <div className="process-step">
            <div className="circle-icon">
              <span className="icon-text">✅</span>
            </div>
            <p>Hoàn tất</p>
          </div>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="booking-form">
        <section className="contact-info">
          <h2>Thông tin liên hệ</h2>
          <div className="form-group-book">
            <label>Họ và tên:</label>
            <input
              type="text"
              name="fullName"
              value={formData.fullName}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group-book">
            <label>Số điện thoại:</label>
            <input
              type="tel"
              name="phone"
              value={formData.phone}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group-book">
            <label>Email:</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group-book">
            <label>Địa chỉ:</label>
            <input
              type="text"
              name="address"
              value={formData.address}
              onChange={handleChange}
              required
            />
          </div>
        </section>

        <section className="passenger-info">
          <h2>Hành khách</h2>
          <div className="form-group-book">
            <label>Người lớn</label>
            <select
              name="numberOfAdults"
              value={formData.numberOfAdults}
              onChange={handleChange}
            >
              {[...Array(10).keys()].map((n) => (
                <option key={n + 1} value={n + 1}>
                  {n + 1}
                </option>
              ))}
            </select>
          </div>
        </section>

        <section className="payment-method">
          <h2>Các hình thức thanh toán</h2>
          <div className="form-group-book">
            <label>
              <input
                type="radio"
                name="paymentMethod"
                value="vnpay"
                checked={formData.paymentMethod === "vnpay"}
                onChange={handleChange}
              />
              <FaMoneyBill style={{ marginRight: "15px" }} />
              Thanh toán VNPay
            </label>
          </div>
        </section>

        <section className="notes-section">
          <h2>Ghi chú</h2>
          <textarea
            name="notes"
            value={formData.notes}
            onChange={handleChange}
            placeholder="Nhập ghi chú nếu có"
          ></textarea>
        </section>

        <div className="form-group-book">
          <label>
            <input type="checkbox" required />
            Tôi đồng ý với điều khoản khi đăng ký online
          </label>
        </div>
        <button type="submit" className="submit-button" disabled={loading}>
          {loading ? "Đang xử lý..." : "Thanh Toán"}
        </button>
      </form>
      <Footer />
    </div>
  );
};

export default BookingPage;
