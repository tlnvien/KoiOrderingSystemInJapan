// PaymentPage.js
import React, { useState } from "react";
import "./Payment.css";

const PaymentPage = () => {
  const [amount, setAmount] = useState("");
  const [description, setDescription] = useState("");

  const handlePayment = () => {
    if (!amount || !description) {
      alert("Vui lòng điền đầy đủ thông tin!");
      return;
    }

    const paymentUrl = `https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=${
      amount * 100
    }&vnp_TxnRef=${Date.now()}&vnp_OrderInfo=${description}&vnp_Locale=vn&vnp_ReturnUrl=http://localhost:3000/payment-success&vnp_Version=2.1.0&vnp_Command=pay&vnp_CurrCode=VND&vnp_TmnCode=YOUR_TMNCODE_HERE&vnp_IpAddr=127.0.0.1&vnp_CreateDate=${new Date()
      .toISOString()
      .slice(0, 19)
      .replace("T", "")}`;

    window.location.href = paymentUrl;
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
