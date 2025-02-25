import "../css/Profile.css"
import UserInfo from "../components/UserInfo"
import Teams from "../components/Teams"

function Profile(){
    //mock data
    const teams = [
        {id: 1, name: "The Alphas"},
        {id: 2, name: "The Nerds"},
        {id: 3, name: "C++ +"},
    ];

    return (
    <div className="profile-page">
        <UserInfo />
        <h1>My teams</h1>
        <div className="teams-grid">
            {teams.map((team) => (
                <Teams team={team} key={team.id}/> 
            ))}
        </div> 
        <button className="logout-btn">
            Logout
        </button>
    </div>
    
    );
}

export default Profile