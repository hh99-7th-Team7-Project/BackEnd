import './App.css';
import { Route, Routes } from "react-router-dom"
import Home from "../src/components/Home"
import Detail from '../src/components/Detail';
import { db } from "./firebase"
import React, { useEffect } from 'react';
import { collection, getDoc, getDocs, addDoc, updateDoc, doc, deleteDoc } from "firebase/firestore";


function App() {

  React.useEffect(async () => {

    addDoc(collection(db, "myword"), { text: "new", completed: false });
  }
    , [])
  // const is_loaded = useSelector(state => state.word.is_loaded);
  return (
    
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/detail" element={<Detail />} />
    </Routes>
  );
}

export default App;
