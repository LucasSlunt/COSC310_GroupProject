import { useEffect } from 'react';
import {Controller, useForm} from 'react-hook-form';
import Select from 'react-select'
import { createTeamMember, createAdmin} from '../api/adminApi';
import { addMemberToTeam } from '../api/isMemberOfApi';
export default function CreateAccountForm({teams}){
    const { register, handleSubmit, formState: {errors},control} = useForm();
    const onSubmit = async data => {
        try {
            console.log(data);
        if(data.role === 'admin'){
            const response = await createAdmin(data.username,data.email,data.password)
            console.log(response)
            if(data.teams.length >0){
                await addToTeams(response.accountId, data.teams)
            }
            await alert('Admin Account created the userID is '+ response.accountId)
        }else if(data.role === 'teamMember'){
            const response = await createTeamMember(data.username,data.email,data.password)
            if(data.teams.length >0){
                await addToTeams(response.accountId, data.teams)
            }
            await alert('Team Member Account created the userID is '+response.accountId)
        }else{
            throw new Error('CONTACT ADMIN')
        }
        } catch (error) {
            alert(error)
        }
        window.location.href="/home";
    }
    async function addToTeams(id, teams) {
        const data = await Promise.all(
            teams.map( async (team)=>{
                const didCreate =  await addMemberToTeam(id,team.value)
                console.log(didCreate)
            if(!didCreate){
            throw Error("FAILED TO ADD TEAM MEMBER")
        }
            })
        )
        
    }
    const customStyles = {
        control: (provided) => ({
          ...provided,
          width: '65vw',
        minWidth: '100px',
        maxWidth: '100%',
        minHeight: '30px',
        maxHeight: '100%',
          border: '2px solid grey',
          borderRadius: '10px',
          paddingLeft: '8px',
          backgroundColor: '#BFCDE0',
          margin: '10px 0px',
        }),
      };
    return(
        <form id="createAccountForm" onSubmit={handleSubmit(onSubmit)} className="form">
                    <label>Create Account</label>
                    
                    <input className = 'input' type="text" name="username" placeholder="Username"
                    {...register("username", {
                        required:{
                            value: true,
                            message: "Please enter a username"
                        }
                    })}
                    />

                    <input className = 'input' type="email" name="email" placeholder="Email"
                    {...register("email", {
                        required:{
                            value: true,
                            message: "Please enter an email"
                        }
                    })}
                    />
                        
                    <input type="password"  className = 'input' name="password" placeholder="Password"
                    {...register("password", {
                        required:{
                            value: true,
                            message: "Please enter a password"
                        }
                    })}
                    />
                    <select {...register('role',{required:true})}>
                        <option disabled selected value={''}>Select Role</option>
                        <option value="teamMember">Team Member</option>
                        <option value="admin">Admin</option>
                    </select>
                    <Controller
                    control={control}
                    defaultValue={[]}
                    className='Select'
                    name="teams"
                    rules={{required:true}}
                     render={({field}) => (
                        <Select
                        {...field}
                        options={teams}
                        isMulti
                        styles={customStyles}
                        
                        />)}
                    />
                     <button type="submit" id ='createAccountSumbitButton'>Create</button>

                </form>
            )
}