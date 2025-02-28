import {useTable, useFilters, useSortBy} from 'react-table'
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
            accessor: "team",
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

    const { getTableBodyProps, getTableProps, rows, prepareRow, headerGroups} = useTable({columns, data,}, useFilters, useSortBy)
    return(
        <div className='container'>
            
            <table {...getTableProps()}>
                <thead>
                    {headerGroups.map((headerGroup) => (
                        <tr {...headerGroup.getHeaderGroupProps()}>
                            {headerGroup.headers.map((column) =>(
                                <th {...column.getHeaderProps(column.getSortByToggleProps())}>
                            {column.render("Header")}
                            <div>{column.canFilter ? column.render('Filter'): null}</div>
                            <span>
                                {column.isSorted ? (column.isSortedDesc ? '⬇️':'⬆️'): '↕️'}
                            </span>
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


