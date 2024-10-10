import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios"; // Import Axios
import "./Profile.css"; // Import CSS cho trang
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import {
  UserOutlined,
  ShoppingCartOutlined,
  StarOutlined,
  LockOutlined,
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
  const [isEditing, setIsEditing] = useState(false); // Thêm state để kiểm tra xem có đang ở chế độ chỉnh sửa không
  const userId = localStorage.getItem("userId");
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const navigate = useNavigate();
  const apiUrl = "http://localhost:8080/api/info";

  useEffect(() => {
    fetchUserProfile();
  }, []);

  // Lấy thông tin người dùng từ API bằng Axios
  const fetchUserProfile = async () => {
    try {
      const response = await axios.get(`${apiUrl}/${userId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.data) {
        setUserData(response.data); // Lưu dữ liệu người dùng vào state
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
        setUserData(response.data); // Cập nhật dữ liệu người dùng
        setIsEditing(false); // Đóng chế độ chỉnh sửa
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
      [name]: value, // Cập nhật dữ liệu khi người dùng nhập
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
          </ul>
        </div>
        {/* Profile Form */}
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
                readOnly={!isEditing} // Cho phép chỉnh sửa nếu isEditing là true
              />
            </div>
            <div className="form-group">
              <label>First Name:</label>
              <input
                type="text"
                name="firstName"
                value={userData.firstName}
                onChange={handleChange}
                readOnly={!isEditing} // Cho phép chỉnh sửa nếu isEditing là true
              />
            </div>
            <div className="form-group">
              <label>Giới tính:</label>
              <select
                name="gender"
                value={userData.gender}
                onChange={handleChange}
                disabled={!isEditing} // Cho phép chỉnh sửa nếu isEditing là true
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
            {/* Chỉ hiển thị nút lưu khi ở chế độ chỉnh sửa */}
            {isEditing && (
              <button type="submit" className="button button-save">
                Lưu thay đổi
              </button>
            )}
          </form>
          {/* Nút chỉnh sửa */}
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
