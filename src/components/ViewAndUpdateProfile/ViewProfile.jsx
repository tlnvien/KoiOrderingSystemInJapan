import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./Profile.css";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import {
  UserOutlined,
  ShoppingCartOutlined,
  StarOutlined,
  LockOutlined,
  TeamOutlined, // Import icon for staff registration
} from "@ant-design/icons";

const ViewProfile = () => {
  const [userData, setUserData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    citizenID: "",
    gender: "",
    dob: "",
    address: "",
  });

  const [isLoading, setIsLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const userId = localStorage.getItem("userId");
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const navigate = useNavigate();
  const apiUrl = "http://localhost:8082/api/info/user";

  useEffect(() => {
    fetchUserProfile();
  }, []);

  const fetchUserProfile = async () => {
    try {
      const response = await axios.get(`${apiUrl}/${userId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.data) {
        setUserData(response.data);
        setIsLoading(false);
      }
    } catch (error) {
      console.error("Error fetching user data:", error.response || error);
      alert("Không thể tải dữ liệu người dùng.");
    }
  };

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.put(`${apiUrl}/${userId}`, userData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.data) {
        setUserData(response.data);
        setIsEditing(false);
        alert("Cập nhật thông tin thành công!");
      }
    } catch (error) {
      console.error("Error updating user data:", error.response || error);
      alert("Cập nhật thông tin không thành công.");
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUserData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <div className="profile-container">
      <Header />
      <div className="profile-content">
        <div className="sidebar-profile">
          <ul>
            <li onClick={() => navigate("/profile")}>
              <UserOutlined style={{ marginRight: "10px" }} /> Tài khoản
            </li>
            <li onClick={() => navigate("/orders")}>
              <ShoppingCartOutlined style={{ marginRight: "10px" }} /> Đơn đặt
              hàng
            </li>
            <li onClick={() => navigate("/feedback")}>
              <StarOutlined style={{ marginRight: "10px" }} /> Đánh giá
            </li>
            <li onClick={() => navigate("/reset-password")}>
              <LockOutlined style={{ marginRight: "10px" }} /> Đổi mật khẩu
            </li>
            {/* Hiển thị tùy chọn đăng ký staff chỉ khi role là manager */}
            {role === "MANAGER" && (
              <li onClick={() => navigate("/register/staff")}>
                <TeamOutlined style={{ marginRight: "10px" }} /> Đăng ký tài
                khoản cho staff
              </li>
            )}
          </ul>
        </div>
        <div className="profile-form">
          <h1>Thông tin cá nhân</h1>
          <form onSubmit={isEditing ? handleSave : null}>
            <div className="form-group">
              <label>Last Name:</label>
              <input
                type="text"
                name="lastName"
                value={userData.lastName}
                onChange={handleChange}
                readOnly={!isEditing}
              />
            </div>
            <div className="form-group">
              <label>First Name:</label>
              <input
                type="text"
                name="firstName"
                value={userData.firstName}
                onChange={handleChange}
                readOnly={!isEditing}
              />
            </div>
            <div className="form-group">
              <label>Giới tính:</label>
              <select
                name="gender"
                value={userData.gender}
                onChange={handleChange}
                disabled={!isEditing}
              >
                <option value="MALE">MALE</option>
                <option value="FEMALE">FEMALE</option>
                <option value="OTHER">OTHER</option>
              </select>
            </div>
            <div className="form-group">
              <label>Ngày sinh:</label>
              <input
                type="date"
                format="yyyy-MM-dd"
                name="dob"
                value={userData.dob}
                onChange={handleChange}
                readOnly={!isEditing}
              />
            </div>
            <div className="form-group">
              <label>Email:</label>
              <input
                type="email"
                name="email"
                value={userData.email}
                onChange={handleChange}
                readOnly={!isEditing}
              />
            </div>
            <div className="form-group">
              <label>Địa chỉ:</label>
              <input
                type="text"
                name="address"
                value={userData.address}
                onChange={handleChange}
                readOnly={!isEditing}
              />
            </div>
            {isEditing && (
              <button type="submit" className="button button-save">
                Lưu thay đổi
              </button>
            )}
          </form>
          {!isEditing && (
            <button onClick={handleEdit} className="button button-edit">
              Chỉnh sửa thông tin
            </button>
          )}
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default ViewProfile;
