import { useEffect, useState} from "react"
import Header from "../components/Header"
import {useLocation} from 'react-router-dom'
import { getTeamMemberById, modifyAdminEmail, modifyAdminName, modifyTeamMemberEmail, modifyTeamMemberName } from "../api/adminApi"
import {useForm} from 'react-hook-form'
import { isAdmin } from "../api/authInfo"

export default function EditUserDetails(){
    const {register, handleSubmit} = useForm();
    const location = useLocation()
    const { accountToEdit } = location.state
    const [accountInfo, setAccountInfo] = useState(null);
    const [loading, setLoading] = useState(true);

    const onSubmit = async(data)=>{
        //const admin = await isAdmin(accountToEdit); when is admin is fixed
        const admin = true;
        console.log(admin)
        try{
            if(data.userName !== accountInfo.userName){
                if(admin){
                    await changeAdminName(data.userName)
                }else{  
                    await changeUserName(data.userName);
                }
            }if(data.password !== undefined){
                console.log(data.password)
            }if(data.userEmail !== accountInfo.userEmail){
                if(admin){
                    await changeAdminEmail(data.userEmail);
                }else{  
                    await changeUserEmail(data.userEmail);
                }
            }
            await alert("User Details set");
            window.location.href="/all-users";
        }catch(error){
            alert("FAILED TO SET USER DETAILS")
        }
        
    }
    async function changeAdminName(newUserName){
            await modifyAdminName(accountToEdit, newUserName);
    }
    async function changeAdminEmail(newUserName){
            await modifyAdminEmail(accountToEdit, newUserName);
    }
    async function changeUserName(newUserName){
            await modifyTeamMemberName(accountToEdit, newUserName);
    }
    async function changeUserEmail(newUserName){
            await modifyTeamMemberEmail(accountToEdit, newUserName);

    }


    useEffect(()=>{
        async function getData(){
            try {
                const data = await getTeamMemberById(accountToEdit);
                setAccountInfo(data)
            } catch (error) {
                console.log(error)
            }finally{
                setLoading(false)
            }
        }
        getData();
    },[accountToEdit])
    if(loading){
        return (
            <div>Loading...</div>
        )
    }

    return(
        <div>
            <Header/>
            {accountToEdit}
            <form onSubmit={handleSubmit(onSubmit)}>
                Change user info for: {accountInfo.userName}
                <label htmlFor="">
                   <div>
                        UserName:
                        <input type="text" defaultValue={accountInfo.userName} id="" {...register("userName")}/>
                   </div>
                </label>
                <label htmlFor="">
                   <div>
                        Email:
                        <input type="text" defaultValue={accountInfo.userEmail} id="" {...register("userEmail")}/>
                   </div>
                </label>
                <label htmlFor="">
                   <div>
                        Password:
                        <input type="password" id="" />
                   </div>
                </label>
                <button type="submit">Change userInfo</button>

            </form>
        </div>
    )
}