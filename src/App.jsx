import HomePage from "./components/HomePage/HomePage";
import Login from "./components/Auth/Login";
import Register from "./components/Auth/Register";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import ForgotPassword from "./components/Auth/ForgotPassword";
import VerifyCode from "./components/Auth/VerifyCode";
import VerifyAccount from "./components/Auth/VerifyAccount";
import ResetPassword from "./components/Auth/ResetPassword";
import { GoogleOAuthProvider } from "@react-oauth/google"; // Thêm GoogleOAuthProvider
import Dashboard from "./components/Dashboard/Dashboard"; // Adjust path as needed
import KoiFishDetail from "./components/KoiFishDetail/KoiFishDetail";
import FarmList from "./components/FarmDetail/FarmList";
import FarmDetail from "./components/FarmDetail/FarmDetail";
import Search from "./components/SearchPage/SearchPage";
import TourDetail from "./components/TourDetail/TourDetail";
import ViewProfile from "./components/ViewAndUpdateProfile/ViewProfile";
import Admin from "./components/Admin/Admin";
import CustomerManagement from "./components/Admin/CustomerManagement";
import TourManagement from "./components/Admin/TourManagement";
import InvoiceManagement from "./components/Admin/InvoiceManagement";
import ReviewManagement from "./components/Admin/ReviewManagement";
import UserManagement from "./components/Admin/UserManagement";
import GoogleProfile from "./components/ViewAndUpdateProfile/GoogleProfile";
import FacebookProfile from "./components/ViewAndUpdateProfile/FacebookProfile";
import Feedback from "./components/Feedback/Feedback";
import BookingPage from "./components/BookingPage/BookingPage";
import React from "react";

function App() {
  return (
    // Bọc toàn bộ ứng dụng trong GoogleOAuthProvider
    <GoogleOAuthProvider clientId="870323659005-s7a2jki564i8e5n09e5lqqn2hof4fe8c.apps.googleusercontent.com">
      <Router>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/verify-code" element={<VerifyCode />} />
          <Route path="/reset-password" element={<ResetPassword />} />
          <Route path="/verify-account" element={<VerifyAccount />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/koi-fish" element={<KoiFishDetail />} />
          <Route path="/farm" element={<FarmList />} />
          <Route path="/farms/:farmId" element={<FarmDetail />} />
          <Route path="/search" element={<Search />} />
          <Route path="/tour/:id" element={<TourDetail />} />
          <Route path="/admin" element={<Admin />} />
          <Route path="/view-profile" element={<ViewProfile />} />
          <Route path="/google-profile" element={<GoogleProfile />} />
          <Route path="/facebook-profile" element={<FacebookProfile />} />
          <Route path="/customers" element={<CustomerManagement />} />
          <Route path="/tours" element={<TourManagement />} />
          <Route path="/invoices" element={<InvoiceManagement />} />
          <Route path="/reviews" element={<ReviewManagement />} />
          <Route path="/users" element={<UserManagement />} />
          <Route path="/feedback" element={<Feedback />} />
          <Route path="/booking/:id" element={<BookingPage />} />
        </Routes>
      </Router>
    </GoogleOAuthProvider>
  );
}

export default App;
