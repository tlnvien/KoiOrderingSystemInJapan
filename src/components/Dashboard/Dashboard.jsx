import React from "react";
import { Table, Card, Statistic, Row, Col, Button } from "antd";
import {
  UserOutlined,
  HomeOutlined,
  ShoppingCartOutlined,
  FileTextOutlined,
  DollarOutlined,
  SettingOutlined,
} from "@ant-design/icons";
import "./Dashboard.css";
import Sidebar from "../Admin/Admin";

// Columns for the table
const columns = [
  {
    title: "Order ID",
    dataIndex: "id",
    key: "id",
  },
  {
    title: "Customer",
    dataIndex: "customer",
    key: "customer",
  },
  {
    title: "Date",
    dataIndex: "date",
    key: "date",
  },
  {
    title: "Amount",
    dataIndex: "amount",
    key: "amount",
  },
];

// Sample data for the table
const dataSource = [
  { id: "1", customer: "John Doe", date: "2024-09-01", amount: "$1200" },
  { id: "2", customer: "Jane Smith", date: "2024-09-05", amount: "$1500" },
];

const Dashboard = () => {
  return (
    <div className="dashboard">
      {/* Sidebar */}
      <Sidebar />

      {/* Main content */}
      <div className="main-content">
        <Row gutter={16} style={{ marginBottom: 20 }}>
          <Col span={6}>
            <Card>
              <Statistic title="Total Orders" value={1128} />
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic title="Total Revenue" value="$25,600" />
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic title="New Customers" value={43} />
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic title="Pending Orders" value={8} />
            </Card>
          </Col>
        </Row>

        <Card title="Orders Summary" bordered={false} className="card">
          <Table columns={columns} dataSource={dataSource} pagination={false} />
        </Card>

        <Card title="Recent Activities" bordered={false} className="card">
          <p>Activity 1: Order #12345 processed.</p>
          <p>Activity 2: Payment received from John Doe.</p>
          <p>Activity 3: New customer registration: Jane Smith.</p>
        </Card>

        <div style={{ marginTop: 20 }}>
          <Button type="primary" style={{ marginRight: 10 }}>
            Add New Order
          </Button>
          <Button type="default">Generate Report</Button>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
