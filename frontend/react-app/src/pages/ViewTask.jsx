import "../css/ViewTask.css"
import Header from "../components/Header.jsx"
import {useLocation, Link} from 'react-router-dom'
import { deleteTask, editTask } from "../api/teamMemberApi.js"


function ViewTask(){
    const location = useLocation()
    const { taskToSee } = location.state
    const {teamMembers} = location.state
    async function changeStatus(event) {
        try {
            const response = await editTask(taskToSee.taskId, taskToSee.title, taskToSee.description, taskToSee.isLocked, event.target.value, taskToSee.dueDate)
            if(response === null){
                throw Error ("Failed to update status")
            }
            alert("Status updated")
        } catch (error) {
            alert(error)
        }
    }




    async function deleteThisTask() {
        try {
            const response = await deleteTask(taskToSee.taskId)
            if(response){
                alert('task Deleted')
                window.location.href="/my-tasks";
            }else{
                throw Error("Failed to delete Task")
            }
        } catch (error) {
            alert(error)
        }
        
    }

    
    return(
        <div className='pageContainer'>
            <Header/>
            <div className='pageBody'>
                <div className="flexbox">
                <strong class ="nameOfTask">{taskToSee.title + "\n"}</strong>
                    <div className="rowFlexbox" style={{justifyContent:'space-between', marginBottom: '15px'}}>
                        <div className="header"style={{ whiteSpace: 'pre-line' }}>
                         Assigned to:{teamMembers}
                        </div>
                        {/* Place holder for when we get our database we will get the prority DB*/}
                        <div className="priority">
                            Priority: {taskToSee.priority}
                        </div>
                    </div>
                
                    <div className="description">
                        {taskToSee.description}
                    </div>
                    {/**
                     * possible image/comments feature not need but could be cool
                     */}
                    {/*<div class="pic">
                        <ul>
                            <li class="imgSpacing"><img src={getTask.pic1} alt="" /></li>
                            <li class="imgSpacing"><img src={getTask.pic2} alt="" /></li>
                        </ul>

                    </div>
                    <div className="comment-section">
                        <form action="">
                            <input type="text" name="Comment" id="comment" placeholder = "Add Comment/Note"class = "comment"/>
                            <input type="button" value="SUBMIT" classNmae ="submit-comment"/>
                        </form>
                    </div>*/}
                    <div className="update-class rowFlexbox" style={{justifyContent:'space-between'}}>
                        <div>
                            <select name="update status" id="newStatus" className = "updateSelector" defaultValue={taskToSee.status} onChange={changeStatus}>
                                <option value="notStarted">Not Started</option>
                                <option value="InProgress">In Progress</option>
                                <option value="Done">Done</option>
                            </select>
                            <Link to = {'/edit-task'} state={{taskToEdit: taskToSee}}>
                                <button class="fotterbutton">EDIT TASK</button>
                                </Link>
                        </div>
                        <input type="button" value="DELETE TASK" className="fotterbutton" onClick={deleteThisTask}/>

                    </div>
                </div>
            </div>
        </div>
    )
}
export default ViewTask
//as = {Link} to = {'/edit-task'} state={{taskToEdit: task, onThisTeam: team}}