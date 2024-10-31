import React, { useState, useEffect } from "react";
import axios from "axios";
import { Table, Select, Form, notification, Button } from "antd";
import { useNavigate } from "react-router-dom"; // Đổi từ useHistory sang useNavigate
import api from "../../config/axios";

const { Option } = Select;

const FarmHost = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const token = localStorage.getItem("token");
  const farmId = localStorage.getItem("farmId");
  const userId = localStorage.getItem("userId");
  const apiUrl = `order/farmHost/${farmId}`;
  const navigate = useNavigate(); // Sử dụng useNavigate

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const response = await api.get(apiUrl, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setOrders(response.data);
        setLoading(false);
      } catch (error) {
        console.error("Lỗi khi lấy đơn hàng:", error);
        setLoading(false);
      }
    };

    fetchOrders();
  }, [apiUrl, token]);

  const handleStatusChange = async (orderId, newStatus) => {
    try {
      await api.post(
        `order/farmHost/${orderId}`,
        { status: newStatus },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setOrders((prevOrders) =>
        prevOrders.map((order) =>
          order.id === orderId ? { ...order, status: newStatus } : order
        )
      );
      notification.success({
        message: "Thành công",
        description: "Cập nhật trạng thái đơn hàng thành công.",
      });
    } catch (error) {
      notification.error({
        message: "Lỗi",
        description: "Cập nhật trạng thái đơn hàng thất bại.",
      });
      console.error("Lỗi khi cập nhật trạng thái đơn hàng:", error);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("farmId");
    localStorage.removeItem("userId");
    navigate("/login"); // Dùng navigate để chuyển hướng đến trang đăng nhập
  };

  const columns = [
    {
      title: "Mã Đơn Hàng",
      dataIndex: "orderId",
      key: "orderId",
      render: (text) => <strong>#{text}</strong>,
    },
    {
      title: "Tên Khách Hàng",
      dataIndex: "customerName",
      key: "customerName",
    },
    {
      title: "Trạng Thái",
      dataIndex: "status",
      key: "status",
      render: (text, record) => (
        <Form.Item>
          <Select
            value={text}
            onChange={(value) => handleStatusChange(record.id, value)}
          >
            {/* <Option value="PENDING">Chờ xử lý</Option> */}
            {/* <Option value="CONFIRMED">Đã xác nhận</Option> */}
            <Option value="SHIPPING">Đã giao hàng</Option>
            {/* <Option value="COMPLETED">Hoàn thành</Option> */}
            {/* <Option value="CANCELLED">Đã hủy</Option> */}
          </Select>
        </Form.Item>
      ),
    },
  ];

  if (loading) {
    return <p>Đang tải đơn hàng...</p>;
  }

  return (
    <div style={{ padding: "20px" }}>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <p style={{ color: "blue", fontSize: "26px" }}>Xin chào {userId}</p>
        <Button type="primary" onClick={handleLogout}>
          Đăng xuất
        </Button>
      </div>
      <h2 style={{ textAlign: "center", marginBottom: "20px" }}>
        Quản lý đơn hàng của trang trại
      </h2>
      <Table
        columns={columns}
        dataSource={orders}
        rowKey={(record) => record.orderId}
        pagination={{ pageSize: 10 }}
        bordered
      />
    </div>
  );
};

export default FarmHost;
