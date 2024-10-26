import { createRoot } from "react-dom/client";
import App from "./App.jsx";
import "./config/firebase.js";
createRoot(document.getElementById("root")).render(<App />);
