import React from 'react';
import './home.css';

const Home = () => {
    return (
        <body>
        
        <div id="navBar">
                <a href="https://www.ubc.ca/"><img src="./icons/cowbell.png" alt="Notifications"/></a>
                <a href="https://www.ubc.ca/"><img src="./icons/messages.png" alt="Messages"/></a>
                <a href="https://www.ubc.ca/"><img src="./icons/user.png" alt="Profile"/></a>
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
                            <tr>
                                <td className="taskName">Create wireframe</td>
                                <td><button className="miniButton">Team 1</button></td>
                                <td>11 04 25</td>
                            </tr>
                            <tr>
                                <td className="taskName">Plan things</td>
                                <td><button className="miniButton">Team 2</button></td>
                                <td>10 04 25</td>
                            </tr>
                        </tbody>
                </table>
            </div>
        </main>

        </body>
    )
};

export default Home;