import { createRoot } from "react-dom/client";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import App from "./App";

createRoot(document.getElementById("root")).render(
  <>
    <App />,
    <ToastContainer />
  </>
);
