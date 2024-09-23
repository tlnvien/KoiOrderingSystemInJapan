import React, { useEffect, useState } from "react";
import { Table, Button, Modal, Form, Input } from "antd";
import axios from "axios";
import "./Admin.css";

const Admin = () => {
  const [data, setData] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);

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
    fetchData(); // Refresh data after delete
  };

  const handleOk = async (values) => {
    if (editingRecord) {
      await axios.put(
        `https://66e1d268c831c8811b5672e8.mockapi.io/User/${editingRecord.id}`,
        values
      );
    } else {
      const response = await axios.post(
        "https://66e1d268c831c8811b5672e8.mockapi.io/User",
        values
      );
      const newUser = { id: response.data.id, ...values };
      setData([...data, newUser]);
    }
    setIsModalVisible(false);
    fetchData(); // Refresh data after add/update
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };

  const columns = [
    { title: "ID", dataIndex: "id", key: "id" },
    { title: "Username", dataIndex: "username", key: "username" },
    { title: "Email", dataIndex: "email", key: "email" },
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
    <div>
      <Button type="primary" onClick={handleAdd}>
        Add User
      </Button>
      <Table dataSource={data} columns={columns} rowKey="id" />

      <Modal
        title={editingRecord ? "Edit User" : "Add User"}
        visible={isModalVisible}
        onCancel={handleCancel}
        footer={null}
      >
        <Form
          initialValues={editingRecord}
          onFinish={handleOk}
          layout="vertical"
        >
          <Form.Item
            name="username"
            label="Username"
            rules={[{ required: true, message: "Please input the username!" }]}
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
  );
};

export default Admin;
