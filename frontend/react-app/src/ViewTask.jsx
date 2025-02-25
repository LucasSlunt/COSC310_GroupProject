import "./ViewTask.css"
import Header from "./Header.jsx"
function ViewTask(){
    //Mock Data
    const task = {
        nameOfTask: "Task",
        assignedTo: "James",
        priority: "Low",
        discription: "This is a really cool task with lots of stuff in it",
        pic1:"https://th.bing.com/th/id/OIP.cqp9dHHXg-Vwj5TNQO8-SgHaEK?w=312&h=180&c=7&r=0&o=5&pid=1.7",
        pic2:"https://th.bing.com/th/id/OIP.hEdNtgAioJP9H_d_U1nCWAHaE8?w=263&h=180&c=7&r=0&o=5&pid=1.7",


    }
    return(
        <div class = "viewTask">
            <Header/>
            <div class="page-body">
                <div class="flexbox">
                    <div class="header"style={{ whiteSpace: 'pre-line' }}>
                    <strong class ="nameOfTask">{task.nameOfTask + "\n"}</strong> Assigned to: {task.assignedTo}
                    </div>
                    {/* Place holder for when we get our database we will get the prority DB*/}
                    <div class="priority">
                        Priority: {task.priority}
                    </div>
                </div>
                <div class="discription">
                
                </div>
                <div class="pic">
                    <ul>
                    {/* Place holder for when we get our database imgs will be its own coponent */}
                        <li class="imgSpacing"><img src={task.pic1} alt="" /></li>
                        <li class="imgSpacing"><img src={task.pic2} alt="" /></li>
                    </ul>

                </div>
                <div class="comment-section">
                    <form action="">
                        <input type="text" name="Comment" id="comment" placeholder = "Add Comment/Note"class = "comment"/>
                        <input type="button" value="SUBMIT" class ="submit-comment"/>
                    </form>
                </div>
                <form class="update-class">
                    <div class = "updateStatus">
                        <select name="update status" id="newStatus" class = "updateSelector">
                            <option value="">Update Status</option>
                            <option value="Backlog">Backlog</option>
                            <option value="InProgress">In Progress</option>
                            <option value="UnderReview">Under Review</option>
                            <option value="Done">Done</option>
                        </select>
                        <input type="button" value="EDIT" class="fotterbutton"/>
                    </div>
                    <input type="button" value="DELETE TASK" class="fotterbutton"/>
                </form>
            </div>
        </div>
    )
}
export default ViewTask