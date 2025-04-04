import UserTable from '../components/UserTable'
import Header from '../components/Header'
import '../css/AdminAllUsers.css'
import {useState, useEffect} from 'react'
import { getTeams } from '../api/teamApi';
import Loading from './Loading';


function AdminAllUsers(){
    const [teamData, setTeamData] = useState();
    const [loading, setLoading] = useState(true);
    useEffect(()=>{
        async function getTeamData() {
            try {
                const teams = await getTeams();
                setTeamData(teams);
            } catch (error) {
                alert(error)
            }finally{
                setLoading(false);
            }
        }
        getTeamData();
        
    },[])
    if(loading){
        return(<Loading/>)
    }
    return(
        <div className='pageContainer'>
            <Header/>
            <div className='pageBody collumFlexBox'>
                    <UserTable
                teams={teamData}
                />
            </div>

        </div>
    )
}
export default AdminAllUsers
/*
 <UserTable
                teamMember = {AllTeams[0].members}
                />
*/