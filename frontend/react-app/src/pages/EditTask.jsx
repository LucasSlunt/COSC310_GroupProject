import {useLocation} from 'react-router-dom'
import EditTaskForm from '../components/EditTaskForm'
import Header from '../components/Header'
import '../css/EditTask.css'
function EditTask(){
        const location = useLocation()
        const { taskToEdit } = location.state
        const {onThisTeam} = location.state
        console.log("taskToEdit: ", taskToEdit)
        console.log("onThisTeam: ", onThisTeam)

        return(
            <div className='page'>
                <Header/>
                <div>
                    <EditTaskForm
                    task={taskToEdit}
                    team={onThisTeam}
                    />
                </div>
            </div>
        );
    
}
export default EditTask