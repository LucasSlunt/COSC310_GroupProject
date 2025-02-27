import "../css/Profile.css"
import logo from '../logo.svg';

//user information used in the profile page
function UserInfo(){

    //mock data
    const user  = {
        name: "Bobby Joe",
        username: "bobbyjoe",
        email: "bobbyjoe@example.com",
        profilePictureUrl: "https://i.pravatar.cc/150?img=12" 
    }
    
    return (
        <div className="user-info">
            <div className="profile-picture">
                    <img src={user.profilePictureUrl} alt="Profile Picture" />
                </div>
                <div className="profile-info">
                    <h1 className="profile-name">{user.name}</h1>
                    <p className="profile-username">{user.username}</p>
                    <h3 className="profile-email">{user.email}</h3>
            </div>
        </div>
    );
}

export default UserInfo