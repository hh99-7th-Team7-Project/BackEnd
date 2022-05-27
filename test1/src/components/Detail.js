import React from 'react'
import '../App.css';
import { useParams } from "react-router-dom";
import { Link } from 'react-router-dom';
import { useState } from 'react';
import Header from '../Fixed/Header'
import Footer from '../Fixed/Footer';

const Detail = () => {
  return (
    <div className='App'>
    <Header />
    <div className='section'>
    <h3 style={{textAlign:"center"}}>단어 추가하기</h3>
    <div>단어</div>
    <input></input>
    <div>병음</div>
    <input></input>
    <div>의미</div>
    <input></input>
    <div>예문</div>
    <input></input>
    <div>해석</div>
    <input></input>
    </div>
    <div><Link to = "/" style={{textDecoration:"none"}}><button className='mybtn'>저장하기</button></Link></div>
    </div>
  )
}

export default Detail