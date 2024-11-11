import "./Admin.css"; // Tùy chỉnh CSS cho layout
import { Link, Navigate } from "react-router-dom";
import {
  UserOutlined,
  StarOutlined,
  DashboardOutlined,
} from "@ant-design/icons";
import { FaFish, FaLink, FaUserCircle } from "react-icons/fa";
import { GiHouse } from "react-icons/gi";

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
            <Link to="/admin/feedback" className="menu-items">
              <StarOutlined style={{ marginRight: "8px" }} />
              Quản lý đánh giá
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
            <Link to="/admin/koi-farm" className="menu-items">
              <FaLink style={{ marginRight: "8px" }} />
              Liên kết koi với trang trại
            </Link>
          </li>

          <li>
            <Link to="/admin/tour-request-from-sale" className="menu-items">
              <FaLink style={{ marginRight: "8px" }} />
              Nhận yêu cầu từ sale
            </Link>
          </li>

          <li>
            <Link to="/admin/mana-profile" className="menu-items">
              <FaUserCircle style={{ marginRight: "8px" }} />
              Profile
            </Link>
          </li>

          {role === "MANAGER" && (
            <li>
              <Link to="/register/staff" className="menu-items">
                <FaUserCircle style={{ marginRight: "8px" }} />
                Đăng ký tài khoản cho nhân viên
              </Link>
            </li>
          )}

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
