import React from 'react'
import '../App.css';


import { Link } from 'react-router-dom';

const Footer = () => {
  return (
    <Link to = '/detail'>
      {/* <div className='footer'>🐣🐥</div> */}
    <img src='rupy2.png' alt="logo" className='footer'/>
    </Link>
  )
}

export default Footer