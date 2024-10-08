import React, { useState } from "react";
import { Form, Input, Button, Card, message } from "antd";
import "./PersonalPage.css"; // Import file CSS

const PersonalPage = () => {
  // Initial user information stored in the component state
  const [userInfo, setUserInfo] = useState({
    name: "John Doe",
    email: "johndoe@example.com",
    position: "Sales Staff",
    contact: "+1 123 456 7890",
  });

  // Handle form submission to update user information
  const onFinish = (values) => {
    setUserInfo(values); // Update the user information state
    message.success("Information updated successfully!"); // Show success message
  };

  return (
    <div className="personal-page-container">
      <Card
        title="Personal Information"
        bordered={false}
        className="personal-info-card"
      >
        {/* Ant Design form to allow updates */}
        <Form
          layout="vertical"
          initialValues={userInfo} // Prefill the form with current user information
          onFinish={onFinish} // Handle form submission
        >
          <Form.Item
            label="Name"
            name="name"
            rules={[{ required: true, message: "Please input your name!" }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="Email"
            name="email"
            rules={[{ required: true, message: "Please input your email!" }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="Position"
            name="position"
            rules={[{ required: true, message: "Please input your position!" }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="Contact"
            name="contact"
            rules={[{ required: true, message: "Please input your contact!" }]}
          >
            <Input />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" className="update-button">
              Update Information
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default PersonalPage;
  