import React from 'react';
import './login.css';

const Login = () => {
    return (
        <body id="login">
            <div class="container">

                <div id="siteName">
                    <img src="https://www.shutterstock.com/image-vector/sample-logo-business-brand-identity-600nw-1948366864.jpg" alt="logo"/>
                    <h1>Our site name</h1>
                    <p1>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum condimentum nulla pharetra, ultrices mauris ut, pharetra massa. Morbi sodales in metus in porta. Morbi vel semper ligula, at dignissim eros. Aenean sollicitudin, diam et cursus elementum, elit dui viverra leo, et commodo tortor urna et dolor</p1>
                </div>


                <form id="loginForm">
                    <label>Log in</label>
                    
                    <input type="text" name="username" placeholder="Username"/>
                    
                    <input type="password" name="password" placeholder="Password"/>
                    
                    <button type="submit">Login</button>
                
                </form>
            </div>
        </body>
    )
};

export default Login;
