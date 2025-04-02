import {useForm} from 'react-hook-form'
import {login, isAdmin} from '../api/authInfoApi'
import { useCookies } from 'react-cookie';
function LoginForm(){
    const { register, handleSubmit, formState: {errors}} = useForm();
    const [cookie, setCookie] = useCookies(['userInfo',{doNotParse: true}]);
    const onSubmit = async(data)=> {
        try {
            const responseLogin = await login(data.username, data.password);
            if(responseLogin === null){
                alert("Failed Login")
            }else{
                const dataToCokie= {
                    accountId:responseLogin.accountId,
                    userName:responseLogin.userName,
                    role:(responseLogin.role === 'ADMIN' ? "admin":"teamMember"),
                    password: responseLogin.password}
                await setCookie("userInfo", dataToCokie,{secure: false, maxAge: 1800});
                window.location.href="/home";
                
            }
        } catch (error) {
            console.log(error)
        } 
    };
    return(
        <div id="loginFormWrapper">
        <form onSubmit = {handleSubmit(onSubmit)} id="loginForm" >
                    <label>Log in</label>
                    
                    <input type="text" name="username" placeholder="Username" {...register("username", { 
                        required:{
                            value: true,
                            message: 'Please enter a Username Name'
                        }
                        })}/>
                    <input type="text" name="password" placeholder="Password" {...register("password", { 
                        required:{
                            value: true,
                            message: 'Please set a Password'
                        }
                        })}/>

                    
                    <button type="submit" >Login</button>
                
                </form>
            </div>
    )

}
export default LoginForm