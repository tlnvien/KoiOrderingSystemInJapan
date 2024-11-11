import React, { useState, useEffect } from "react";
import { Table, Button, Select, Space, message, Modal } from "antd";
import axios from "axios";
import Sidebar from "./Admin.jsx";
import api from "../../config/axios.js";

const { Option } = Select;

const FeedbackManagement = () => {
  const [feedbacks, setFeedbacks] = useState([]);
  const [filteredFeedbacks, setFilteredFeedbacks] = useState([]);
  const [filter, setFilter] = useState("ALL");
  const [selectedTourId, setSelectedTourId] = useState("");
  const [selectedCustomerId, setSelectedCustomerId] = useState("");
  const [customers, setCustomers] = useState([]);
  const [tours, setTours] = useState([]);
  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchFeedbacks = async () => {
      try {
        const response = await api.get(`feedback/manage/all`, {
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
        const response = await api.get(`info`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setCustomers(response.data);
      } catch (error) {
        message.error("Lỗi khi lấy danh sách khách hàng!");
        console.error("Lỗi khi lấy khách hàng:", error);
      }
    };

    const fetchTours = async () => {
      try {
        const response = await api.get(`tour`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setTours(response.data);
      } catch (error) {
        message.error("Lỗi khi lấy danh sách tour!");
        console.error("Lỗi khi lấy tour:", error);
      }
    };

    fetchFeedbacks();
    fetchCustomers();
    fetchTours();
  }, [token]);

  const handleFilterChange = async (value) => {
    setFilter(value);
    try {
      let response;
      switch (value) {
        case "ALL":
          response = await api.get(`feedback/manage/all`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          break;
        case "POSITIVE":
          response = await api.get(`feedback/manage/positive`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          break;
        case "NEGATIVE":
          response = await api.get(`feedback/manage/negative`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          break;
        // case "CUSTOMER":
        //   if (selectedCustomerId) {
        //     response = await api.get(
        //       `feedback/manage/customer/${selectedCustomerId}`,
        //       {
        //         headers: { Authorization: `Bearer ${token}` },
        //       }
        //     );
        //   }
        //   break;
        // case "TOUR":
        //   if (selectedTourId) {
        //     response = await api.get(`feedback/manage/tour/${selectedTourId}`, {
        //       headers: { Authorization: `Bearer ${token}` },
        //     });
        //   }
        //   break;
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
      title: "Nhận xét",
      dataIndex: "comment",
      key: "comment",
    },
    {
      title: "Hành Động",
      key: "actions",
      render: (_, record) => (
        <Space size="middle">
          <Button type="danger" onClick={() => handleDelete(record.feedbackId)}>
            Xóa
          </Button>
        </Space>
      ),
    },
  ];

  const handleDelete = (feedbackId) => {
    Modal.confirm({
      title: "Bạn có chắc chắn muốn xóa phản hồi này?",
      content: "Phản hồi sẽ không thể phục hồi sau khi xóa.",
      okText: "Có",
      okType: "danger",
      cancelText: "Không",
      onOk: async () => {
        try {
          await api.delete(`feedback/${feedbackId}`, {
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
      },
    });
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
          {/* <Option value="CUSTOMER">Phản Hồi Theo Khách Hàng</Option>
          <Option value="TOUR">Phản Hồi Theo Tour</Option> */}
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
                {customer.userID}
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