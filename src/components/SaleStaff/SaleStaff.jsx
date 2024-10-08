import React, { useState } from "react";
import { Layout, Menu, Spin, Divider } from "antd";
import {
  UserOutlined,
  FileSearchOutlined,
  MessageOutlined,
  FileTextOutlined,
} from "@ant-design/icons";
import PersonalPage from "./PersonalPage";
import CustomerRequestPage from "./CustomerRequestPage";
import TourManagement from "./TourManagement";
import TourList from "./TourList";
import ConsultingStaff from "../ConsultingStaff/ConsultingStaff";
import SendQuote from "./SentQuote";
import HeaderBar from "./HeaderBar"; // Import CustomHeader
import "./SaleStaff.css";

const { Content, Sider } = Layout;

const SalesStaffPage = () => {
  const [currentPage, setCurrentPage] = useState("personalInfo");
  const [loading, setLoading] = useState(false);
  const [activeTabKey, setActiveTabKey] = useState("1");

  const handleMenuClick = (e) => {
    setLoading(true);
    setTimeout(() => {
      setCurrentPage(e.key);
      setLoading(false);
    }, 1000);
  };

  const renderContent = () => {
    switch (currentPage) {
      case "personalInfo":
        return <PersonalPage />;
      case "customerRequest":
        return <CustomerRequestPage />;
      case "sendQuote":
        return <SendQuote />;
      case "tourManagement":
        return <TourManagement />;
      case "1":
        return <TourList />;
      case "2":
        return <ConsultingStaff />;
      default:
        return <TourManagement />;
    }
  };

  return (
    <Layout style={{ minHeight: "100vh", backgroundColor: "#f0f2f5" }}>
      <Sider
        width={200}
        className="site-layout-background"
        style={{ position: "fixed", height: "100vh", overflowY: "auto" }}
      >
        <Menu
          mode="inline"
          defaultSelectedKeys={["customerRequest"]}
          style={{ height: "100%", borderRight: 0 }}
          onClick={handleMenuClick}
          theme="light"
        >
          <Menu.Item key="personalInfo" icon={<UserOutlined />}>
            Personal Information
          </Menu.Item>
          <Menu.Item key="customerRequest" icon={<FileSearchOutlined />}>
            Customer Request
          </Menu.Item>
          <Menu.Item key="sendQuote" icon={<MessageOutlined />}>
            Send Quote to Manager
          </Menu.Item>
          <Menu.Item key="tourManagement" icon={<FileTextOutlined />}>
            Tour Management
          </Menu.Item>
        </Menu>
      </Sider>
      <Layout style={{ padding: "0 24px 24px", marginLeft: 200 }}>
        <HeaderBar />
        <Divider style={{ margin: "16px 0" }} />
        <Content
          style={{
            padding: 24,
            margin: 0,
            minHeight: 280,
            background: "#fff",
            borderRadius: "8px",
            boxShadow: "0 2px 8px rgba(0, 0, 0, 0.1)",
          }}
        >
          {loading ? <Spin tip="Loading..." size="large" /> : renderContent()}
        </Content>
      </Layout>
    </Layout>
  );
};

export default SalesStaffPage;
