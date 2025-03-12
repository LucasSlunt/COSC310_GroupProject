import Header from "../components/Header";
import fakeAllTeamData from "../FakeData/fakeAllTeamsData.json"
import {useForm} from "react-hook-form"
import '../css/AdminPanel.css'
import {useNavigate} from 'react-router-dom'
export default function AdminPanel(){
    const { register, handleSubmit} = useForm();
    const navigate = useNavigate();
    const onSubmit = data => {
        console.log(data)
        //send to teamTasks with team id as prop.
        //For now because I do not have admin team tasks I will send them to team tasks
        navigate('/team-tasks')
    };
    return(
        <div>
            <Header/>
            <main>
            <a href="#"><button className="button">See All Users</button></a>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <select name="" id="" {...register("team")}>
                        {fakeAllTeamData.map((team)=>(
                            <option value={team.id}>{team.teamName}</option>
                        ))}
                    </select>
                    <input type="submit" value="Go to team task page" className="button"/>

                </form>
            </main>
        </div>
    )
}