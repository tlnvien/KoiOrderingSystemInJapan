// PaymentSuccessPage.js
import { Button, Result } from "antd";
import axios from "axios";
import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import useGetParams from "../../hooks/useGetParam";
import api from "../../config/axios";

const PaymentSuccessPage = () => {
  const params = useGetParams();
  const bookingId = params("Id");
  const orderId = params("Id");
  const vnp_TransactionStatus = params("vnp_TransactionStatus");
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const postBookingId = async () => {
    try {
      const responseRequest = await api.post(
        `booking/transaction/${bookingId}`,
        {}, // Empty request body since no data is being sent
        {
          headers: {
            Authorization: `Bearer ${token}`, // Include the token in headers
          },
        }
      );

      const responseAvailable = await api.post(
        `booking/available/transaction/${bookingId}`,
        {}, // Dữ liệu gửi đi (nếu không có thì để là một object rỗng)
        {
          headers: {
            Authorization: `Bearer ${token}`, // Bao gồm token trong headers
          },
        }
      );
      const responseOrderFirst = await api.post(
        `order/transactions/first/${orderId}`,
        {}, // Dữ liệu gửi đi (nếu không có thì để là một object rỗng)
        {
          headers: {
            Authorization: `Bearer ${token}`, // Bao gồm token trong headers
          },
        }
      );
      const responseOrderFinal = await api.post(
        `order/transactions/final/$${orderId}`,
        {}, // Dữ liệu gửi đi (nếu không có thì để là một object rỗng)
        {
          headers: {
            Authorization: `Bearer ${token}`, // Bao gồm token trong headers
          },
        }
      );
    } catch (error) {
      console.log(error);
    }
  };

  const handleSubmit = () => {
    navigate(`/`);
  };

  useEffect(() => {
    if (vnp_TransactionStatus === "00") {
      postBookingId();
    } else {
      alert("Payment failed! Please try again.");
    }
  }, []);
  return (
    <Result
      status="success"
      title="Thanh toán thành công"
      subTitle="Chúng quý khách thượng lộ bình an"
      extra={[
        <Button type="primary" key="console" onClick={handleSubmit}>
          Quay lại trang chủ
        </Button>,
      ]}
    />
  );
};

export default PaymentSuccessPage;
