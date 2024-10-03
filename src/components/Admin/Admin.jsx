import React from "react";
import "./Admin.css"; // Tùy chỉnh CSS cho layout
import { Link } from "react-router-dom";
import {
  UserOutlined,
  FundViewOutlined,
  FileTextOutlined,
  StarOutlined,
  DashboardOutlined,
  TeamOutlined,
} from "@ant-design/icons";

const Admin = () => {
  return (
    <div className="admin">
      <div className="sidebar">
        <ul>
          <li>
            <Link to="/dashboard" className="menu-items">
              <DashboardOutlined style={{ marginRight: "8px" }} />
              Thống kê
            </Link>
          </li>
          <li>
            <Link to="/customers" className="menu-items">
              <TeamOutlined style={{ marginRight: "8px" }} />
              Quản lý Khách hàng
            </Link>
          </li>
          <li>
            <Link to="/tours" className="menu-items">
              <FundViewOutlined style={{ marginRight: "8px" }} />
              Quản lý Tour
            </Link>
          </li>
          <li>
            <Link to="/invoices" className="menu-items">
              <FileTextOutlined style={{ marginRight: "8px" }} />
              Quản lý Hóa đơn
            </Link>
          </li>
          <li>
            <Link to="/reviews" className="menu-items">
              <StarOutlined style={{ marginRight: "8px" }} />
              Quản lý Đánh giá
            </Link>
          </li>
          <li>
            <Link to="/users" className="menu-items">
              <UserOutlined style={{ marginRight: "8px" }} />
              Quản lý người dùng
            </Link>
          </li>
        </ul>
      </div>
      <div className="admin-content"></div>
    </div>
  );
};

export default Admin;
