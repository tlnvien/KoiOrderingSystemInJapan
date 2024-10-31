// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getStorage } from "firebase/storage";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyBgyT8VoCKOarjnYObVX_UUYWSk7qQkKt0",
  authDomain: "koi-ordering-system-10bc1.firebaseapp.com",
  projectId: "koi-ordering-system-10bc1",
  storageBucket: "koi-ordering-system-10bc1.appspot.com",
  messagingSenderId: "26763694160",
  appId: "1:26763694160:web:40d2131b3be4d59d05c155",
  measurementId: "G-RZJ1V36HTZ"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const storage = getStorage(app);

export { storage };