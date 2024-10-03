import React, { useEffect, useState } from "react";
import { Table, Button, Modal, Form, Input } from "antd";
import axios from "axios";
import { Link } from "react-router-dom"; // Thêm Link cho sidebar
import "./Admin.css"; // Import CSS cho layout
import Sidebar from "./Admin.jsx"; //

const CustomerManagement = () => {
  const [data, setData] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);
  const [searchText, setSearchText] = useState("");

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    const response = await axios.get(
      "https://66e1d268c831c8811b5672e8.mockapi.io/User"
    );
    setData(response.data);
  };

  const handleAdd = () => {
    setEditingRecord(null);
    setIsModalVisible(true);
  };

  const handleEdit = (record) => {
    setEditingRecord(record);
    setIsModalVisible(true);
  };

  const handleDelete = async (id) => {
    await axios.delete(
      `https://66e1d268c831c8811b5672e8.mockapi.io/User/${id}`
    );
    fetchData();
  };

  const handleOk = async (values) => {
    if (editingRecord) {
      await axios.put(
        `https://66e1d268c831c8811b5672e8.mockapi.io/User/${editingRecord.id}`,
        values
      );
    } else {
      await axios.post(
        "https://66e1d268c831c8811b5672e8.mockapi.io/User",
        values
      );
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
    { title: "ID", dataIndex: "id", key: "id" },
    {
      title: "Username",
      dataIndex: "username",
      key: "username",
      sorter: (a, b) => a.username.localeCompare(b.username),
    },
    {
      title: "Email",
      dataIndex: "email",
      key: "email",
      sorter: (a, b) => a.email.localeCompare(b.email),
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
