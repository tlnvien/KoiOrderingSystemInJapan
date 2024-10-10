import React, { useEffect, useState } from "react";
import { Table, Button, Modal, Form, Input, notification } from "antd";
import axios from "axios";
import { Link } from "react-router-dom"; // Thêm Link cho sidebar
import "./Admin.css"; // Import CSS cho layout
import Sidebar from "./Admin.jsx"; //

const CustomerManagement = () => {
  const [data, setData] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);
  const [searchText, setSearchText] = useState("");

  const apiUrl = "http://localhost:8081/api/info";
  const token =
    "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTcyODMyMTk4OSwiZXhwIjoxNzI4NDA4Mzg5fQ.YG8AFw5VhUM3iHlINXqO3waYcdKHlXQcpHx2ouXoWlA";

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const response = await axios.get(apiUrl, {
        headers: {
          Accept: "*/*",
          Authorization: `Bearer ${token}`,
        },
      });
      setData(response.data);
    } catch (error) {
      notification.error({ message: "Failed to fetch farm list" });
    }
  };

  const handleEdit = (record) => {
    setEditingRecord(record);
    setIsModalVisible(true);
  };

  const handleDelete = async (id) => {
    await axios.delete(`${apiUrl}/${id}`);
    fetchData();
  };

  const handleOk = async (values) => {
    if (editingRecord) {
      await axios.put(`${apiUrl}/${editingRecord.id}`, values);
    } else {
      await axios.post(apiUrl, values);
    }
    setIsModalVisible(false);
    fetchData();
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };

  const handleSearch = (value) => {
    setSearchText(value);
  };

  const filteredData = data.filter((record) => {
    return (
      record.username.toLowerCase().includes(searchText.toLowerCase()) ||
      record.email.toLowerCase().includes(searchText.toLowerCase())
    );
  });

  const columns = [
    { title: "UserID", dataIndex: "userID", key: "id" },
    {
      title: "Username",
      dataIndex: "username",
      key: "username",
      sorter: (a, b) => a.username.localeCompare(b.username),
    },
    {
      title: "Phone",
      dataIndex: "phone",
      key: "phone",
    },
    {
      title: "Email",
      dataIndex: "email",
      key: "email",
      sorter: (a, b) => a.email.localeCompare(b.email),
    },
    {
      title: "Role",
      dataIndex: "role",
      key: "role",
    },
    {
      title: "First Name",
      dataIndex: "firstName",
      key: "firstName",
    },
    {
      title: "Last Name",
      dataIndex: "lastName",
      key: "lastName",
    },
    {
      title: "Gneder",
      dataIndex: "gender",
      key: "gender",
    },
    {
      title: "Action",
      key: "action",
      render: (_, record) => (
        <>
          <Button onClick={() => handleEdit(record)}>Edit</Button>
          <Button onClick={() => handleDelete(record.id)} danger>
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
        <h2>Quản lý khách hàng</h2>

        {/* Thêm ô tìm kiếm */}
        <Input.Search
          placeholder="Tìm kiếm khách hàng theo username hoặc email"
          onSearch={handleSearch}
          style={{ marginBottom: 16, width: 300 }}
          allowClear
        />

        <Table dataSource={filteredData} columns={columns} rowKey="id" />

        <Modal
          title={editingRecord ? "Edit Customer" : "Add Customer"}
          visible={isModalVisible}
          onCancel={handleCancel}
          footer={null}
          key={editingRecord ? editingRecord.id : "add-customer"}
        >
          <Form
            initialValues={editingRecord}
            onFinish={handleOk}
            layout="vertical"
          >
            <Form.Item
              name="username"
              label="Username"
              rules={[
                { required: true, message: "Please input the username!" },
              ]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="email"
              label="Email"
              rules={[{ required: true, message: "Please input the email!" }]}
            >
              <Input />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit">
                Submit
              </Button>
            </Form.Item>
          </Form>
        </Modal>
      </div>
    </div>
  );
};

export default CustomerManagement;
