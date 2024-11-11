import React, { useState, useEffect } from "react";
import { DatePicker } from "antd";
import api from "../../config/axios.js";
import Layout from "../Dashboard/Dashboard.jsx";
import dayjs from "dayjs";

const ManaProfile = () => {
  const [userData, setUserData] = useState({
    fullName: "",
    email: "",
    gender: "",
    dob: null,
    address: "",
  });

  const [isLoading, setIsLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const userId = localStorage.getItem("userId");
  const token = localStorage.getItem("token");

  useEffect(() => {
    fetchUserProfile();
  }, []);

  const fetchUserProfile = async () => {
    try {
      const response = await api.get(`info/user/${userId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.data) {
        setUserData({
          ...response.data,
          dob: response.data.dob
            ? dayjs(response.data.dob, "DD-MM-YYYY")
            : null,
        });
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
      const response = await api.put("info", userData, {
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
      <div className="profile-content">
        <Layout />
        <div className="profile-form">
          <h1>Thông tin cá nhân</h1>
          <form onSubmit={isEditing ? handleSave : null}>
            <div className="form-group">
              <label>Họ và tên:</label>
              <input
                type="text"
                name="fullName"
                value={userData.fullName}
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
                <option value="MALE">Nam</option>
                <option value="FEMALE">Nữ</option>
                <option value="OTHER">Khác</option>
              </select>
            </div>
            <div className="form-group">
              <label>Ngày sinh:</label>
              <DatePicker
                value={userData.dob}
                format="DD-MM-YYYY"
                onChange={(date) => {
                  setUserData((prevData) => ({
                    ...prevData,
                    dob: date,
                  }));
                }}
                disabled={!isEditing}
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
    </div>
  );
};

export default ManaProfile;
