import "../css/Header.css"
function Header(){
    return(
            <header class ="header">
                <h1 class ="inner-header">
                        <ul>
                            <li class ="logo"><span><a href="#">Task Master</a></span></li>
                            <li><span><a href="/home">Home</a></span></li>
                            <li><span><a href="/my-tasks">My Tasks</a></span></li>
                            <li><span><a href="/profile">Profile</a></span></li>
                            <li><span><a href="/admin-panel">Admin</a></span></li>
                        </ul>
                </h1>
            </header>
    );
}
export default Header