import React, { useEffect, useState } from "react";
import { Table, Button, Modal, Form, Input, Select } from "antd";
import axios from "axios";
import Sidebar from "./Admin.jsx";
import api from "../../config/axios.js";

const UserManagement = () => {
  const [data, setData] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);
  const [searchText, setSearchText] = useState("");

  // const apiUrl = "http://localhost:8082/api/info";
  const token = localStorage.getItem("token");

  // Fetch user data when component mounts
  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const response = await api.get("/info", {
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "*/*",
        },
      });
      setData(response.data);
    } catch (error) {
      console.error("Error fetching user data:", error);
      alert("Có lỗi xảy ra khi lấy dữ liệu người dùng.");
    }
  };

  const handleEdit = (record) => {
    setEditingRecord(record);
    setIsModalVisible(true);
  };

  const handleDelete = (userId) => {
    Modal.confirm({
      title: "Bạn có chắc chắn muốn xóa người dùng này không?",
      okText: "Có",
      okType: "danger",
      cancelText: "Không",
      onOk: async () => {
        try {
          await api.delete(`/info/${userId}`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          fetchData();
        } catch (error) {
          console.error("Error deleting user:", error);
          alert("Có lỗi xảy ra khi xóa người dùng.");
        }
      },
    });
  };

  const handleOk = async (values) => {
    try {
      // Nếu không có editingRecord, đảm bảo userID không để trống
      if (!editingRecord && !values.userId) {
        alert("Vui lòng nhập User ID!");
        return;
      }

      const dataToSend = { ...values };

      if (editingRecord) {
        // Cập nhật một bản ghi hiện tại
        await api.put(`/info/user/${editingRecord.userId}`, dataToSend, {
          headers: { Authorization: `Bearer ${token}` },
        });
        alert("Cập nhật thành công");
      } else {
        // Tạo một bản ghi mới
        await api.post("/info", dataToSend, {
          headers: { Authorization: `Bearer ${token}` },
        });
      }

      setIsModalVisible(false);
      fetchData(); // Làm mới dữ liệu sau khi thực hiện thao tác
    } catch (error) {
      console.error("Error saving user data:", error);
      alert("Có lỗi xảy ra khi lưu dữ liệu người dùng.");
    }
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };

  const handleSearch = (value) => {
    setSearchText(value);
  };

  const filteredData = data.filter((record) =>
    record.username.toLowerCase().includes(searchText.toLowerCase())
  );

  const columns = [
    { title: "ID", dataIndex: "userID", key: "userID" },
    { title: "Tên người dùng", dataIndex: "username", key: "username" },
    { title: "Số điện thoại", dataIndex: "phone", key: "phone" },
    { title: "Email", dataIndex: "email", key: "email" },
    { title: "Vai trò", dataIndex: "role", key: "role" },
    { title: "Họ và tên", dataIndex: "fullName", key: "fullName" },
    {
      title: "Giới tính",
      dataIndex: "gender",
      key: "gender",
      // render: (text) => {
      //   switch (text) {
      //     case "MALE":
      //       return "Nam";
      //     case "FEMALE":
      //       return "Nữ";
      //     case "OTHER":
      //       return "Khác";
      //     default:
      //       return text;
      //   }
      // },
    },
    { title: "Địa chỉ", dataIndex: "address", key: "address" },
    { title: "Ghi chú", dataIndex: "note", key: "note" },
    {
      title: "Action",
      key: "action",
      render: (_, record) => (
        <>
          {/* <Button onClick={() => handleEdit(record)}>Cập nhật</Button> */}
          <Button onClick={() => handleDelete(record.userID)} danger>
            Xóa
          </Button>
        </>
      ),
    },
  ];

  return (
    <div className="admin">
      <Sidebar />
      <div className="admin-content">
        <h2>Quản lý người dùng</h2>

        <Input.Search
          placeholder="Tìm kiếm người dùng theo tên"
          onSearch={handleSearch}
          style={{ marginBottom: 16, width: 300 }}
          allowClear
        />

        <Table
          dataSource={filteredData}
          columns={columns}
          rowKey="userID"
          pagination={{ pageSize: 7 }}
        />
      </div>
    </div>
  );
};

export default UserManagement;
