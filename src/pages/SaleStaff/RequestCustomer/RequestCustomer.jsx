import { Table } from "antd";
import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";
import api from "../../../config/axios";

function RequestCustomer() {
  const [datas, setDatas] = useState([]);
  const token = localStorage.getItem("token");

  const fetchData = async (values) => {
    try {
      if (!token) {
        throw new Error("Token not found");
      }
      const response = await api.get(`booking/${bookingId}`, values);
      setDatas(response.data);
    } catch {
      toast.error(response.data);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const columns = [
    {
      title: "Booking ID",
      dataIndex: "bookingId",
      key: "bookingId",
    },
    {
      title: "Customer ID",
      dataIndex: "customerId",
      key: "customerId",
    },
    {
      title: "Tour ID",
      dataIndex: "tourId",
      key: "tourId",
    },
    {
      title: "Payment ID",
      dataIndex: "paymentId",
      key: "paymentId",
    },
    {
      title: "Number of Attendances",
      dataIndex: "numberOfAttendances",
      key: "numberOfAttendances",
    },
    {
      title: "Total Price",
      dataIndex: "totalPrice",
      key: "totalPrice",
      render: (text) => `${text.toLocaleString()} VND`, // Định dạng giá
    },
    {
      title: "Status",
      dataIndex: "status",
      key: "status",
    },
    {
      title: "Created Date",
      dataIndex: "createdDate",
      key: "createdDate",
      render: (text) => new Date(text).toLocaleString(), // Định dạng ngày tháng
    },
  ];

  return (
    <div>
      <h1>RequestCusomer</h1>
      <Table columns={columns} dataSource={datas} />
    </div>
  );
}

export default RequestCustomer;
