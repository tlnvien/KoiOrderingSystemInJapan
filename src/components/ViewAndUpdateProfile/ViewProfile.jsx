import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
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
    fullName: "",
    email: "",
    phoneNumber: "",
    gender: "",
    dob: "",
    address: "",
  });

  const [isLoading, setIsLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false); // Thêm state để kiểm tra xem có đang ở chế độ chỉnh sửa không
  const userId = localStorage.getItem("userId");
  const navigate = useNavigate();

  useEffect(() => {
    fetchUserProfile();
  }, []);

  // Lấy thông tin người dùng từ API
  const fetchUserProfile = async () => {
    try {
      const response = await fetch(
        `https://66e1d268c831c8811b5672e8.mockapi.io/User/${userId}`,
        {
          method: "GET",
        }
      );

      if (!response.ok) {
        throw new Error("Failed to fetch user profile");
      }

      const result = await response.json();
      setUserData(result); // Lưu dữ liệu người dùng vào state
      setIsLoading(false);
    } catch (error) {
      console.error("Error fetching user data:", error);
      alert("Không thể tải dữ liệu người dùng.");
    }
  };

  const handleEdit = () => {
    setIsEditing(true); // Chuyển sang chế độ chỉnh sửa
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(
        `https://66e1d268c831c8811b5672e8.mockapi.io/User/${userId}`,
        {
          method: "PUT", // Sử dụng PUT để cập nhật thông tin
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(userData), // Chuyển đổi dữ liệu thành JSON
        }
      );

      if (!response.ok) {
        throw new Error("Failed to update user profile");
      }

      const updatedUser = await response.json();
      setUserData(updatedUser); // Cập nhật dữ liệu người dùng
      setIsEditing(false); // Đóng chế độ chỉnh sửa
      alert("Cập nhật thông tin thành công!");
    } catch (error) {
      console.error("Error updating user data:", error);
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
            <li onClick={() => navigate("/reviews")}>
              <StarOutlined style={{ marginRight: "10px" }} /> Đánh giá
            </li>
            <li onClick={() => navigate("/change-password")}>
              <LockOutlined style={{ marginRight: "10px" }} /> Đổi mật khẩu
            </li>
          </ul>
        </div>
        {/* Profile Form */}
        <div className="profile-form">
          <h1>Thông tin cá nhân</h1>
          <form onSubmit={isEditing ? handleSave : null}>
            <div className="form-group">
              <label>Tên:</label>
              <input
                type="text"
                name="fullName"
                value={userData.fullName}
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
                <option value="male">Nam</option>
                <option value="female">Nữ</option>
              </select>
            </div>
            <div className="form-group">
              <label>Ngày sinh:</label>
              <input
                type="date"
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
              <label>Số điện thoại:</label>
              <input
                type="text"
                name="phoneNumber"
                value={userData.phoneNumber}
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
