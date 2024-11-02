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
import AdminDashboard from "./components/Admin/Dashboard"; // Adjust path as needed
import KoiFishDetail from "./components/KoiFishDetail/KoiFishDetail";
import FarmList from "./components/FarmDetail/FarmList";
import FarmDetail from "./components/FarmDetail/FarmDetail";
import Search from "./components/SearchPage/SearchPage";
import TourDetail from "./components/TourDetail/TourDetail";
import ViewProfile from "./components/ViewAndUpdateProfile/ViewProfile";
import ManaProfile from "./components/ViewAndUpdateProfile/ManagerProfile";
import Admin from "./components/Admin/Admin";
import TourManagement from "./components/SaleStaff/manage-tour/TourManagement";
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
import Dashboard from "./components/Dashboard/Dashboard";
import SaleStaff from "./components/SaleStaff/index";
import RequestCustomer from "./components/SaleStaff/RequestCustomer/RequestCustomer";
import AssociateBookingTour from "./components/SaleStaff/associate-bookingtour/AssociateBookingTour";
import ConsultingStaff from "./components/ConsultingStaff/ConsultingStaff";
import TourList from "./components/ConsultingStaff/TourList/TourList";
import CreateOrder from "./components/ConsultingStaff/create-order/CreateOrder";
import ReceivedOrder from "./components/ConsultingStaff/received-order/ReceivedOrder";
import DeliveryStaff from "./components/DeliveryStaff/index";
import DeliveryOrder from "./components/DeliveryStaff/update-orderStatus/DeliveryOrder";
import CustomerInTour from "./components/ConsultingStaff/customer-in-tour/CustomerInTour";
import ListOrderC from "./components/ConsultingStaff/list-order/ListOrder";
import StaffProfile from "./components/ViewAndUpdateProfile/StaffProfile";
import ListTour from "./components/TourDetail/ListTour";
import BookingPageAvailable from "./components/BookingPage/BookingPageAvailable";
import TourDetailPage from "./components/TourDetail/TourDetail";
import KoiFarm from "./components/Admin/KoiFarm";
import TourRequestManager from "./components/Admin/TourRequestFromSale";
import Checkin from "./components/ConsultingStaff/check-in/Checkin";
import ListOrderD from "./components/DeliveryStaff/order-list/ListOrder";
import CreateDelivery from "./components/DeliveryStaff/create-delivery/CreateDelivery";
import Delivering from "./components/DeliveryStaff/work-day/Delivering";
import DeliveryDone from "./components/DeliveryStaff/done/DeliveryDone";
import DeliveryStarting from "./components/DeliveryStaff/starting/DeliveryStarting";
import ListHistoryTour from "./components/HistoryPage/view-history-tour/ListHistoryTour";
import HistoryDetail from "./components/HistoryPage/history-tour-detail/HistoryDetail";

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
          <Route path="/admin/dashboard" element={<AdminDashboard />} />
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
          <Route path="/admin/feedback" element={<ReviewManagement />} />
          <Route path="/admin/users" element={<UserManagement />} />
          <Route path="/feedback-page" element={<Feedback />} />
          <Route path="/admin/koi" element={<KoiManagement />} />
          <Route path="/admin/farm-management" element={<FarmManagement />} />
          <Route path="/admin/koi-farm" element={<KoiFarm />} />
          <Route path="/list-tour" element={<ListTour />} />
          <Route path="/tour-detail/:tourId" element={<TourDetailPage />} />
          <Route path="/booking" element={<BookingPage />} />
          <Route path="/booking/available" element={<BookingPageAvailable />} />
          <Route path="/aboutUs" element={<AboutUs />} />
          <Route path="/payment" element={<PaymentPage />} />
          <Route path="/payment-success" element={<PaymentSuccessPage />} />
          <Route path="/contact" element={<Contact />} />
          <Route path="/farm-host" element={<FarmHost />} />
          <Route path="/staff-profile" element={<StaffProfile />} />
          <Route path="/history-tour" element={<ListHistoryTour />} />
          <Route
            path="/history-tour-detail/:tourId"
            element={<HistoryDetail />}
          />
          <Route
            path="/admin/tour-request-from-sale"
            element={<TourRequestManager />}
          />
          <Route path="/dashboard" element={<Dashboard />}>
            {/* Routes cho Sale Staff */}
            <Route path="sale" element={<SaleStaff />}>
              <Route path="manage-tour" element={<TourManagement />} />
              <Route path="request-customer" element={<RequestCustomer />} />
              <Route
                path="associate-bookingtour"
                element={<AssociateBookingTour />}
              />
            </Route>

            {/* Routes cho Consulting Staff */}
            <Route path="consulting" element={<ConsultingStaff />}>
              <Route path="tour-list" element={<TourList />} />
              <Route path="create-order" element={<CreateOrder />} />
              <Route path="received-order" element={<ReceivedOrder />} />
              <Route
                path="list-passenger/:tourId"
                element={<CustomerInTour />}
              />
              <Route path="list-order" element={<ListOrderC />} />
              <Route path="checkin" element={<Checkin />} />
            </Route>
            <Route path="delivering" element={<DeliveryStaff />}>
              <Route path="get-order" element={<DeliveryOrder />} />
              <Route path="order-list" element={<ListOrderD />} />
              <Route path="create-delivery" element={<CreateDelivery />} />
              <Route path="work" element={<Delivering />} />
              <Route path="starting" element={<DeliveryStarting />} />
              <Route path="done" element={<DeliveryDone />} />
            </Route>
          </Route>
        </Routes>
      </Router>
    </GoogleOAuthProvider>
  );
}

export default App;
