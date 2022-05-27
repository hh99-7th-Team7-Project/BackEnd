import React from 'react'
import '../App.css';
import { Link } from 'react-router-dom';

const Header = () => {
  return (
    <div className='header'>
    <div>
      
      <h2>
        <Link to="/" style={{textDecoration:"none", color:"black"}}>
        서현어 단어장
        </Link>
      </h2>
      
      </div>
    </div>
  )
}

export default Header