import React from 'react'
import '../App.css';

import { Link } from 'react-router-dom';

const Footer = () => {
  return (
    <Link to = '/detail/:word'>
      <div className='footer'>🐣🐥</div>
    {/* <img src={chick} alt="logo" className='footer'/> */}
    </Link>
  )
}

export default Footer