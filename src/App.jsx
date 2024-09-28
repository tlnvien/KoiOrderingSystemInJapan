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
import FarmDetail from "./components/FarmDetail/FarmDetail";
import Search from "./components/SearchPage/SearchPage";
import TourDetail from "./components/TourDetail/TourDetail";
import ViewProfile from "./components/ViewAndUpdateProfile/ViewProfile";
import Admin from "./components/Admin/Admin";
import GoogleProfile from "./components/ViewAndUpdateProfile/GoogleProfile";
import FacebookProfile from "./components/ViewAndUpdateProfile/FacebookProfile";

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
          <Route path="/farm" element={<FarmDetail />} />
          <Route path="/search" element={<Search />} />
          <Route path="/tour/:id" element={<TourDetail />} />
          <Route path="/admin" element={<Admin />} />
          <Route path="/view-profile" element={<ViewProfile />} />
          <Route path="/google-profile" element={<GoogleProfile />} />
          <Route path="/facebook-profile" element={<FacebookProfile />} />
        </Routes>
      </Router>
    </GoogleOAuthProvider>
  );
}

export default App;
