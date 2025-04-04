import TaskList from "../components/TaskList";
import Header from "../components/Header";
import "../css/MyTasks.css";
import "../css/Header.css";
import fakeData from "../FakeData/fakeTaskData.json";
import { Link } from 'react-router-dom';
import { getAssignedTasks } from "../api/teamMemberApi";
import { useCookies } from 'react-cookie';
import { useState, useEffect } from 'react';
import LockUnlockTask from "../components/LockUnlockTask";
import Loading from "./Loading";
import { Lock, LockOpen } from 'lucide-react';

function getAssigneesNames(taskItem) {
    return taskItem.assignedMembers.map((member) => member.userName).join(", ");
}
function mapTaskItem(taskItem) {
    return {
      name: taskItem,
      id: taskItem.taskId,
      team: taskItem.teamName,
      assignees: getAssigneesNames(taskItem),
      dueDate: taskItem.dueDate || "No Due Date",
    };
  }

  function setUpData(results) {
    return results
    .filter((taskItem) => taskItem.status !== "Done")
      .map((taskItem) => {
        const baseItem = mapTaskItem(taskItem);
        return{
          ...baseItem,
          status: taskItem.status,
          priority: taskItem.priority,
          isLocked: !!taskItem.isLocked
        };
        
      });
  }
  function setUpDataCompleted(results) {
    return results
      .filter((taskItem) => taskItem.status === "Done")
      .map((taskItem) => {
        const baseItem = mapTaskItem(taskItem);
        return{
          ...baseItem,
          dateCompleted: taskItem.dateCompleted,
        };
      });
  }

function MyTasks(){
    const [cookies] = useCookies(['userInfo'])
    const userId = cookies.userInfo.accountId
    const isAdmin =cookies.userInfo.role ==='admin';

    const [tasksToDo, setTasksToDo ] = useState([]);
    const [loading, setLoading] = useState(true);

    const updateLinkById = (id, isLocked)=>{
        setTasksToDo(tasksToDo.map((task)=>{
            if(task.taskId === id){
                task.isLocked = isLocked;
            }
            return task
        }))
    }

    async function fetchData(){
    try{
        const results = await getAssignedTasks(userId);
        console.log("API Results:", results);
        setTasksToDo(results);
    } catch (error){
        console.log("error while getting tasks: ", error);
    }finally{
        setLoading(false)
    }
}


useEffect(()=>{
    fetchData();
    console.log("Tasks To Do:", tasksToDo);
    
    
},[tasksToDo]);



const commonColumns = [
    {
        Header: "Task Name",
        accessor: "name",
        Cell: (original) => (
            <Link to="/view-task" state={{taskToSee: original.value, teamMembers: original.cell.row.values.assignees}}>{original.value.title}</Link>
          )
    },
    {
        Header: "Team Name",
        accessor:"team",
    },
    {
        Header: "Task ID",
        accessor:"id",
    },
    {
        Header: "Assignee(s)",
        accessor: "assignees",
    },
    {
        Header: "Due Date",
        accessor: "dueDate",
    },
]


const headerAndAccessors = [
    ...commonColumns,
    {
        Header: "Status",
        accessor: "status",
        Cell: (original) => {
            const statusValue = original.value;
            let formattedStatus;
            switch (statusValue) {
                case "InProgress":
                    formattedStatus = "In Progress";
                    break;
                case "notStarted":
                    formattedStatus = "Not Started";
                    break;
                case "done":
                    formattedStatus = "Done";
                    break;
                default:
                    formattedStatus = statusValue;
            }
            return <span>{formattedStatus}</span>;
        } 
    },
    {
        Header: "Priority",
        accessor: "priority",
        Cell: (original) => {
            const prioirtyValue = original.value;
            let color;
            switch (prioirtyValue) {
              case "HIGH":
                color = "red";
                break;
              case "MEDIUM":
                color = "orange";
                break;
              case "LOW":
                color = "green";
                break;
              default:
                color = "black";
            }
       
            return <span style={{ color }}>{prioirtyValue}</span>;
          }
    
    },
    {
        Header: "Is Locked",
        accessor: "isLocked",
        Cell: (original) => {
          const isLocked = original.value;
          return isAdmin ? (
            <LockUnlockTask initialIsLocked={isLocked} taskId={original.row.original.id} updateLinkById={updateLinkById} />
          ) : (
            isLocked ? '🔒' : '🔓'
          );
        },
      }
]
const headerAndAccessorsComplete = [
    ...commonColumns
]
if(loading){
    return (<Loading/>)
}
    const tasksToDoData = setUpData(tasksToDo);
    const tasksCompletedData = setUpDataCompleted(tasksToDo);
    return (
        <div className='pageContainer'>
            <Header/>
            <div className='pageBody'>
                <div class="content-wrapper flexbox">                    
                    <div className="column-box-mytasks">
                        <div className="my-tasks-row">
                            <h1>My Tasks</h1>
                            <a href="/create-task"><button className="create-task-btn">Create Task</button></a>
                        </div>
                        <div className="section-divider"></div>

                        <div className ="taskBox">
                        {tasksToDoData.length > 0 ? (
                            <TaskList
                            dataToUse={tasksToDoData}
                            headersAndAccessors={headerAndAccessors}
                            />
                        ) : (
                            <p>No tasks to do</p>
                        )} 
                        </div>
                    </div>
                        
                    <div className="column-box-mytasks">
                        <h2>My Completed Tasks</h2>
                        <div className="section-divider"></div>
                        <div className ="taskBox">
                            {tasksCompletedData.length > 0 ? (
                                <TaskList
                                dataToUse={tasksCompletedData}
                                headersAndAccessors={headerAndAccessorsComplete}
                                />
                            ) : (
                                <h2>No tasks completed</h2>
                                )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
      

}

export default MyTasks