import React, { useEffect, useState } from "react";
import { Button, Table, Spin } from "antd";
import { useNavigate } from "react-router-dom";
import api from "../../config/axios"; // Adjust the import path as necessary
import { toast, ToastContainer } from "react-toastify";
import { ShoppingCartOutlined, StarOutlined } from "@ant-design/icons";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";

// Sidebar component for navigation
const Sidebar = () => {
  const navigate = useNavigate();

  return (
    <div className="sidebar-profile">
      <ul>
        <li onClick={() => navigate("/orders")}>
          <ShoppingCartOutlined style={{ marginRight: "10px" }} /> Đơn đặt hàng
        </li>
        <li onClick={() => navigate("/history-tour")}>
          <StarOutlined style={{ marginRight: "10px" }} /> Tour đã đi
        </li>
      </ul>
    </div>
  );
};

const OrderList = () => {
  const [orders, setOrders] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const token = localStorage.getItem("token");
  const navigate = useNavigate();
  const userId = localStorage.getItem("userId");

  const fetchOrders = async () => {
    try {
      const response = await api.get(`order/customer/${userId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setOrders(response.data);
      setIsLoading(false);
    } catch (error) {
      console.error("Error fetching orders:", error.response || error);
      toast.error("Không thể tải danh sách đơn hàng.");
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  // Define columns for the orders table
  const columns = [
    {
      title: "Mã đơn hàng",
      dataIndex: "orderId",
      key: "orderId",
    },
    {
      title: "Tên khách hàng",
      dataIndex: "customerName",
      key: "customerName",
    },
    {
      title: "Ngày đặt",
      dataIndex: "orderDate",
      key: "orderDate",
    },
    {
      title: "Tổng tiền",
      dataIndex: "totalPrice",
      key: "totalPrice",
    },
    {
      title: "Hành động",
      key: "action",
      render: (text, record) => (
        <Button
          type="primary"
          onClick={() => navigate(`/orders/${record.orderId}`)}
        >
          Xem chi tiết
        </Button>
      ),
    },
  ];

  return (
    <div style={{ display: "flex" }}>
      <Sidebar />
      <div style={{ padding: "20px", flexGrow: 1 }}>
        <ToastContainer />
        {isLoading ? (
          <Spin tip="Đang tải danh sách đơn hàng..." />
        ) : (
          <Table
            columns={columns}
            dataSource={orders}
            rowKey="orderId" // Unique key for each order
          />
        )}
      </div>
    </div>
  );
};

export default OrderList;
