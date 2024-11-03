import React, { useState, useEffect } from "react";
import { Table, Select, Form, notification, Button } from "antd";
import { useNavigate } from "react-router-dom";
import api from "../../config/axios";

const { Option } = Select;

const FarmHost = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const token = localStorage.getItem("token");
  const farmId = localStorage.getItem("farmId");
  const userId = localStorage.getItem("userId");
  const check = "SHIPPING";

  const navigate = useNavigate();

  const fetchOrders = async () => {
    setLoading(true); // Show loading spinner
    try {
      const response = await api.get(`order/farmHost`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setOrders(response.data);
    } catch (error) {
      console.error("Error fetching orders:", error);
    } finally {
      setLoading(false); // Hide loading spinner
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [farmId, token]);

  const handleStatusChange = async (orderId, newStatus) => {
    try {
      await api.post(
        `order/farmHost/${orderId}?status=${check}`,
        { status: newStatus },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      notification.success({
        message: "Success",
        description: "Order status updated successfully.",
      });
      fetchOrders();
    } catch (error) {
      notification.error({
        message: "Error",
        description: "Failed to update order status.",
      });
      console.error("Error updating order status:", error);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("farmId");
    localStorage.removeItem("userId");
    navigate("/login");
  };

  const handleViewDetails = (orderId) => {
    navigate(`/order/${orderId}`);
  };

  const columns = [
    {
      title: "Mã đơn hàng",
      dataIndex: "orderId",
      key: "orderId",
      render: (text) => <strong>#{text}</strong>,
    },
    {
      title: "Tên khách hàng",
      dataIndex: "customerName",
      key: "customerName",
    },
    {
      title: "Trạng thái",
      dataIndex: "status",
      key: "status",
      render: (text, record) => (
        <Form.Item>
          <Select
            value={text}
            onChange={(value) => handleStatusChange(record.orderId, value)}
          >
            <Option value="SHIPPING">SHIPPING</Option>
          </Select>
        </Form.Item>
      ),
    },
    // {
    //   title: "Action",
    //   key: "action",
    //   render: (_, record) => (
    //     <Button type="link" onClick={() => handleViewDetails(record.orderId)}>
    //       Chi tiết
    //     </Button>
    //   ),
    // },
  ];

  if (loading) {
    return <p>Loading orders...</p>;
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
        <p style={{ color: "blue", fontSize: "26px" }}>Hello, {userId}</p>
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
