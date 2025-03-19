import { useCookies } from 'react-cookie';
import Header from '../components/Header'
import {useForm} from 'react-hook-form'
import { changePassword } from '../api/teamMemberApi';
import '../css/ChangePassword.css'
import { useState } from 'react';
export default function ChangePassword(){
    const {register, handleSubmit, formState: {errors}} = useForm();
    const [cookies, removeCookie] = useCookies(['userInfo'])
    const [isShown, setIsShown] = useState(false)
    const onSubmit = async(data)=>{
        try {
            if(data.newPassword === data.conNewPassword && data.newPassword !== ''){
                const changePass = await changePassword(cookies.userInfo.accountId, data.oldPassword, data.newPassword);
                if(changePass.ok){
                    await alert("Your password has been changed")
                    removeCookie("userInfo");
                    window.location.href="/login";
                }else{
                    await alert("Failed to change password")
                }
        }else{
            alert("THOSE PASSWORDS DON'T MATCH")
        }
    } catch (error) {
            console.log(error)
        }
        

    }
    const toggleShown = ()=>{
        setIsShown((isShown)=>!isShown)
    }


    return(
        <div>
            <Header/>
            <form onSubmit = {handleSubmit(onSubmit)}>
                <div>
                    Change Password
                </div>
                
                <label htmlFor="old Password">
                    Old Password
                    <div>
                        <input type={isShown ? 'text':'password'} name="" id="oldPassword" {
                            ...register('oldPassword',
                            {
                                required: true,
                                message: "You must enter the old passsword"
                            }
                        )}/>
                    </div>
                </label>
                <label htmlFor="new Password">
                    New Password
                    <div>
                        <input type={isShown ? 'text':'password'} name="" id="newPassword" {
                            ...register('newPassword',
                            {
                                required: true,
                                message: "You must enter the new passsword"
                            }
                    )}/>
                    </div>
                </label>
                <label htmlFor="confirm new Password">
                    Confirm New Password
                    <div>
                        <input type={isShown ? 'text':'password'} name="" id="newPassword" {
                            ...register('conNewPassword',
                            {
                                required: true,
                                message: "You must confirm new passsword"
                            }
                    )}/>
                    </div>
                </label>
                <div>
                    Show passwords
                    <input type="checkbox" checked={isShown} onChange={toggleShown}/>
                </div>
                <button type='submit'>Change Password</button>
                
            </form>
        </div>
    )

}


