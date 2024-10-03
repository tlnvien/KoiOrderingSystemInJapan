import React, { useState } from "react";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import "./BookingPage.css";
import { FaCreditCard, FaMobileAlt, FaMoneyBillWave } from "react-icons/fa";
import { FaMoneyBillTransfer } from "react-icons/fa6";

const BookingPage = () => {
  const [formData, setFormData] = useState({
    fullName: "",
    phone: "",
    email: "",
    address: "",
    numberOfAdults: 1,
    numberOfChildren: 0,
    paymentMethod: "credit",
    notes: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({ ...prevState, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Booking info:", formData);
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
            <label>Họ và tên</label>
            <input
              type="text"
              name="fullName"
              value={formData.fullName}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group-book">
            <label>Số điện thoại</label>
            <input
              type="tel"
              name="phone"
              value={formData.phone}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group-book">
            <label>Email</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group-book">
            <label>Địa chỉ</label>
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
          <div className="form-group-book">
            <label>Trẻ em</label>
            <select
              name="numberOfChildren"
              value={formData.numberOfChildren}
              onChange={handleChange}
            >
              {[...Array(10).keys()].map((n) => (
                <option key={n} value={n}>
                  {n}
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
                value="credit"
                checked={formData.paymentMethod === "credit"}
                onChange={handleChange}
              />
              <FaCreditCard style={{ marginRight: "15px" }} />
              Thẻ tín dụng
            </label>
          </div>
          <div className="form-group-book">
            <label>
              <input
                type="radio"
                name="paymentMethod"
                value="momo"
                checked={formData.paymentMethod === "momo"}
                onChange={handleChange}
              />
              <FaMobileAlt style={{ marginRight: "15px" }} />
              Thanh toán MOMO
            </label>
          </div>
          <div className="form-group-book">
            <label>
              <input
                type="radio"
                name="paymentMethod"
                value="vnpay"
                checked={formData.paymentMethod === "vnpay"}
                onChange={handleChange}
              />
              <FaMoneyBillWave style={{ marginRight: "15px" }} />
              Thanh toán VNPay
            </label>
          </div>
          <div className="form-group-book">
            <label>
              <input
                type="radio"
                name="paymentMethod"
                value="bank-transfer"
                checked={formData.paymentMethod === "bank-transfer"}
                onChange={handleChange}
              />
              <FaMoneyBillTransfer style={{ marginRight: "15px" }} />
              Thanh toán chuyển khoản ngân hàng
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
        <button type="submit" className="submit-button">
          Thanh Toán
        </button>
      </form>
      <Footer />
    </div>
  );
};

export default BookingPage;
