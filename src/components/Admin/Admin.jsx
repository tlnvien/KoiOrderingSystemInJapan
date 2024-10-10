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
import { FaFish } from "react-icons/fa";
import { GiFarmer, GiFarmTractor, GiHouse } from "react-icons/gi";

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
          <li>
            <Link to="/koies" className="menu-items">
              <FaFish style={{ marginRight: "8px" }} />
              Quản lý cá Koi
            </Link>
          </li>
          <li>
            <Link to="/farm-management" className="menu-items">
              <GiHouse style={{ marginRight: "8px" }} />
              Quản lý trang trại
            </Link>
          </li>
          <li>
            <Link to="/tourSchedule-management" className="menu-items">
              <GiHouse style={{ marginRight: "8px" }} />
              Quản lý lịch trình tour
            </Link>
          </li>
        </ul>
      </div>
      <div className="admin-content"></div>
    </div>
  );
};

export default Admin;
