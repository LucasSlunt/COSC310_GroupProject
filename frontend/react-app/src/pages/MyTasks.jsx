import TaskList from "../components/TaskList";
import Header from "../components/Header";
import "../css/MyTasks.css"
import fakeData from "../FakeData/fakeTaskData.json"
import { Link } from 'react-router-dom';
import { getAssignedTasks } from "../api/teamMemberApi";
import { useCookies } from 'react-cookie';
import { useState, useEffect } from 'react';





// function setUpDataComplete(obj){
//  let ansArr = []
//  fakeData.map((taskItem) =>{
//    if(taskItem.status === "done"){
//     ansArr = [...ansArr,
//         {
//             id: taskItem.taskId,
//             name: taskItem.title,
//             team: taskItem.id,
//             assignees: "fix late",
//             dueDate: taskItem.dueDate,
//             dateCompteted: taskItem.dateCompteted


//         }]
//    }
//  }
// )
// if(ansArr.length > 0){
//     return ansArr
// }else{
//     return [" "]
// }
// }
// function getAssignnesNames(task){
//     let returnArr = []
//     task.assignees.map((assigne)=>{
//         returnArr = [...returnArr, assigne.name]
//     })
//     return returnArr
// }

function getAssigneesNames(taskItem) {
    return taskItem.assignedMembers.map((member) => member.userName).join(", ");
}

function setUpData(results) {
    return results
      .map((taskItem) => ({
        id: taskItem.taskId,
        name: taskItem,
        team: taskItem.teamId,
        assignees: getAssigneesNames(taskItem),
        status: taskItem.status,
        dueDate: taskItem.dueDate || "No Due Date", 
      }));
}


// function setUpDataTasksToDo(obj){
//     let ansArr = []
//     fakeData.map((taskItem) =>{
//    if(taskItem.status !== "done"){
//     console.log(taskItem.assignees)
//     ansArr = [ ...ansArr,
//         {
//             id: taskItem.id,
//             name: taskItem.name,
//             team: taskItem.team,
//             assignees: getAssignnesNames(taskItem).join(' '),
//             status: taskItem.status,
//             priority: taskItem.priority,
//             dueDate: taskItem.dueDate
            


//         }]
    
//    }
//  }
// )
// if(ansArr.length > 0){
//     return ansArr
// }else{
//     return [" "]
// }
// }
function MyTasks(){
const [cookies] = useCookies(['userInfo'])
const userId = cookies.userInfo.accountId

const [tasksToDo, setTasksToDo ] = useState([]);
//const [tasksComplete, setTasksComplete] = useState([]);
const [loading, setLoading] = useState(true);

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
    
    
},[]);

useEffect(() => {
    
    console.log("Tasks To Do (after state update):", tasksToDo);
  }, [tasksToDo]);


  

    const headerAndAccessors = [
        {
            Header: "Task Name",
            accessor: "name",
            Cell: (original) => (
                <Link to="/view-task" state={{taskToSee: original.value, teamMembers: original.cell.row.values.assignees}}>{original.value.title}</Link>
              )
        },
        {
            Header: "TeamId",
            accessor:"team",
        },
        {
            Header: "ID",
            accessor:"id",
        },
        {
            Header: "Assignee(s)",
            accessor: "assignees",
        },
        {
            Header: "Status",
            accessor: "status",
        },
        {
            Header: "Due Date",
            accessor: "dueDate",
        }
    ]
    // const headerAndAccessorsComplete = [
    //     {
    //         Header: "Task Name",
    //         accessor: "name",
    //         Cell: (original) => (
    //             <Link to="/view-task" state={{taskToSee: original.cell.row.values.id}}>{original.value}</Link>
    //           )
    //     },
    //     {
    //         Header: "Team",
    //         accessor:"team",
    //     },
    //     {
    //         Header: "ID",
    //         accessor:"id",
    //     },
    //     {
    //         Header: "Assignee(s)",
    //         accessor: "assignees",
    //     },
    //     {
    //         Header: "Due Date",
    //         accessor: "dueDate",
    //     },
    //     {
    //         Header: "Date Completed",
    //         accessor: "dateCompteted",
    //     }
    // ]
    if(loading){
        return (<div>Loading...</div>)
    }
    return (

        <div className='pageContainer'>
            <Header/>
            <div className='pageBody'>
            <div class="content-wrapper flexbox">
                    
                    <h1>My Tasks</h1>
                    <span class ="taskBox">
                        <TaskList
                        dataToUse={setUpData(tasksToDo)}
                        headersAndAccessors={headerAndAccessors}
                        />
                        

                    </span>
                    <a href="/create-task"><button className="create-task-btn">Create Task</button></a>
                    
                    <h2>My Completed Tasks</h2>
                    
                    
            </div>
            </div>
        </div>
      );
      

}

export default MyTasks