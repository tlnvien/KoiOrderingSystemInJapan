import React, { useState, useEffect } from "react";
import { Form, Input, Button, message, Select } from "antd";
import api from "../../../config/axios";

function DeliveryOrder() {
  const [loading, setLoading] = useState(false);

  // Hàm xử lý khi người dùng submit form
  const onFinish = async (values) => {
    const { orderId, status } = values; // Chỉ lấy giá trị status từ form
    const token = localStorage.getItem("token");

    if (!token) {
      message.error("Authentication token is missing. Please login.");
      return;
    }

    setLoading(true);

    try {
      await api.post(
        `order/delivering/${orderId}?status=${status}`, // Đường dẫn API
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      message.success("Order status updated successfully!");
    } catch (error) {
      console.error("Error updating order status:", error);
      message.error("Failed to update order status.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: "400px", margin: "0 auto", padding: "20px" }}>
      <h2>Cập nhật trạng thái giao hàng</h2>
      <Form
        layout="vertical"
        onFinish={onFinish}
        initialValues={{ status: "DELIVERED" }}
      >
        <Form.Item
          label="Order ID" // Nhãn của trường nhập liệu
          name="orderId" // Tên của trường, dùng để lấy dữ liệu khi submit
          rules={[{ required: true, message: "Vui lòng nhập Order ID!" }]} // Quy định trường này bắt buộc phải nhập
        >
          <Input placeholder="Nhập Order ID" />
        </Form.Item>

        <Form.Item
          label="Trạng thái" // Nhãn của trường Trạng thái
          name="status"
        >
          <Select placeholder="Chọn trạng thái">
            <Select.Option value="DELIVERING">DELIVERING</Select.Option>
            <Select.Option value="DELIVERED">DELIVERED</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading} block>
            Cập nhật trạng thái
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
}

export default DeliveryOrder;
