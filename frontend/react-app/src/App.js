
import './App.css';
import Profile from './pages/Profile';

import React from 'react';

//routing for pages?
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

//Login page
import Login from'./Login';
import Home from './Home';

const App = () => {
  return (
    <Router>
      <nav>
        <a href="/login">Login</a> 
        <a href="/home">Home</a>
      </nav>

      <Routes>
        <Route path="/Login" element={<Login/>}></Route>
        <Route path="/Home" element={<Home/>}></Route>
      </Routes>
    </Router>
  );
}

export default App;
