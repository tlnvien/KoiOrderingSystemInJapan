import React, { useState } from "react";
import Login from "./components/Auth/Login";
import Register from "./components/Auth/Register";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import ForgotPassword from "./components/Auth/ForgotPassword";
import VerifyCode from "./components/Auth/VerifyCode";
import VerifyAccount from "./components/Auth/VerifyAccount";
import ResetPassword from "./components/Auth/ResetPassword";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/verify-code" element={<VerifyCode />} />
        <Route path="/reset-password" element={<ResetPassword />} />
        <Route path="/verify-account" element={<VerifyAccount />} />
      </Routes>
    </Router>
  );
}

export default App;
