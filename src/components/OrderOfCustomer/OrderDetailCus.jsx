import React, { useEffect, useState } from "react";
import { Button, Descriptions, Spin, Table } from "antd";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../config/axios"; // Adjust the import path as necessary
import { toast, ToastContainer } from "react-toastify";

const OrderDetail = () => {
  const { orderId } = useParams();
  const [orderDetail, setOrderDetail] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const token = localStorage.getItem("token");
  const navigate = useNavigate();

  const fetchOrderDetail = async () => {
    try {
      const response = await api.get(`orders/${orderId}`, {
        // Adjust the endpoint as necessary
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setOrderDetail(response.data);
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
    <div>
      <ToastContainer />
      {isLoading ? (
        <Spin tip="Đang tải thông tin đơn hàng..." />
      ) : orderDetail ? (
        <div>
          <Descriptions title="Thông tin chi tiết đơn hàng" bordered>
            <Descriptions.Item label="Mã đơn hàng">
              {orderDetail.orderId}
            </Descriptions.Item>
            <Descriptions.Item label="Tên khách hàng">
              {orderDetail.customerName}
            </Descriptions.Item>
            <Descriptions.Item label="Địa chỉ">
              {orderDetail.customerAddress}
            </Descriptions.Item>
            <Descriptions.Item label="Ngày đặt">
              {orderDetail.orderDate}
            </Descriptions.Item>
            <Descriptions.Item label="Ngày giao">
              {orderDetail.deliveredDate}
            </Descriptions.Item>
            <Descriptions.Item label="Tổng tiền">
              {orderDetail.totalPrice}
            </Descriptions.Item>
            <Descriptions.Item label="Trạng thái">
              {orderDetail.status}
            </Descriptions.Item>
            <Descriptions.Item label="Ghi chú">
              {orderDetail.note}
            </Descriptions.Item>
          </Descriptions>

          <h2 style={{ marginTop: 20 }}>Chi tiết đơn hàng</h2>
          <Table
            columns={columns}
            dataSource={orderDetail.orderDetails}
            rowKey={(record) => record.species}
            pagination={false}
          />
        </div>
      ) : (
        <p>Không tìm thấy thông tin đơn hàng.</p>
      )}
      <Button onClick={() => navigate("/orders")} style={{ marginTop: 16 }}>
        Trở về danh sách đơn hàng
      </Button>
    </div>
  );
};

export default OrderDetail;
