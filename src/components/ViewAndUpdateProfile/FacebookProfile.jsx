import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./Profile.css"; // Import CSS chung nếu cần
import Header from "../Header/Header";
import Footer from "../Footer/Footer";

const FacebookProfile = () => {
  const [userData, setUserData] = useState({
    fullName: "",
    email: "",
    phoneNumber: "",
    gender: "",
    dob: "",
    address: "",
  });

  const [isLoading, setIsLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const facebookId = localStorage.getItem("facebookId");
  const userId = localStorage.getItem("userId");
  const navigate = useNavigate();

  useEffect(() => {
    fetchFacebookProfile(); // Gọi hàm để lấy thông tin người dùng
  }, []);

  // Lấy thông tin người dùng từ MockAPI bằng googleId
  const fetchFacebookProfile = async () => {
    try {
      const response = await fetch(
        `https://66f19ed541537919155193cf.mockapi.io/FacebookProfile?facebookId=${facebookId}`,
        {
          method: "GET",
        }
      );

      if (!response.ok) {
        throw new Error("Failed to fetch Facebook profile");
      }

      const result = await response.json();
      if (result.length > 0) {
        setUserData(result[0]);
      } else {
        alert("Không tìm thấy hồ sơ Facebook.");
      }
      setIsLoading(false);
    } catch (error) {
      console.error("Error fetching Google user data:", error);
      alert("Không thể tải dữ liệu người dùng từ Facebook.");
    }
  };

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    console.log("Updating user with ID:", facebookId);
    console.log("User data to update:", userData);
    try {
      const response = await fetch(
        `https://66f19ed541537919155193cf.mockapi.io/FacebookProfile/${userId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(userData),
        }
      );

      if (!response.ok) {
        throw new Error("Failed to update Facebook user profile");
      }

      const updatedUser = await response.json();
      setUserData(updatedUser);
      setIsEditing(false);
      alert("Cập nhật thông tin thành công!");
    } catch (error) {
      console.error("Error updating Google user data:", error);
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
        <div className="profile-form">
          <h1>Thông tin cá nhân (Facebook)</h1>
          <form onSubmit={isEditing ? handleSave : null}>
            <div className="form-group">
              <label>Tên:</label>
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

export default FacebookProfile;
