import { Table, Button, Empty } from "antd";
import React, { useEffect, useState } from "react";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import api from "../../config/axios";
import Sidebar from "./Admin";

function TourRequestManager() {
  const [dataSource, setDataSource] = useState([]);
  const [approvedTours, setApprovedTours] = useState([]);
  const [deniedTours, setDeniedTours] = useState([]);
  const token = localStorage.getItem("token");

  const fetchTourRequests = async () => {
    try {
      if (!token) {
        throw new Error("Token không tồn tại. Vui lòng đăng nhập lại.");
      }
      const response = await api.get("tour/list/requested", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      console.log("Fetched data:", response.data);
      setDataSource(Array.isArray(response.data) ? response.data : []);

      // Update the status of approved and denied tours
      const approved = response.data
        .filter((tour) => tour.status === "CONFIRMED")
        .map((tour) => tour.tourId);
      const denied = response.data
        .filter((tour) => tour.status === "DENIED")
        .map((tour) => tour.tourId);
      setApprovedTours(approved);
      setDeniedTours(denied);
    } catch (error) {
      toast.error(error.response?.data || "Failed to fetch tour requests.");
    }
  };

  const handleApprove = async (tourId) => {
    try {
      await api.post(`tour/approve/${tourId}`, null, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      toast.success("Yêu cầu tour thành công!");

      // Update approvedTours state after approval
      setApprovedTours((prev) => [...prev, tourId]);
      fetchTourRequests(); // Refresh data after approval, though it's now redundant
    } catch (error) {
      toast.error(error.response?.data || "Failed to approve tour request.");
    }
  };

  const handleDeny = async (tourId) => {
    try {
      await api.post(`tour/deny/${tourId}`, null, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      toast.success("Tour request denied successfully!");

      // Update deniedTours state after denial
      setDeniedTours((prev) => [...prev, tourId]);
      fetchTourRequests(); // Refresh data after denial, though it's now redundant
    } catch (error) {
      toast.error(error.response?.data || "Failed to deny tour request.");
    }
  };

  useEffect(() => {
    fetchTourRequests();
  }, []);

  const columns = [
    {
      title: "Tour ID",
      dataIndex: "tourId",
      key: "tourId",
    },
    {
      title: "Tour Name",
      dataIndex: "tourName",
      key: "tourName",
    },
    {
      title: "Tour Type",
      dataIndex: "tourType",
      key: "tourType",
    },
    {
      title: "Requested By",
      dataIndex: "salesId",
      key: "salesId",
    },
    {
      title: "Status",
      dataIndex: "status",
      key: "status",
    },
    {
      title: "Departure Date",
      dataIndex: "departureDate",
      key: "departureDate",
    },
    {
      title: "Duration",
      dataIndex: "duration",
      key: "duration",
    },
    {
      title: "Price (VND)",
      dataIndex: "price",
      key: "price",
    },
    {
      title: "Actions",
      render: (record) => {
        const isApproved = approvedTours.includes(record.tourId);
        const isDenied = deniedTours.includes(record.tourId);
        const isCompleted = record.status === "COMPLETED";

        return (
          <div>
            <Button
              type="primary"
              onClick={() => handleApprove(record.tourId)}
              style={{
                marginRight: 8,
                backgroundColor: isApproved || isCompleted ? "gray" : "",
                borderColor: isApproved || isCompleted ? "gray" : "",
              }}
              disabled={isApproved || isDenied || isCompleted}
            >
              Chấp nhận
            </Button>
            <Button
              type="danger"
              onClick={() => handleDeny(record.tourId)}
              style={{
                backgroundColor: isDenied || isCompleted ? "gray" : "",
                borderColor: isDenied || isCompleted ? "gray" : "",
              }}
              disabled={isApproved || isDenied || isCompleted}
            >
              Từ chối
            </Button>
          </div>
        );
      },
    },
  ];

  return (
    <div className="admin">
      <Sidebar />
      <div className="admin-content">
        <h1>Quản lý tour theo yêu cầu</h1>
        <ToastContainer />
        {dataSource.length > 0 ? (
          <Table columns={columns} dataSource={dataSource} rowKey="tourId" />
        ) : (
          <Empty description="No tour requests available." />
        )}
      </div>
    </div>
  );
}

export default TourRequestManager;
