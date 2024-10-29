import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/login/login";
import Dashboard from "./components/dashboard";
import SaleStaff from "./pages/SaleStaff";
import TourManagement from "./pages/SaleStaff/manage-tour/TourManagement";
import RequestCustomer from "./pages/SaleStaff/RequestCustomer/RequestCustomer";
import ConsultingStaff from "./pages/ConsultingStaff/ConsultingStaff";
import TourList from "./pages/ConsultingStaff/TourList/TourList";
import AssociateBookingTour from "./pages/SaleStaff/associate-bookingtour/AssociateBookingTour";
import CreateOrder from "./pages/ConsultingStaff/create-order/CreateOrder";
import ReceivedOrder from "./pages/ConsultingStaff/received-order/ReceivedOrder";
import DeliveryStaff from "./pages/DeliveryStaff";
import DeliveryOrder from "./pages/DeliveryStaff/update-orderStatus/DeliveryOrder";
import CustomerInTour from "./pages/ConsultingStaff/customer-in-tour/CustomerInTour";
import ListOrderC from "./pages/ConsultingStaff/list-order/ListOrder";
import ListOrderD from "./pages/DeliveryStaff/order-list/ListOrder";
import CreateDelivery from "./pages/DeliveryStaff/create-delivery/CreateDelivery";
import Delivering from "./pages/DeliveryStaff/work-day/Delivering";
import DeliveryDone from "./pages/DeliveryStaff/done/DeliveryDone";
import DeliveryStarting from "./pages/DeliveryStaff/starting/DeliveryStarting";
import Checkin from "./pages/ConsultingStaff/check-in/Checkin";

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
            <Route path="list-passenger/:tourId" element={<CustomerInTour />} />
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
  );
}

export default App;
