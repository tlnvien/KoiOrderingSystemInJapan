import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import "./Profile.css";

const ViewProfile = () => {
  const [user, setUser] = useState(null);
  const navigate = useNavigate();

  // Fetch the registered user data from localStorage or API
  useEffect(() => {
    const registeredUser = JSON.parse(localStorage.getItem("registeredUser")); // assuming user data is stored in localStorage
    if (registeredUser) {
      setUser(registeredUser);
    } else {
      // Redirect to registration if no user data is found
      navigate("/register");
    }
  }, [navigate]);

  const handleEditProfile = () => {
    navigate("/update-profile");
  };

  if (!user) {
    return <p>Loading...</p>; // Loading state while fetching the user data
  }

  return (
    <div className="profile-container">
      <Header />
      <div className="profile-content">
        <h1>Welcome, {user.username}!</h1>
        <div className="profile-info">
          <p>
            <strong>Username:</strong> {user.username}
          </p>
          <p>
            <strong>Email:</strong> {user.email}
          </p>
          <p>
            <strong>Year of Birth:</strong> {user.yearOfBirth}
          </p>
        </div>
        <button className="edit-button" onClick={handleEditProfile}>
          Edit Profile
        </button>
      </div>
      <Footer />
    </div>
  );
};

export default ViewProfile;
