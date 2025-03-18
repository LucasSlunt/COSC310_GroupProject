import { useCookies } from 'react-cookie';
import Header from '../components/Header'
import {useForm} from 'react-hook-form'
import { changePassword } from '../api/teamMemberApi';
import '../css/ChangePassword.css'
export default function ChangePassword(){
    const {register, handleSubmit, formState: {errors}} = useForm();
    const [cookies, removeCookie] = useCookies(['userInfo'])
    const onSubmit = async(data)=>{
        try {
            const changePass = await changePassword(cookies.userInfo.accountId, data.oldPassword, data.newPassword);
            if(changePass.ok){
                await alert("Your password has been changed")
                removeCookie("userInfo");
                window.location.href="/login";
            }else{
                await alert("Failed to change password")
            }
        } catch (error) {
            console.log(error)
        }
        

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
                        <input type="password" name="" id="oldPassword" {
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
                        <input type="password" name="" id="newPassword" {
                            ...register('newPassword',
                            {
                                required: true,
                                message: "You must enter the new passsword"
                            }
                    )}/>
                    </div>
                </label>
                <button type='submit'>Change Password</button>
                
            </form>
        </div>
    )

}


