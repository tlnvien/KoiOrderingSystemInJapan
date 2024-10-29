import React, { useState } from "react";
import { Form, Input, Button, message } from "antd";
import api from "../../../config/axios";
import { useForm } from "antd/es/form/Form";

const AssociateBookingTour = () => {
  const [loading, setLoading] = useState(false);
  const [form] = useForm(); // Form instance from Ant Design
  const token = localStorage.getItem("token");

  // Function handle submit form
  const handleSubmit = async (values) => {
    setLoading(true);

    try {
      // Send request with bookingId and tourId from form values
      const response = await api.post(
        `booking/associate?bookingId=${values.bookingId}&tourId=${values.tourId}`,
        {}, // body can be empty
        {
          headers: {
            Accept: "*/*",
            Authorization: `Bearer ${token}`,
          },
        }
      );
      message.success("Liên kết thành công: " + response.data.message);
    } catch (error) {
      const errorMessage =
        error.response?.data?.message || "Đã có lỗi xảy ra. Vui lòng thử lại.";
      message.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Liên kết Mã Booking và Mã Tour</h2>
      <Form form={form} onFinish={handleSubmit} layout="vertical">
        <Form.Item
          label="Mã booking"
          name="bookingId"
          rules={[{ required: true, message: "Vui lòng nhập mã đơn hàng" }]}
        >
          <Input placeholder="Nhập mã đơn hàng" />
        </Form.Item>
        <Form.Item
          label="Mã Tour"
          name="tourId"
          rules={[{ required: true, message: "Vui lòng nhập mã tour" }]}
        >
          <Input placeholder="Nhập mã tour" />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading}>
            Liên Kết
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
};

export default AssociateBookingTour;
