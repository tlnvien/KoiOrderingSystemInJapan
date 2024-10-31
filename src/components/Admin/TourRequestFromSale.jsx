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
      setDataSource(Array.isArray(response.data) ? response.data : []);
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
      setApprovedTours((prev) => [...prev, tourId]); // Add tourId to approved list
      fetchTourRequests(); // Refresh data after approval
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
      setDeniedTours((prev) => [...prev, tourId]); // Add tourId to denied list
      fetchTourRequests(); // Refresh data after denial
    } catch (error) {
      toast.error(error.response?.data || "Failed to deny tour request.");
    }
  };

  useEffect(() => {
    fetchTourRequests(); // Fetch data on component mount
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
      render: (text) => new Date(text).toLocaleString(),
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

        return (
          <div>
            <Button
              type="primary"
              onClick={() => handleApprove(record.tourId)}
              style={{
                marginRight: 8,
                backgroundColor: isApproved ? "gray" : "",
                borderColor: isApproved ? "gray" : "",
                pointerEvents: isApproved ? "none" : "auto",
              }}
              disabled={isApproved || isDenied}
            >
              Approve
            </Button>
            <Button
              type="danger"
              onClick={() => handleDeny(record.tourId)}
              style={{
                backgroundColor: isDenied ? "gray" : "",
                borderColor: isDenied ? "gray" : "",
                pointerEvents: isDenied ? "none" : "auto",
              }}
              disabled={isApproved || isDenied}
            >
              Deny
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
        <h1>Tour Request Manager</h1>
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
