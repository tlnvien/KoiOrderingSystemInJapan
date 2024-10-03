import React, { useState } from "react";
import { Table, Button, Input, Modal, Form } from "antd";
import Sidebar from "./Admin.jsx";

const ManageUsers = () => {
  const [users, setUsers] = useState([
    {
      key: "1",
      name: "Người dùng 1",
      email: "user1@example.com",
      role: "Admin",
    },
    {
      key: "2",
      name: "Người dùng 2",
      email: "user2@example.com",
      role: "Nhân viên",
    },
  ]);

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [currentUser, setCurrentUser] = useState(null);

  const handleAddEditUser = (values) => {
    if (currentUser) {
      setUsers((prevUsers) =>
        prevUsers.map((user) =>
          user.key === currentUser.key ? { ...user, ...values } : user
        )
      );
    } else {
      setUsers((prevUsers) => [
        ...prevUsers,
        {
          key: (users.length + 1).toString(),
          ...values,
        },
      ]);
    }
    setIsModalVisible(false);
    setCurrentUser(null);
  };

  const handleDeleteUser = (key) => {
    setUsers((prevUsers) => prevUsers.filter((user) => user.key !== key));
  };

  const handleEditUser = (user) => {
    setCurrentUser(user);
    setIsModalVisible(true);
  };

  const handleModalClose = () => {
    setIsModalVisible(false);
    setCurrentUser(null);
  };

  const columns = [
    {
      title: "Tên",
      dataIndex: "name",
      sorter: (a, b) => a.name.localeCompare(b.name),
    },
    {
      title: "Email",
      dataIndex: "email",
      sorter: (a, b) => a.email.localeCompare(b.email),
    },
    {
      title: "Vai Trò",
      dataIndex: "role",
    },
    {
      title: "Hành động",
      render: (text, user) => (
        <div>
          <Button type="primary" onClick={() => handleEditUser(user)}>
            Edit
          </Button>
          <Button
            type="danger"
            onClick={() => handleDeleteUser(user.key)}
            style={{ marginLeft: 8 }}
          >
            Delete
          </Button>
        </div>
      ),
    },
  ];

  return (
    <div className="admin">
      <Sidebar />

      <div className="admin-content">
        <h2>Quản lý Người Dùng</h2>
        <Input.Search
          placeholder="Tìm kiếm người dùng"
          onSearch={(value) => {
            const filteredUsers = users.filter((user) =>
              user.name.toLowerCase().includes(value.toLowerCase())
            );
            setUsers(filteredUsers);
          }}
          style={{ marginBottom: 16, width: 300 }}
          allowClear
        />
        <Button
          type="primary"
          onClick={() => setIsModalVisible(true)}
          style={{ marginBottom: 16 }}
        >
          Thêm Người Dùng
        </Button>
        <Table
          columns={columns}
          dataSource={users}
          pagination={{ pageSize: 5 }}
          rowKey="key"
        />

        <Modal
          title={currentUser ? "Sửa Người Dùng" : "Thêm Người Dùng"}
          visible={isModalVisible}
          onCancel={handleModalClose}
          footer={null}
        >
          <Form
            layout="vertical"
            initialValues={
              currentUser || { name: "", email: "", role: "Nhân viên" }
            }
            onFinish={handleAddEditUser}
          >
            <Form.Item
              label="Tên"
              name="name"
              rules={[
                { required: true, message: "Vui lòng nhập tên người dùng!" },
              ]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              label="Email"
              name="email"
              rules={[{ required: true, message: "Vui lòng nhập email!" }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              label="Vai Trò"
              name="role"
              rules={[{ required: true, message: "Vui lòng chọn vai trò!" }]}
            >
              <Input />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit">
                {currentUser ? "Cập Nhật" : "Thêm"}
              </Button>
            </Form.Item>
          </Form>
        </Modal>
      </div>
    </div>
  );
};

export default ManageUsers;
