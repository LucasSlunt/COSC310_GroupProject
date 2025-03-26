import {useTable, useSortBy} from 'react-table'
import React, { use } from 'react';
import "../css/TaskList.css"
import SearchFilterSort from './SearchFilterSort';
import { useState} from 'react';



function TaskList({dataToUse, headersAndAccessors}){
    let  defaultSearch = headersAndAccessors[0].accessor
    const [searchForThis, setSearch] = useState({value: defaultSearch})
    const [searchQuery, setSearchQuery] = useState(""); //creates a state variable "searchQuery" and function "setSearchQuery" to update it
    //whenever the searchQuery changes, runs the useMemo and filters through the fakeData data to only the task names that contain the searchQuery 


    const filteredData = React.useMemo(()=> {return dataToUse.filter((task) =>{
            switch(searchForThis.value){
            case "name":
                return task.name.title.toLowerCase().includes(searchQuery.toLowerCase())
            case "team":
                return task.team.toLowerCase().includes(searchQuery.toLowerCase())
            case "assignees":
                return task.assignees.toLowerCase().includes(searchQuery.toLowerCase())
            case "status":
                return task.status.toLowerCase().includes(searchQuery.toLowerCase())
            case "priority":
                return task.priority.toLowerCase().includes(searchQuery.toLowerCase())
            case "dueDate":
                return task.dueDate.toLowerCase().includes(searchQuery.toLowerCase())
            case "dateCompteted":
                return task.dateCompteted.toLowerCase().includes(searchQuery.toLowerCase())
            case "isLocked":
                return task.isLocked.toLowerCase().includes(searchQuery.toLowerCase())
            case "id":
                return task.id.toLowerCase().includes(searchQuery.toLowerCase())
        }}
    );}, [searchQuery],((searchForThis),[setSearch])) ;
    
    const changeSearch = (event) => {
        setSearch(previousState => {
          return {value: event.target.value}
        });
      }
    const columns = React.useMemo(() => headersAndAccessors,
    [])
    
    const { getTableBodyProps, getTableProps, rows, prepareRow, headerGroups} = useTable({columns,data: filteredData}, useSortBy)
    return(
        
        <div className='container'>
            <div className='Selector-Search'>
                <select name="searchThis" id="searchThis" className='selector' onChange={changeSearch}>
                    {headersAndAccessors.map((headerAndAccessor) =>(
                        <option value={headerAndAccessor.accessor} key = {headerAndAccessor.accessor}>{headerAndAccessor.Header}</option>
                    ))}
                </select>
                
                {/* pass searchQuery and setSearchQuery to SearchFilterSort component */}
                <div className='searchContainer'>
                    <SearchFilterSort
                        searchQuery={searchQuery}
                        setSearchQuery={setSearchQuery}
                        searchForThis={searchForThis}
                    />
                </div>
            </div>

            <table className='taskTable'{...getTableProps()}>
                <thead>
                    {headerGroups.map((headerGroup) => (
                        <tr className='tableHeader'{...headerGroup.getHeaderGroupProps()}>
                            {headerGroup.headers.map((column) =>(
                                <th {...column.getHeaderProps(column.getSortByToggleProps())}>
                            {column.render("Header")}
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