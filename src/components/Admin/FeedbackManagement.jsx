import React, { useState, useEffect } from "react";
import { Table, Button, Select, Space, message } from "antd";
import axios from "axios";
import Sidebar from "./Admin.jsx";

const { Option } = Select;

const FeedbackManagement = () => {
  const [feedbacks, setFeedbacks] = useState([]);
  const [filteredFeedbacks, setFilteredFeedbacks] = useState([]);
  const [filter, setFilter] = useState("ALL"); // Default to "ALL"
  const [selectedTourId, setSelectedTourId] = useState(""); // For tour feedback
  const [selectedCustomerId, setSelectedCustomerId] = useState(""); // For customer feedback
  const [customers, setCustomers] = useState([]); // List of customers
  const [tours, setTours] = useState([]); // List of tours
  const token = localStorage.getItem("token");

  const apiUrl = "http://localhost:8082/api/feedback"; // Update with your API URL
  const customersApiUrl = "http://localhost:8082/api/info"; // API URL for customers
  const toursApiUrl = "http://localhost:8082/api/tour"; // API URL for tours

  useEffect(() => {
    const fetchFeedbacks = async () => {
      try {
        const response = await axios.get(`${apiUrl}/manage/all`, {
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

    const fetchCustomers = async () => {
      try {
        const response = await axios.get(customersApiUrl, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setCustomers(response.data); // Assuming response.data contains an array of customer objects
      } catch (error) {
        message.error("Lỗi khi lấy danh sách khách hàng!");
        console.error("Lỗi khi lấy khách hàng:", error);
      }
    };

    const fetchTours = async () => {
      try {
        const response = await axios.get(toursApiUrl, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setTours(response.data); // Assuming response.data contains an array of tour objects
      } catch (error) {
        message.error("Lỗi khi lấy danh sách tour!");
        console.error("Lỗi khi lấy tour:", error);
      }
    };

    fetchFeedbacks();
    fetchCustomers();
    fetchTours();
  }, [apiUrl, customersApiUrl, toursApiUrl, token]);

  const handleFilterChange = async (value) => {
    setFilter(value);
    try {
      let response;
      switch (value) {
        case "ALL":
          response = await axios.get(`${apiUrl}/manage/all`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          break;
        case "POSITIVE":
          response = await axios.get(`${apiUrl}/manage/positive`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          break;
        case "NEGATIVE":
          response = await axios.get(`${apiUrl}/manage/negative`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          break;
        case "CUSTOMER":
          if (selectedCustomerId) {
            response = await axios.get(
              `${apiUrl}/manage/customer/${selectedCustomerId}`,
              {
                headers: { Authorization: `Bearer ${token}` },
              }
            );
          }
          break;
        case "TOUR":
          if (selectedTourId) {
            response = await axios.get(
              `${apiUrl}/manage/tour/${selectedTourId}`,
              {
                headers: { Authorization: `Bearer ${token}` },
              }
            );
          }
          break;
        default:
          const rating = parseInt(value);
          response = {
            data: feedbacks.filter((feedback) => feedback.rating === rating),
          };
          break;
      }
      setFilteredFeedbacks(response ? response.data : []);
    } catch (error) {
      message.error("Lỗi khi lọc phản hồi!");
    }
  };

  const handleCustomerSelect = (value) => {
    setSelectedCustomerId(value);
    handleFilterChange("CUSTOMER");
  };

  const handleTourSelect = (value) => {
    setSelectedTourId(value);
    handleFilterChange("TOUR");
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
    // Edit functionality
    message.info(`Đang chỉnh sửa phản hồi với ID: ${record.feedbackId}`);
  };

  const handleDelete = async (feedbackId) => {
    try {
      await axios.delete(`${apiUrl}/${feedbackId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setFilteredFeedbacks(
        filteredFeedbacks.filter(
          (feedback) => feedback.feedbackId !== feedbackId
        )
      );
      message.success("Đã xóa phản hồi thành công!");
    } catch (error) {
      message.error("Lỗi khi xóa phản hồi!");
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
          <Option value="POSITIVE">Phản Hồi Tích Cực</Option>
          <Option value="NEGATIVE">Phản Hồi Tiêu Cực</Option>
          <Option value="CUSTOMER">Phản Hồi Theo Khách Hàng</Option>
          <Option value="TOUR">Phản Hồi Theo Tour</Option>
          <Option value="1">1 Sao</Option>
          <Option value="2">2 Sao</Option>
          <Option value="3">3 Sao</Option>
          <Option value="4">4 Sao</Option>
          <Option value="5">5 Sao</Option>
        </Select>

        {filter === "CUSTOMER" && (
          <Select
            onChange={handleCustomerSelect}
            placeholder="Chọn Mã Khách Hàng"
            style={{ width: 200, marginBottom: 16 }}
          >
            {customers.map((customer) => (
              <Option key={customer.userId} value={customer.userID}>
                {customer.userID}{" "}
              </Option>
            ))}
          </Select>
        )}

        {filter === "TOUR" && (
          <Select
            onChange={handleTourSelect}
            placeholder="Chọn Mã Tour"
            style={{ width: 200, marginBottom: 16 }}
          >
            {tours.map((tour) => (
              <Option key={tour.tourId} value={tour.tourId}>
                {tour.tourName}
              </Option>
            ))}
          </Select>
        )}

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
