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
        const admin = await isAdmin(accountToEdit);
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
                await changeUserEmail(data.userName);
            }
        }
    }
    async function changeAdminName(newUserName){
        try {
            await modifyAdminName(accountToEdit, newUserName);
        } catch (error) {
            console.log(error)
        }
    }
    async function changeAdminEmail(newUserName){
        try {
            await modifyAdminEmail(accountToEdit, newUserName);
        } catch (error) {
            console.log(error)
        }
    }
    async function changeUserName(newUserName){
        try {
            await modifyTeamMemberName(accountToEdit, newUserName);
        } catch (error) {
            console.log(error)
        }
    }
    async function changeUserEmail(newUserName){
        try {
            await modifyTeamMemberEmail(accountToEdit, newUserName);
        } catch (error) {
            console.log(error)
        }
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
                        <input type="email" defaultValue={accountInfo.userEmail} id="" />
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