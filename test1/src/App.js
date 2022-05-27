import './App.css';
import { Route, Routes } from "react-router-dom" 
import Home from "../src/components/Home"
import Detail from '../src/components/Detail';

function App() {
  return (
    <Routes>
    <Route path="/" element={<Home />} />
    <Route path="/detail/:word" element={<Detail />} />
  </Routes>
  );
}

export default App;
