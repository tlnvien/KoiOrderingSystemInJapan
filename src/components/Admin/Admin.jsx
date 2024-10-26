import React, { useState } from "react";
import "./Admin.css"; // Tùy chỉnh CSS cho layout
import { Link, Navigate } from "react-router-dom";
import {
  UserOutlined,
  FundViewOutlined,
  FileTextOutlined,
  StarOutlined,
  DashboardOutlined,
  TeamOutlined,
} from "@ant-design/icons";
import { FaFish, FaUserCircle } from "react-icons/fa";
import { GiFarmer, GiFarmTractor, GiHouse } from "react-icons/gi";

const Admin = () => {
  const role = localStorage.getItem("role");

  const handleLogout = () => {
    // Remove token and navigate to homepage
    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    Navigate("/login");
  };
  return (
    <div className="admin">
      <div className="sidebar">
        <p style={{ color: "white" }}> Welcome {role}</p>
        <ul>
          <li>
            <Link to="/admin/dashboard" className="menu-items">
              <DashboardOutlined style={{ marginRight: "8px" }} />
              Thống kê
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
            <Link to="/admin/feedback" className="menu-items">
              <StarOutlined style={{ marginRight: "8px" }} />
              Quản lý Đánh giá
            </Link>
          </li>
          <li>
            <Link to="/admin/users" className="menu-items">
              <UserOutlined style={{ marginRight: "8px" }} />
              Quản lý người dùng
            </Link>
          </li>
          <li>
            <Link to="/admin/koi" className="menu-items">
              <FaFish style={{ marginRight: "8px" }} />
              Quản lý cá Koi
            </Link>
          </li>
          <li>
            <Link to="/admin/farm-management" className="menu-items">
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

          <li>
            <Link to="/admin/mana-profile" className="menu-items">
              <FaUserCircle style={{ marginRight: "8px" }} />
              Profile
            </Link>
          </li>

          <li>
            <Link to="/login" onClick={handleLogout} className="menu-items">
              Đăng xuất
            </Link>
          </li>
        </ul>
      </div>
      <div className="admin-content"></div>
    </div>
  );
};

export default Admin;
