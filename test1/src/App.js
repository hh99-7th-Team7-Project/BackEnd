import './App.css';
import { Route, Routes } from "react-router-dom" 
import Home from "../src/components/Home"
import Detail from '../src/components/Detail';
import { useSelector } from 'react-redux';
import Header from './Fixed/Header'

function App() {
  // const is_loaded = useSelector(state => state.word.is_loaded);
  return (
    <Routes>
    <Route path="/" element={<Home />} />
    <Route path="/detail" element={<Detail />} />
    </Routes>
  );
}

export default App;
