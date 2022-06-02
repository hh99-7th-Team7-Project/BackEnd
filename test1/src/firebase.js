// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";

import { getFirestore } from "firebase/firestore";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyAEkC10_N2XfilhWVvu9AraMJqaubY-Psc",
  authDomain: "hanghae-99-a.firebaseapp.com",
  projectId: "hanghae-99-a",
  storageBucket: "hanghae-99-a.appspot.com",
  messagingSenderId: "763631544716",
  appId: "1:763631544716:web:cb3a401fbcb5b041b38ea9",
  measurementId: "G-V58XQS8GEF"
};
initializeApp(firebaseConfig)
// Initialize Firebase
// const app = initializeApp(firebaseConfig);
export const db = getFirestore();