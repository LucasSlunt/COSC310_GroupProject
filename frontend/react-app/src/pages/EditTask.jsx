import {Link, useLocation} from 'react-router-dom'
import EditTaskForm from '../components/EditTaskForm'
import Header from '../components/Header'
import '../css/EditTask.css'
function EditTask(){
        const location = useLocation()
        const { taskToEdit } = location.state


        return(
            <div className='pageContainer'>
                <Header/>
                <div className='pageBody'>
                        <EditTaskForm
                        task={taskToEdit}
                        />
                </div>
            </div>
        );
    
}
export default EditTask