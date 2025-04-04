import {useEffect, useState} from 'react';
import './home.css';


import Task from './Task';


const testTask = [
    { taskName: "Create wireframe", taskTeam: "Team 1", dueDate: "11/05/25" },
    { taskName: "Plan things", taskTeam: "Team 2", dueDate: "10/04/25" }
]


/**/


const Home = () => {
    return (


        <body>


        <div id="topBar">
            <a href="https://www.ubc.ca/">Notifications </a>
            <a href="https://www.ubc.ca/">Messages </a>
            <a href="https://www.ubc.ca/">Profile</a>
        </div>
       


        <main>
            <div id="teamSection">
                <h2>My Teams</h2>
               
                <div id="teamButtons">
                    <button className="teamButton">Team 1 Name</button>
                    <button className="teamButton">Team 2 Name</button>
                </div>
            </div>


            <div id="taskSection">
                <h2>My Tasks (Preview)</h2>
                <table>
                    <thead>
                        <tr>
                            <th>Task Name</th>
                            <th>Team</th>
                            <th>Deadline</th>
                        </tr>
                    </thead>
                        <tbody>
                            {testTask.map((task, index) => (
                                <Task key={index} {...task}/>
                            ))}
                        </tbody>
                </table>
            </div>
        </main>


        </body>


       
    )
};


export default Home;

