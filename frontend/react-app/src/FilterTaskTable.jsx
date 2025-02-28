import React from 'react';


export const  FilterTaskTable = ( {column} ) => {
    const {filterValue, setFilter} = column

    return(
        <div> 
            <span>
                Search:{' '}
                <input type="text" value={filterValue || ''}
                onChange={e => setFilter(e.target.value)} placeholder=''></input>
            </span>
        </div>
      
    )


}
export default FilterTaskTable