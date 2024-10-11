import React, { useState } from "react";
import axios from "axios";
import "./Payment.css";

const PaymentPage = () => {
  const [amount, setAmount] = useState("");
  const [description, setDescription] = useState("");

  const handlePayment = async () => {
    if (!amount || !description) {
      alert("Vui lòng điền đầy đủ thông tin!");
      return;
    }

    try {
      // Gửi yêu cầu tới backend để lấy URL thanh toán
      const response = await axios.post("http://localhost:8080/api/payment", {
        amount: amount * 100, // VNPay yêu cầu giá trị là đơn vị VND * 100
        description: description,
      });

      if (response.data && response.data.paymentUrl) {
        // Điều hướng đến URL thanh toán
        window.location.href = response.data.paymentUrl;
      } else {
        alert("Không thể khởi tạo URL thanh toán!");
      }
    } catch (error) {
      console.error("Lỗi khi tạo yêu cầu thanh toán:", error);
      alert("Đã xảy ra lỗi khi tạo yêu cầu thanh toán!");
    }
  };

  return (
    <div className="payment-container">
      <h2>Thanh toán với VNPay</h2>
      <div className="payment-form">
        <label htmlFor="amount">Số tiền (VND):</label>
        <input
          type="number"
          id="amount"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          placeholder="Nhập số tiền"
          required
        />

        <label htmlFor="description">Mô tả giao dịch:</label>
        <input
          type="text"
          id="description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Nhập mô tả giao dịch"
          required
        />

        <button onClick={handlePayment} className="payment-button">
          Thanh toán
        </button>
      </div>
    </div>
  );
};

export default PaymentPage;
