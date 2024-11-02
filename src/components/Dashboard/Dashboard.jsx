import { useState } from "react";
import {
  PieChartOutlined,
  TeamOutlined,
  LogoutOutlined,
} from "@ant-design/icons"; // Import Logout icon
import { Layout, Menu, Button, theme } from "antd";
import { Link, Outlet, useNavigate } from "react-router-dom";
import { FaUserCircle } from "react-icons/fa";

const { Content, Sider, Footer } = Layout;

function getItem(label, key, icon) {
  return { key, icon, label };
}

const Dashboard = () => {
  const [collapsed, setCollapsed] = useState(false);
  const userId = localStorage.getItem("userId");

  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();
  const navigate = useNavigate();

  // Get role from localStorage
  const role = localStorage.getItem("role");

  // Menu items based on role
  const saleItems = [
    getItem(
      "Quản lý tour",
      "/dashboard/sale/manage-tour",
      <PieChartOutlined />
    ),
    getItem(
      "Yêu cầu từ khách hàng",
      "/dashboard/sale/request-customer",
      <TeamOutlined />
    ),
    getItem(
      "Liên kết booking",
      "/dashboard/sale/associate-bookingtour",
      <PieChartOutlined />
    ),
  ];

  const consultingItems = [
    getItem(
      "Danh sách tour",
      "/dashboard/consulting/tour-list",
      <PieChartOutlined />
    ),
    getItem("Check-in", "/dashboard/consulting/checkin", <PieChartOutlined />),
    getItem(
      "Đơn đã tạo",
      "/dashboard/consulting/list-order",
      <PieChartOutlined />
    ),
    getItem(
      "Đơn đã nhận",
      "/dashboard/consulting/received-order",
      <PieChartOutlined />
    ),
  ];

  const deliveryItems = [
    getItem(
      "List Order",
      "/dashboard/delivering/order-list",
      <PieChartOutlined />
    ),
    getItem(
      "Create Delivery Order",
      "/dashboard/delivering/create-delivery",
      <PieChartOutlined />
    ),
    getItem("Work", "/dashboard/delivering/work", <PieChartOutlined />),
    getItem(
      "Đơn đang giao",
      "/dashboard/delivering/starting",
      <PieChartOutlined />
    ),
    getItem("Đơn đã giao", "/dashboard/delivering/done", <PieChartOutlined />),
  ];

  let items;
  if (role === "SALES") {
    items = saleItems;
  } else if (role === "DELIVERING") {
    items = deliveryItems;
  } else if (role === "CONSULTING") {
    items = consultingItems;
  }

  // Handle menu item click
  const handleMenuClick = ({ key }) => {
    navigate(key);
  };

  // Handle logout
  const handleLogout = () => {
    localStorage.removeItem("role");
    localStorage.removeItem("token");
    navigate("/login"); // Redirect to login page
  };

  return (
    <Layout style={{ minHeight: "100vh" }}>
      <Sider collapsible collapsed={collapsed} onCollapse={setCollapsed}>
        <div style={{ padding: "14px", color: "#fff", textAlign: "center" }}>
          {userId && <p>Welcome, {userId}!</p>}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          items={items}
          onClick={handleMenuClick}
        />
        <Link
          to="/staff-profile"
          className="menu-items"
          style={{ padding: "14px", display: "block", color: "#fff" }}
        >
          <FaUserCircle style={{ marginRight: "8px" }} />
          Profile
        </Link>
        <Button
          type="primary"
          icon={<LogoutOutlined />}
          onClick={handleLogout}
          style={{ margin: "16px", width: "90%" }} // Style the button
        >
          Đăng xuất
        </Button>
      </Sider>

      <Layout>
        {/* <Header
          style={{
            padding: 0,
            background: colorBgContainer,
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        ></Header> */}
        <Content style={{ margin: "16px" }}>
          <div
            style={{
              padding: 24,
              minHeight: 360,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
            }}
          >
            <Outlet /> {/* Render child components */}
          </div>
        </Content>
        <Footer style={{ textAlign: "center" }}>
          Koi Booking System ©{new Date().getFullYear()}
        </Footer>
      </Layout>
    </Layout>
  );
};

export default Dashboard;
