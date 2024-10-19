import React, { useState } from "react";
import {
  PieChartOutlined,
  FileOutlined,
  TeamOutlined,
} from "@ant-design/icons";
import { Layout, Menu, theme } from "antd";
import { Outlet, useNavigate } from "react-router-dom";

const { Header, Content, Sider, Footer } = Layout;

function getItem(label, key, icon) {
  return { key, icon, label };
}

const Dashboard = () => {
  const [collapsed, setCollapsed] = useState(false);
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();
  const navigate = useNavigate();

  // Lấy role từ localStorage hoặc API
  const role = localStorage.getItem("role");

  // Tạo menu dựa trên role
  const saleItems = [
    getItem("Manage Tour", "/dashboard/sale/manage-tour", <PieChartOutlined />),
    getItem("Sent Quote", "/dashboard/sale/sent-quote", <FileOutlined />),
    getItem(
      "Request from customer",
      "/dashboard/sale/request-customer",
      <TeamOutlined />
    ),
  ];

  const consultingItems = [
    getItem(
      "List Tour",
      "/dashboard/consulting/tour-list",
      <PieChartOutlined />
    ),
  ];

  const items = role === "SALES" ? saleItems : consultingItems;

  // Điều hướng khi chọn menu
  const handleMenuClick = ({ key }) => {
    navigate(key);
  };

  return (
    <Layout style={{ minHeight: "100vh" }}>
      <Sider collapsible collapsed={collapsed} onCollapse={setCollapsed}>
        <Menu
          theme="dark"
          mode="inline"
          items={items}
          onClick={handleMenuClick}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: 0, background: colorBgContainer }} />
        <Content style={{ margin: "16px" }}>
          <div
            style={{
              padding: 24,
              minHeight: 360,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
            }}
          >
            <Outlet /> {/* Hiển thị nội dung các component con */}
          </div>
        </Content>
        <Footer style={{ textAlign: "center" }}>
          Ant Design ©{new Date().getFullYear()} Created by Ant UED
        </Footer>
      </Layout>
    </Layout>
  );
};

export default Dashboard;
