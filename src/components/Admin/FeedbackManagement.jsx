import React, { useState, useEffect } from "react";
import { Table, Button, Select, Space, message } from "antd";
import axios from "axios";
import Sidebar from "./Admin.jsx";

const { Option } = Select;

const FeedbackManagement = () => {
  const [feedbacks, setFeedbacks] = useState([]);
  const [filteredFeedbacks, setFilteredFeedbacks] = useState([]);
  const [filter, setFilter] = useState("ALL"); // TẤT CẢ, 1, 2, 3, 4, 5
  const token = localStorage.getItem("token");

  const apiUrl = "http://localhost:8082/api/feedback"; // Cập nhật với URL API của bạn

  useEffect(() => {
    const fetchFeedbacks = async () => {
      try {
        const response = await axios.get(apiUrl, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setFeedbacks(response.data);
        setFilteredFeedbacks(response.data);
      } catch (error) {
        message.error("Lỗi khi lấy phản hồi!");
        console.error("Lỗi khi lấy phản hồi:", error);
      }
    };

    fetchFeedbacks();
  }, []);

  const handleFilterChange = (value) => {
    setFilter(value);
    if (value === "ALL") {
      setFilteredFeedbacks(feedbacks);
    } else {
      const rating = parseInt(value);
      setFilteredFeedbacks(
        feedbacks.filter((feedback) => feedback.rating === rating)
      );
    }
  };

  const columns = [
    {
      title: "Mã đánh giá",
      dataIndex: "feedbackId",
      key: "feedbackId",
    },
    {
      title: "Mã Khách hàng",
      dataIndex: "customerId",
      key: "customerId",
    },
    {
      title: "Mã tour",
      dataIndex: "tourId",
      key: "tourId",
    },
    {
      title: "Đánh Giá",
      dataIndex: "rating",
      key: "rating",
      render: (rating) => (
        <span>
          {Array(rating)
            .fill()
            .map((_, index) => (
              <span key={index} style={{ color: "#f39c12" }}>
                &#9733;
              </span>
            ))}
        </span>
      ),
    },
    {
      title: "Bình Luận",
      dataIndex: "comment",
      key: "comment",
    },
    {
      title: "Hành Động",
      key: "actions",
      render: (_, record) => (
        <Space size="middle">
          <Button type="primary" onClick={() => handleEdit(record)}>
            Chỉnh Sửa
          </Button>
          <Button type="danger" onClick={() => handleDelete(record.feedbackId)}>
            Xóa
          </Button>
        </Space>
      ),
    },
  ];

  const handleEdit = (record) => {
    // Thực hiện chức năng chỉnh sửa
    message.info(`Đang chỉnh sửa phản hồi với ID: ${record.feedbackId}`);
  };

  const handleDelete = async (feedbackId) => {
    try {
      await axios.delete(`${apiUrl}/${feedbackId}`);
      setFilteredFeedbacks(
        filteredFeedbacks.filter(
          (feedback) => feedback.feedbackId !== feedbackId
        )
      );
      message.success("Đã xóa phản hồi thành công!");
    } catch (error) {
      message.error("Lỗi khi xóa phản hồi!");
      console.error("Lỗi khi xóa phản hồi:", error);
    }
  };

  return (
    <div className="admin">
      <Sidebar />
      <div className="admin-content">
        <h2>Quản Lý Phản Hồi</h2>
        <Select
          defaultValue="ALL"
          onChange={handleFilterChange}
          style={{ width: 200, marginBottom: 16 }}
        >
          <Option value="ALL">Tất Cả Phản Hồi</Option>
          <Option value="1">1 Sao</Option>
          <Option value="2">2 Sao</Option>
          <Option value="3">3 Sao</Option>
          <Option value="4">4 Sao</Option>
          <Option value="5">5 Sao</Option>
        </Select>
        <Table
          dataSource={filteredFeedbacks}
          columns={columns}
          rowKey="feedbackId"
        />
      </div>
    </div>
  );
};

export default FeedbackManagement;
