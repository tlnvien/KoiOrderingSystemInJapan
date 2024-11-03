import { Button, message, Result } from "antd";
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
    const token = localStorage.getItem("token"); // Make sure you retrieve the token

    // First API call
    try {
      const responseRequest = await api.post(
        `booking/transaction/${bookingId}`,
        {}, // Request body trống
        {
          headers: {
            Authorization: `Bearer ${token}`, // Đưa token vào headers
          },
        }
      );
      // Thông báo thành công khi call API thành công
      message.success("Giao dịch đặt chỗ thành công!");
    } catch (error) {
      console.log("Lỗi trong giao dịch đặt chỗ: " + error.response?.data);
    }

    // Gọi API thứ ba
    try {
      const responseOrderFirst = await api.post(
        `order/transactions/first/${orderId}`,
        {}, // Request body trống
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      // Thông báo thành công khi call API thành công
      message.success("Giao dịch trước một phần của đơn hàng thành công!");
    } catch (error) {
      console.log(
        "Lỗi trong giao dịch đơn hàng đầu tiên: " + error.response?.data
      );
    }

    // Gọi API thứ tư
    try {
      const responseOrderFinal = await api.post(
        `order/transactions/final/${orderId}`,
        {}, // Request body trống
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      // Thông báo thành công khi call API thành công
      message.success("Giao dịch phần còn lại của đơn hàng thành công!");
    } catch (error) {
      console.log(
        "Lỗi trong giao dịch phần còn lại của đơn hàng: " + error.response?.data
      );
    }
  };

  const handleSubmit = () => {
    navigate("/");
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
