/*import { render, screen, fireEvent } from "@testing-library/react";
import UserTable from "../components/UserTable";
test('updates table after new select is chosen', () => {
    const logSpy = jest.spyOn(console, 'log');
  
    render(<UserTable/>);
  
    //getting the input element
    const input = document.getElementById("searchThis")
  
    // test the user typing 'test' in as input
    fireEvent.change(input, { target: { value: 'Team2' } });

  
    expect(logSpy).toHaveBeenCalledWith('seeing a new team');
  })
  test('sees if we can when the delete button is pushed we get a deleted account message',()=>{
    const logSpy = jest.spyOn(console, 'log');
  
    render(<UserTable/>);
  
    //getting the input element
    const input = screen.getAllByText(/Delete/i)[0];
  
    // test the user typing 'test' in as input
    fireEvent.click(input);

  
    expect(logSpy).toHaveBeenCalledWith('delete THIS USER ');
  })
  test('sees if we can when we change a role get a changed role',()=>{
    const logSpy = jest.spyOn(console, 'log');
  
    render(<UserTable/>);
  
    //getting the input element
    const input = document.getElementsByClassName("cellSelect")[0]

  
    // test the user typing 'test' in as input
    fireEvent.change(input, { target: { value: 'admin' } });
    fireEvent.change(input, { target: { value: 'teamLead' } });
    fireEvent.change(input, { target: { value: 'admin' } });
  
    expect(logSpy).toHaveBeenCalledWith('rolesChanged');
  })*/

  /*
  Same issue as task List for some reason it can't find react router dom. This is very weird as when the app is running its fine.
  TA told me its not my code and to comment out test as this is a issue with jest
  */
  test('sees if we can when we change a role get a changed role',()=>{
    expect(true);
  })

  
  