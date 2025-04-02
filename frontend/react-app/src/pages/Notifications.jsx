import React, { useEffect, useMemo, useState } from 'react';
import '../css/Notifications.css';
import Notification from "../components/Notification";
import Header from "../components/Header";
import { getReadNotifications, getUnreadNotifications, markAsRead, markAsUnread, deleteNotification as deleteThisNotification} from '../api/notificationApi';
import { useCookies } from 'react-cookie';

const Notifications = () => {
    const [cookies] = useCookies(['userInfo'])

    const [readNotifications, setReadNotifications] = useState();
    const [unreadNotifications, setUnreadNotifications] = useState();
    const [loading, setLoading] = useState(true);

    useEffect(()=>{
        console.log('useEffect')
        async function myNotifications() {
            try {
                const readNotificationsResponse = await getReadNotifications(cookies.userInfo.accountId);
                const unreadNotificationsResponse = await getUnreadNotifications(cookies.userInfo.accountId);
                setReadNotifications(readNotificationsResponse);
                setUnreadNotifications(unreadNotificationsResponse);
            } catch (error) {
                await alert(error)
            }finally{
                setLoading(false);
            }
        }
        myNotifications()

    },[])

    const memoizedUnReadNotifications = useMemo(()=>unreadNotifications,[unreadNotifications])
    const memoizedReadNotifications = useMemo(()=>readNotifications,[readNotifications])

    
    const toggleRead = async(id, isRead) => {
        console.log(id, isRead)
        try {
            if(isRead){
                setReadNotifications(
                    readNotifications.filter(async(notif)=>{
                        if(notif.notificationId === id){
                            setUnreadNotifications((prev)=>([...prev, notif]))
                            const response = await markAsUnread(id);
                            return false;
                        }else{
                            return true;
                        }
                    })
                )
            }else{
                setUnreadNotifications(
                    unreadNotifications.filter(async(notif)=>{
                        if(notif.notificationId === id){
                            setReadNotifications((prev)=>([...prev, notif]))
                            const response = await markAsRead(id);
                            return false;
                        }else{
                            return true;
                        }
                    })
                )
            }
        } catch (error) {
            await alert(error);
        }
    };

    const deleteNotification = async(id, isRead) => {
        if(isRead){
            setReadNotifications(readNotifications.filter(notif => notif.notificationId !== id));}
        else{
            setUnreadNotifications(unreadNotifications.filter(notif => notif.notificationId !== id));}
       try {
        const response = await deleteThisNotification(id)
       } catch (error) {
        alert(error);
       }
    };

    // Store filtered notifications to avoid redundant filtering
    //const unreadNotifications = notifications.filter(notif => !notif.read);
    //const readNotifications = notifications.filter(notif => notif.read);

    // reusable section component
    const NotificationSection = ({ title, items }) => (
        items.length > 0 && (
            <>
                <tr>
                    <td colSpan="4" className="subHeader">{title}</td>
                </tr>
                {items.map(notif => (
                    <Notification 
                        key={notif.id} 
                        notif={notif} 
                        toggleRead={toggleRead} 
                        deleteNotification={deleteNotification} 
                    />
                ))}
            </>
        )
    );
    if(loading){
        return(<div>...Loading</div>)
    }
    return (
        <div id="notifContainer">
            <table>
                <thead>
                    <tr>
                        <td colSpan="4">Notifications</td>
                    </tr>
                </thead>
                <tbody>
                    <NotificationSection title="Unread" items={memoizedUnReadNotifications} />
                    <NotificationSection title="Read" items={memoizedReadNotifications} />
                </tbody>
            </table>
        </div>
    );
};

export default Notifications;


//before refactoring 


// import React from 'react';
// import {useEffect, useState} from 'react';
// import '../css/Notifications.css';
// import Notification from "../components/Notification";

// const Notifications = () => {
//     const [notifications, setNotifications] = useState([
//         {id: 1, team: "Team 2", message: 'Bob edited your task "Create wireframe"', read: false},
//         { id: 2, team: "Team 1", message: 'Mary completed your task "Code things"', read: false },
//         { id: 3, team: "Team 1", message: 'Adam assigned you to "Code more"', read: true }
//     ]);

//     //mark as read or unread
//     const toggleRead = (id) => {
//         setNotifications(notifications.map(notif =>
//             notif.id === id ? { ...notif, read: !notif.read } : notif
//         ));
//     };

//     //delete any notification
//     const deleteNotification = (id) => {
//         setNotifications(notifications.filter(notif => notif.id !== id));
//     };

//     return (
//         <div id="notifContainer">
//            <table>
//                 <thead>
//                     <tr>
//                         <td colSpan="4">Notifications</td>
//                     </tr>
//                 </thead>
//                 <tbody>
//                     {notifications.some(notif => !notif.read) && (
//                         <>
//                             <tr>
//                                 <td colSpan="4" className="subHeader">Unread</td>
//                             </tr>
//                             {notifications.filter(notif => !notif.read).map(notif => (
//                                 <Notification key={notif.id} notif={notif} toggleRead={toggleRead} deleteNotification={deleteNotification} />
//                             ))}
//                         </>
//                     )}

//                     {notifications.some(notif => notif.read) && (
//                         <>
//                             <tr>
//                                 <td colSpan="4" className="subHeader">Read</td>
//                             </tr>
//                             {notifications.filter(notif => notif.read).map(notif => (
//                                 <Notification key={notif.id} notif={notif} toggleRead={toggleRead} deleteNotification={deleteNotification} />
//                             ))}
//                         </>
//                     )}
//                 </tbody>
//             </table>
//         </div>
//     )
// }

// export default Notifications;