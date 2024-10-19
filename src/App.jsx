import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/login/login";
import Dashboard from "./components/dashboard";
import SaleStaff from "./pages/SaleStaff";
import TourManagement from "./pages/SaleStaff/manage-tour/TourManagement";
import RequestCustomer from "./pages/SaleStaff/RequestCustomer/RequestCustomer";
import SentQuote from "./pages/SaleStaff/sent-quote/SentQuote";
import ConsultingStaff from "./pages/ConsultingStaff/ConsultingStaff";
import TourList from "./pages/ConsultingStaff/TourList/TourList";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />

        {/* Dashboard chung */}
        <Route path="/dashboard" element={<Dashboard />}>
          {/* Routes cho Sale Staff */}
          <Route path="sale" element={<SaleStaff />}>
            <Route path="manage-tour" element={<TourManagement />} />
            <Route path="request-customer" element={<RequestCustomer />} />
            <Route path="sent-quote" element={<SentQuote />} />
          </Route>

          {/* Routes cho Consulting Staff */}
          <Route path="consulting" element={<ConsultingStaff />}>
          <Route path="tour-list" element={<TourList/>} />
          </Route>
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
