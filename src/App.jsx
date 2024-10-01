import React from "react";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import TourManagement from "./pages/sale-staff/TourManagement";

function App() {
  const router = createBrowserRouter([
    {
      path: "/",
      element: <TourManagement />,
    },
  ]);
  return <RouterProvider router={router} />;
}

export default App;
