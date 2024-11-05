import React, { useEffect, useState } from "react";
import { Button, Spin, Table } from "antd";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../config/axios"; // Adjust the import path as necessary
import { toast, ToastContainer } from "react-toastify";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";

const Sidebar = () => {
  const navigate = useNavigate();

  return (
    <div className="sidebar-profile">
      <ul>
        <li onClick={() => navigate("/orders")}>Đơn đặt hàng</li>
        <li onClick={() => navigate("/history-tour")}>Tour đã đi</li>
      </ul>
    </div>
  );
};

const OrderDetail = () => {
  const { orderId } = useParams();
  const [orderDetail, setOrderDetail] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role"); // Get the user's role
  const navigate = useNavigate();

  const fetchOrderDetail = async () => {
    try {
      const response = await api.get(`order/orderDetail/${orderId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      console.log("Order detail response:", response.data);
      setOrderDetail(response.data); // Directly set the order detail response
      setIsLoading(false);
    } catch (error) {
      console.error("Error fetching order detail:", error.response || error);
      toast.error("Không thể tải thông tin đơn hàng.");
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchOrderDetail();
  }, [orderId]);

  const columns = [
    {
      title: "Loài",
      dataIndex: "species",
      key: "species",
    },
    {
      title: "Mô tả",
      dataIndex: "description",
      key: "description",
    },
    {
      title: "Số lượng",
      dataIndex: "quantity",
      key: "quantity",
    },
    {
      title: "Giá",
      dataIndex: "price",
      key: "price",
    },
  ];

  return (
    <>
      {role === "CUSTOMER" && <Header />}
      <div style={{ display: "flex" }}>
        {role === "CUSTOMER" && <Sidebar />}
        <div style={{ padding: "20px", flexGrow: 1 }}>
          <ToastContainer />
          {isLoading ? (
            <Spin tip="Đang tải thông tin đơn hàng..." />
          ) : orderDetail && orderDetail.length > 0 ? (
            <div>
              <h2 style={{ marginTop: 20 }}>Chi tiết đơn hàng</h2>
              <Table
                columns={columns}
                dataSource={orderDetail}
                rowKey={(record) => record.species}
                pagination={false}
              />
            </div>
          ) : (
            <p>Không tìm thấy thông tin đơn hàng.</p>
          )}
          <Button
            onClick={() =>
              navigate(role === "CUSTOMER" ? "/orders" : "/farm-host")
            }
            style={{ marginTop: 16 }}
          >
            Trở về danh sách đơn hàng
          </Button>
        </div>
      </div>
      {role === "CUSTOMER" && <Footer />}
    </>
  );
};

export default OrderDetail;
