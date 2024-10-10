// PaymentSuccessPage.js
import React from "react";
import { useLocation } from "react-router-dom";

const PaymentSuccessPage = () => {
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const transactionId = queryParams.get("vnp_TransactionNo");
  const amount = queryParams.get("vnp_Amount") / 100;

  return (
    <div className="payment-success-container">
      <h2>Thanh toán thành công!</h2>
      <p>Mã giao dịch: {transactionId}</p>
      <p>Số tiền: {amount} VND</p>
      <p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>
    </div>
  );
};

export default PaymentSuccessPage;
