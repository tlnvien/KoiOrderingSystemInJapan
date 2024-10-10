import React, { useEffect, useState } from "react";
import { Table, Button, Modal, Form, Input, Select } from "antd";
import axios from "axios";
import Sidebar from "./Admin.jsx";

const UserManagement = () => {
  const [data, setData] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);
  const [searchText, setSearchText] = useState("");

  const apiUrl = "http://localhost:8080/api/info"; // URL API
  const token = localStorage.getItem("token");

  // Fetch user data when component mounts
  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const response = await axios.get(apiUrl, {
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

  const handleDelete = (userID) => {
    Modal.confirm({
      title: "Bạn có chắc chắn muốn xóa người dùng này không?",
      okText: "Có",
      okType: "danger",
      cancelText: "Không",
      onOk: async () => {
        try {
          await axios.delete(`${apiUrl}/${userID}`, {
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
      if (!editingRecord && !values.userID) {
        alert("Vui lòng nhập User ID!");
        return;
      }

      const dataToSend = { ...values };

      if (editingRecord) {
        // Cập nhật một bản ghi hiện tại
        await axios.put(`${apiUrl}/${editingRecord.userID}`, dataToSend, {
          headers: { Authorization: `Bearer ${token}` },
        });
        alert("Cập nhật thành công");
      } else {
        // Tạo một bản ghi mới
        await axios.post(apiUrl, dataToSend, {
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
    { title: "User ID", dataIndex: "userID", key: "userID" },
    { title: "Username", dataIndex: "username", key: "username" },
    { title: "Phone", dataIndex: "phone", key: "phone" },
    { title: "Email", dataIndex: "email", key: "email" },
    { title: "Role", dataIndex: "role", key: "role" },
    { title: "First Name", dataIndex: "firstName", key: "firstName" },
    { title: "Last Name", dataIndex: "lastName", key: "lastName" },
    { title: "Gender", dataIndex: "gender", key: "gender" },
    { title: "Address", dataIndex: "address", key: "address" },
    { title: "Note", dataIndex: "note", key: "note" },
    {
      title: "Action",
      key: "action",
      render: (_, record) => (
        <>
          <Button onClick={() => handleEdit(record)}>Edit</Button>
          <Button onClick={() => handleDelete(record.userID)} danger>
            Delete
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
          pagination={{ pageSize: 5 }}
        />

        <Modal
          title="Edit User"
          visible={isModalVisible}
          onCancel={handleCancel}
          footer={null}
          key={editingRecord ? editingRecord.userID : "edit"}
        >
          <Form
            initialValues={editingRecord ? { ...editingRecord } : {}}
            onFinish={handleOk}
            layout="vertical"
          >
            <Form.Item
              name="userID"
              label="User ID"
              rules={[{ required: true, message: "Vui lòng nhập User ID!" }]}
            >
              <Input disabled={!!editingRecord} />
            </Form.Item>
            <Form.Item
              name="username"
              label="Username"
              rules={[
                { required: true, message: "Vui lòng nhập tên người dùng!" },
              ]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="phone"
              label="Phone"
              rules={[
                { required: true, message: "Vui lòng nhập số điện thoại!" },
              ]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="email"
              label="Email"
              rules={[{ required: true, message: "Vui lòng nhập email!" }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="role"
              label="Role"
              rules={[{ required: true, message: "Vui lòng chọn vai trò!" }]}
            >
              <Select>
                <Select.Option value="MANAGER">Manager</Select.Option>
                <Select.Option value="SALES">Sales</Select.Option>
                <Select.Option value="CONSULTING">Consulting</Select.Option>
                <Select.Option value="DELIVERING">Delivering</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item
              name="firstName"
              label="First Name"
              rules={[{ required: true, message: "Vui lòng nhập tên!" }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="lastName"
              label="Last Name"
              rules={[{ required: true, message: "Vui lòng nhập họ!" }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="gender"
              label="Gender"
              rules={[{ required: true, message: "Vui lòng chọn giới tính!" }]}
            >
              <Select>
                <Select.Option value="MALE">Male</Select.Option>
                <Select.Option value="FEMALE">Female</Select.Option>
                <Select.Option value="OTHER">Other</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item name="address" label="Address">
              <Input />
            </Form.Item>
            <Form.Item name="note" label="Note">
              <Input.TextArea />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit">
                {editingRecord ? "Cập nhật" : "Tạo mới"}
              </Button>
            </Form.Item>
          </Form>
        </Modal>
      </div>
    </div>
  );
};

export default UserManagement;
