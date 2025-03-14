import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

// Import your pages/components
import Login from './Login';
import Home from './Home';
import ViewTask from './pages/ViewTask';
import Profile from './pages/Profile'
import TeamTasks from './pages/TeamTasks';
import EditTask from './pages/EditTask';
import MyTasks from './pages/MyTasks';
import CreateTask from './pages/CreateTask';


function App() {
  return (
    <Router>
      <div>
        <Routes>
          <Route path="/login" element={<Login/>} />
          <Route path="/home" element={<Home/>}/>
          <Route path="/view-task" element={<ViewTask/>}/>
          <Route path="/profile" element={<Profile/>}/>
          <Route path="/team-tasks" element={<TeamTasks/>}/>
          <Route path="/edit-task" element={<EditTask/>}/>
          <Route path="/my-tasks" element={<MyTasks/>}/>
          <Route path="/create-task" element={<CreateTask/>}/>

          {/*Default path should be login, unless specified */}
          <Route path="/" exact element={<Login/>} />

          <Route path="*" element={<div>Page Not Found</div>} /> {/*Default route if no match*/}
        </Routes>
      </div>
    </Router>
  );
}

export default App;