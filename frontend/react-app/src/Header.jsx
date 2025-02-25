import "./Header.css"
function Header(){
    return(
            <header class ="header">
                <h1 class ="inner-header">
                        <ul>
                            <li class ="logo"><span><a href="#">Whatever we Call this</a></span></li>
                            <li><span><a href="#">Home</a></span></li>
                            <li><span><a href="#">Tasks</a></span></li>
                            <li><span><a href="#">Profile</a></span></li>
                        </ul>
                </h1>
            </header>
    );
}
export default Header