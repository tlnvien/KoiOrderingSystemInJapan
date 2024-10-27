import React, { useState } from "react";
import { Form, Input, Button, message } from "antd";
import api from "../../../config/axios";

function ReceivedOrder() {
  const [loading, setLoading] = useState(false);

  // Hàm xử lý khi người dùng submit form
  const onFinish = async (values) => {
    const { orderId, status } = values; 
    const token = localStorage.getItem("token"); // Lấy token từ localStorage

    // Nếu token không tồn tại, thông báo lỗi và dừng quá trình
    if (!token) {
      message.error("Authentication token is missing. Please login."); // Hiển thị lỗi yêu cầu đăng nhập
      return;
    }

    setLoading(true); // Bật trạng thái loading để hiển thị biểu tượng đang xử lý

    try {
      await api.post(
        `order/consulting/${orderId}?status=${status}`, 
        {},
        {
          headers: { Authorization: `Bearer ${token}` }, // Gửi token trong header để xác thực
        }
      );
      message.success("Order status updated successfully!");
    } catch (error) {
      console.error("Error updating order status:", error); 
      message.error("Failed to update order status."); 
    } finally {
      setLoading(false); // Tắt trạng thái loading sau khi hoàn thành yêu cầu API
    }
  };

  return (
    <div style={{ maxWidth: "400px", margin: "0 auto", padding: "20px" }}>
      <h2>Cập nhật trạng thái đơn hàng</h2>
      <Form
        layout="vertical" // Bố cục form dọc
        onFinish={onFinish} // Khi nhấn submit thì gọi hàm onFinish
        initialValues={{ status: "RECEIVED" }} // Đặt giá trị mặc định cho status là 'RECEIVED'
      >
        <Form.Item
          label="Order ID" // Nhãn của trường nhập liệu
          name="orderId" // Tên của trường, dùng để lấy dữ liệu khi submit
          rules={[{ required: true, message: "Vui lòng nhập Order ID!" }]} // Quy định trường này bắt buộc phải nhập
        >
          <Input placeholder="Nhập Order ID" /> 
        </Form.Item>

        <Form.Item
          label="Trạng thái" // Nhãn của trường nhập liệu
          name="status" // Tên của trường, dùng để lấy dữ liệu khi submit
        >
          <Input placeholder="RECEIVED" disabled /> {/* Trường này có giá trị là 'RECEIVED' và không thể chỉnh sửa */}
        </Form.Item>

        <Form.Item>
          {/* Nút submit */}
          <Button type="primary" htmlType="submit" loading={loading} block>
            Cập nhật trạng thái
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
}

export default ReceivedOrder;
