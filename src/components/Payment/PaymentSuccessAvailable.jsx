import { Button, message, Result } from "antd";
import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import useGetParams from "../../hooks/useGetParam";
import api from "../../config/axios";

const PaymentSuccessAvailable = () => {
  const params = useGetParams();
  const bookingId = params("Id");
  const vnp_TransactionStatus = params("vnp_TransactionStatus");
  const navigate = useNavigate();

  const postBookingId = async () => {
    const token = localStorage.getItem("token"); // Make sure you retrieve the token
    // Gọi API thứ hai
    try {
      const responseAvailable = await api.post(
        `booking/available/transaction/${bookingId}`,
        {}, // Request body trống
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      // Thông báo thành công khi call API thành công
      message.success("Giao dịch kiểm tra khả dụng thành công!");
    } catch (error) {
      message.error(
        "Lỗi trong giao dịch kiểm tra khả dụng: " + error.response?.data
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

export default PaymentSuccessAvailable;
