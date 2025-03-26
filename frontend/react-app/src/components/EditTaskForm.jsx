
import {useForm} from 'react-hook-form'
function EditTaskForm({task}){
    const { register, handleSubmit, formState: {errors}} = useForm();
    const onSubmit = data => {
        console.log(data)
        alert("Your task was updated")
        window.location.href="/home";
    };
    return(
        <form onSubmit={handleSubmit(onSubmit)} className='editTaskForm'>
                        <label className='majorLabel'>
                                Task Name
                                <div>
                                <input type="text" name="name" id="name" className='input' defaultValue={task.name}{...register("name", { 
                                    required:{
                                        value: true,
                                        message: 'Please set a Task Name'
                                    }
                                    })}/>
                                </div>
                                </label>
                            <label className='majorLabel'>
                            <div>
                                {
                                    errors.name && <span>{errors.name.message}</span>
                                }
                            </div>
                    
                            </label>
                            <label className='majorLabel'>
                                Assign To:
                                {/*<div className='Checkboxs'>
                                    {team.map((teamMember)=>(
                                        <div className='checkbox' key = {teamMember.name}>
                                            {teamMember.name}
                                            <input type="checkbox" name="" id="" value = {teamMember.name} {...register("assignees")}/>
                                        </div>
                                    ))}
                                </div>*/}
                            </label>
                            <label className='majorLabel'>
                            Discription
                            
                            <div>
                                <input type="text" name="input-discription" id="discription" className='input' defaultValue={task.discription}  {...register("discription", { required: false })}/>
                            </div>
                            </label>
                            <input type="submit" value="Create Task" id="button"/>
                        
                    </form>
    )
}
export default EditTaskForm