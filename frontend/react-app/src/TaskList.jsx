import {useTable} from 'react-table'
import React from 'react';
import "./TaskList.css"
import fakeData from "./fakeTaskData.json"
const mockData = {
    name: "Task",
    team: "Team 1",
    assignees: "James Liam Evan Kelly",
    status: "in progress",
    priority: "Low",
    dueDate: "2/15/2025"
}

function TaskList(){
    const data = React.useMemo(()=> fakeData, []) ;
    const columns = React.useMemo(() => [
        {
            Header: "Task Name",
            accessor: "name",
        },
        {
            Header: "Team",
            accessor:"team",
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
            Header: "Priority",
            accessor: "priority",
        },
        {
            Header: "Due Date",
            accessor: "dueDate",
        }
    ],
    [])

    const {getTableProps, getTableBodyProps, headerGroups, rows, prepareRow} = useTable({columns, data})
    return(
        <div className='container'>
            <table {...getTableProps()}>
                <thead>
                    {headerGroups.map((headerGroup) => (
                        <tr {...headerGroup.getHeaderGroupProps()}>
                            {headerGroup.headers.map((column) =>(
                                <th {...column.getHeaderProps()}>
                            {column.render("Header")}
                            </th>
                            ))}
                        </tr>
                    ))}
                </thead>
                <tbody{...getTableBodyProps()}>
                        {rows.map((row)=>{
                            prepareRow(row)
                            return( 
                                <tr {...row.getRowProps()}>
                                    {row.cells.map((cell)=>(
                                        <td {...cell.getCellProps()}>{cell.render("Cell")}</td>
                                    ))}
                                </tr>
                            )
                        })}
                </tbody>
            </table>
        </div>

    );
}

export default TaskList


