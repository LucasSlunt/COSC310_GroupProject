import {useTable, useSortBy} from 'react-table'
import React from 'react';
import "../css/TaskList.css"
import SearchFilterSort from './SearchFilterSort';
import { useState} from 'react';
import {Link} from 'react-router-dom'
const AllTeams = [
    {
        name: "Team1",
        id: "12746",
        members: [
            {
                name:"Liam",
                memberID: "12746",
                email: "liamDig@jmail.com",
                role: "admin"
            }, 
            {
                name:"Greg",
                memberID: "1",
                email: "GregDig@jmail.com",
                role: "teamMember"
        
            },
            {
                name: "Mel",
                memberID: "4",
                email: "MelDig@jmail.com",
                role: "admin"
    
            }]
    },
    {
        name: "Team2",
        id: "2",
        members: [
        {
            name:"Liam",
            memberID: "0",
            email: "liamDig@jmail.com",
            role: "admin"
        }, 
        {
            name:"Greg",
            memberID: "1",
            email: "GregDig@jmail.com",
            role: "teamMember"
    
        }, 
        {
            name:"Gorgia",
            memberID: "2",
            email: "GorgiaDig@jmail.com",
            role: "teamLead"
        }]
    }
    ]
function UserTable(){
    const deleteUser=((event)=>{
        console.log("delete THIS USER ")
        console.log(event.target.value)
    })
    
    const changeRole = ((userID, event)=>{
        console.log("rolesChanged")
        console.log(userID, event.target.value)
    }
    )

    const [loadThisTeam, setTeam] = useState(()=>{
        let firstSetOfData = []
        AllTeams[0].members.map((member)=>{
            firstSetOfData.push({name: member.name, role: member.role, del: member.memberID})
        })
        return firstSetOfData
    })
    const changeSearch =(event) =>{
        console.log("seeing a new team")
        let membersValue = []
        let arrReturn = []
        for(let i = 0; i<AllTeams.length;i++){
            if(event.target.value === AllTeams[i].id){
                membersValue = AllTeams[i].members
                membersValue.map((member)=>{
                    arrReturn = [...arrReturn,{name: member.name, role: member.role, del: member.memberID}]
                })
                break;
            }
        }
        console.log(arrReturn)
        setTeam(()=>(arrReturn))
      }

    

    const [searchQuery, setSearchQuery] = useState(""); //creates a state variable "searchQuery" and function "setSearchQuery" to update it
    //whenever the searchQuery changes, runs the useMemo and filters through the fakeData data to only the task names that contain the searchQuery 
    const data = React.useMemo(()=> {return loadThisTeam.filter((task) =>{
            return task.name.toLowerCase().includes(searchQuery.toLowerCase())}
    );}, [searchQuery, loadThisTeam]);
    const columns = React.useMemo(() => [
        {
            Header: "Name",
            accessor: "name",
        },
        {
            Header: "Role",
            accessor:"role",
            Cell: (original) => (
                <select className="cellSelect" id={"role "+original.cell.row.values.del} defaultValue={original.value} onChange={(e)=>changeRole(original.cell.row.values.del, e)}>
                    <option value="teamLead">Team Lead</option>
                    <option value="admin">Admin</option>
                    <option value="teamMember">Team Member</option>
                </select>
              )
        },
        {
            Header: "",
            accessor: "del",
            Cell: (original) => (
                <button id= {"delete " + original.value} value={original.value} onClick={(e)=>deleteUser(e)}>
                    Delete
                </button>
              )
        },
        {
            Header: "",
            accessor: "edit",
            Cell: (original) => (
                <Link to='/edit-user-details' state={{accountToEdit: original.cell.row.values.del}}>
                <button key= {"edit" + original.cell.row.values.del} id ={"edit" + original.cell.row.values.del}>
                    Edit
                </button>
                </Link>
              )
        }
    ],[searchQuery, loadThisTeam]
    )

    const { getTableBodyProps, getTableProps, rows, prepareRow, headerGroups} = useTable({columns,data: data}, useSortBy)
    return(
        
        <div className='container'>
            <select name="searchThis" id="searchThis" onChange={changeSearch}>
                    {AllTeams.map((team)=>(
                        <option value={team.id}>{team.name}</option>
                    ))}
             </select>
            
            {/* pass searchQuery and setSearchQuery to SearchFilterSort component */}
            <SearchFilterSort
                searchQuery={searchQuery}
                setSearchQuery={setSearchQuery}
            />
            

            <table {...getTableProps()}>
                <thead>
                    {headerGroups.map((headerGroup) => (
                        <tr {...headerGroup.getHeaderGroupProps()}>
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
                                <tr key={row.id} {...row.getRowProps()}>
                                    {row.cells.map((cell)=>(
                                        <td key = {cell.id} {...cell.getCellProps()}>{cell.render("Cell")}</td>
                                    ))}
                                </tr>
                            )
                        })}
                </tbody>
            </table>
        </div>

    );
}

export default UserTable