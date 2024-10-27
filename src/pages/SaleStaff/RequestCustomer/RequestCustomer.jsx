import { Table, Button, Empty } from "antd";
import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";
import api from "../../../config/axios";

function RequestCustomer() {
  const [dataSource, setDataSource] = useState([]);
  const token = localStorage.getItem("token");

  const fetchData = async (values) => {
    try {
      if (!token) {
        throw new Error("Token không tồn tại. Vui lòng đăng nhập lại.");
      }
      const response = await api.get("booking/requests", values, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      console.log(response.data);
      // Ensure response.data is an array
      setDataSource(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      toast.error(error.response?.data || "Failed to fetch data.");
    }
  };

  const handleTakeRequest = async (bookingId) => {
    try {
      const response = await api.post(`booking/take/${bookingId}`, null, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      toast.success("Nhận yêu cầu thành công!");
      fetchData(); // Refresh data after taking the request
    } catch (error) {
      toast.error(error.response?.data || "Không thể nhận yêu cầu.");
    }
  };

  useEffect(() => {
    fetchData(); // Tải dữ liệu lần đầu
  }, []);

  const columns = [
    {
      title: "Booking ID",
      dataIndex: "bookingId",
      key: "bookingId",
    },
    {
      title: "Customer Name",
      dataIndex: "customerName",
      key: "customerName",
    },
    {
      title: "Phone",
      dataIndex: "phone",
      key: "phone",
    },
    {
      title: "Tour ID",
      dataIndex: "tourId",
      render: (text) => (text ? text : "Chưa có tour"),
    },
    {
      title: "Payment Status",
      dataIndex: "paymentId",
      render: (text) => (text === "Not Paid Yet!" ? "Chưa thanh toán" : text),
    },
    {
      title: "Description",
      dataIndex: "description",
      key: "description",
    },
    {
      title: "Has Visa",
      dataIndex: "hasVisa",
      render: (text) => (text ? "Có" : "Không"),
    },
    {
      title: "Number of Attendances",
      dataIndex: "numberOfAttendances",
    },
    {
      title: "Total Price",
      dataIndex: "totalPrice",
      render: (text) => `${text.toLocaleString()} VND`,
    },
    {
      title: "Status",
      dataIndex: "status",
    },
    {
      title: "Created Date",
      dataIndex: "createdDate",
      render: (text) => new Date(text).toLocaleString(),
    },
    {
      title: "Actions",
      render: (record) => (
        <Button type="primary" onClick={() => handleTakeRequest(record.bookingId)}>
          Nhận yêu cầu
        </Button>
      ),
    },
  ];

  return (
    <div>
      <h1>Request Customer</h1>
      {dataSource.length > 0 ? (
        <Table columns={columns} dataSource={dataSource} />
      ) : (
        <Empty description="Request is empty!" />
      )}
    </div>
  );
}

export default RequestCustomer;