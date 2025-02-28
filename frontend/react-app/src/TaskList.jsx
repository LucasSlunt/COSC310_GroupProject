import {useTable, useFilters} from 'react-table'
import React from 'react';
import "./TaskList.css"
import fakeData from "./fakeTaskData.json"
import {FilterTaskTable} from './FilterTaskTable';

function TaskList(){
    const data = React.useMemo(()=> fakeData, []) ;
    const columns = React.useMemo(() => [
        {
            Header: "Task Name",
            accessor: "name",
            Filter: FilterTaskTable
        },
        {
            Header: "Team",
            type : "text",
            Filter: FilterTaskTable
        },
        {
            Header: "Assignee(s)",
            accessor: "assignees",
            Filter: FilterTaskTable
        },
        {
            Header: "Status",
            accessor: "status",
            Filter: FilterTaskTable
        },
        {
            Header: "Priority",
            accessor: "priority",
            Filter: FilterTaskTable
        },
        {
            Header: "Due Date",
            accessor: "dueDate",
            Filter: FilterTaskTable
        }
    ],
    [])
  
    const {getTableProps, getTableBodyProps, headerGroups, rows, prepareRow} = useTable({columns, data}, useFilters)
    return(
        <div className='container'>
            
            <table {...getTableProps()}>
                <thead>
                    {headerGroups.map((headerGroup) => (
                        <tr {...headerGroup.getHeaderGroupProps()}>
                            {headerGroup.headers.map((column) =>(
                                <th {...column.getHeaderProps()}>
                            {column.render("Header")}
                            <div>{column.canFilter ? column.render('Filter'): null}</div>
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


