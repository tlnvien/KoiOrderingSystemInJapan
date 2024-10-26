import HomePage from "./components/HomePage/HomePage";
import Login from "./components/Auth/Login";
import CustomerRegister from "./components/Auth/Register";
import ManagerRegister from "./components/Auth/ManagerRegister";
import StaffRegister from "./components/Auth/StaffRegister";
import {
  BrowserRouter as Router,
  Route,
  Routes,
  Navigate,
} from "react-router-dom";
import ForgotPassword from "./components/Auth/ForgotPassword";
import VerifyCode from "./components/Auth/VerifyCode";
import ResetPassword from "./components/Auth/ResetPassword";
import { GoogleOAuthProvider } from "@react-oauth/google"; // Thêm GoogleOAuthProvider
import Dashboard from "./components/Dashboard/Dashboard"; // Adjust path as needed
import KoiFishDetail from "./components/KoiFishDetail/KoiFishDetail";
import FarmList from "./components/FarmDetail/FarmList";
import FarmDetail from "./components/FarmDetail/FarmDetail";
import Search from "./components/SearchPage/SearchPage";
import TourDetail from "./components/TourDetail/TourDetail";
import ViewProfile from "./components/ViewAndUpdateProfile/ViewProfile";
import ManaProfile from "./components/ViewAndUpdateProfile/ManagerProfile";
import Admin from "./components/Admin/Admin";
import TourManagement from "./components/Admin/TourManagement";
import InvoiceManagement from "./components/Admin/InvoiceManagement";
import ReviewManagement from "./components/Admin/FeedbackManagement";
import UserManagement from "./components/Admin/UserManagement";
import GoogleProfile from "./components/ViewAndUpdateProfile/GoogleProfile";
import FacebookProfile from "./components/ViewAndUpdateProfile/FacebookProfile";
import Feedback from "./components/Feedback/Feedback";
import KoiManagement from "./components/Admin/KoiManagement";
import FarmManagement from "./components/Admin/FarmManagement";
import BookingPage from "./components/BookingPage/BookingPage";
import AboutUs from "./components/AboutUs/AboutUs";
import React from "react";
import PaymentPage from "./components/Payment/Payment";
import PaymentSuccessPage from "./components/Payment/PaymentSuccess";
import Contact from "./components/Contact/Contact";
import FarmHost from "./components/FarmHost/FarmHost";

function App() {
  const userRole = localStorage.getItem("role");
  return (
    // Bọc toàn bộ ứng dụng trong GoogleOAuthProvider
    <GoogleOAuthProvider clientId="870323659005-s7a2jki564i8e5n09e5lqqn2hof4fe8c.apps.googleusercontent.com">
      <Router>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<Login />} />

          <Route path="/register/customer" element={<CustomerRegister />} />
          <Route path="/register/manager" element={<ManagerRegister />} />
          <Route
            path="/register/staff"
            element={
              userRole === "MANAGER" ? (
                <StaffRegister />
              ) : (
                <Navigate to="/login" />
              )
            }
          />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/verify-code" element={<VerifyCode />} />
          <Route path="/reset-password" element={<ResetPassword />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/koi-fish" element={<KoiFishDetail />} />
          <Route path="/farm" element={<FarmList />} />
          <Route path="/farms/:farmId" element={<FarmDetail />} />
          <Route path="/search" element={<Search />} />
          <Route path="/tour/:id" element={<TourDetail />} />
          <Route path="/admin" element={<Admin />} />
          <Route path="/view-profile" element={<ViewProfile />} />
          <Route path="/admin/mana-profile" element={<ManaProfile />} />
          <Route path="/google-profile" element={<GoogleProfile />} />
          <Route path="/facebook-profile" element={<FacebookProfile />} />
          <Route path="/tours" element={<TourManagement />} />
          <Route path="/admin/invoices" element={<InvoiceManagement />} />
          <Route path="/admin/feedback" element={<ReviewManagement />} />
          <Route path="/admin/users" element={<UserManagement />} />
          <Route path="/feedback-page" element={<Feedback />} />
          <Route path="/admin/koi" element={<KoiManagement />} />
          <Route path="/admin/farm-management" element={<FarmManagement />} />
          <Route path="/booking/:id" element={<BookingPage />} />
          <Route path="/aboutUs" element={<AboutUs />} />
          <Route path="/payment" element={<PaymentPage />} />
          <Route path="/payment-success" element={<PaymentSuccessPage />} />
          <Route path="/contact" element={<Contact />} />
          <Route path="/farm-host" element={<FarmHost />} />
        </Routes>
      </Router>
    </GoogleOAuthProvider>
  );
}

export default App;
