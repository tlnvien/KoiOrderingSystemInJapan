// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getStorage } from "firebase/storage";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyBIyxxevi32kM5BpRrK5GNamjTAGSJLSF0",
  authDomain: "swp391-5fcc7.firebaseapp.com",
  projectId: "swp391-5fcc7",
  storageBucket: "swp391-5fcc7.appspot.com",
  messagingSenderId: "293982899714",
  appId: "1:293982899714:web:c9dbb9296b99103525211b",
  measurementId: "G-FHQ1V9KCMG"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const storage = getStorage(app);
export { storage };