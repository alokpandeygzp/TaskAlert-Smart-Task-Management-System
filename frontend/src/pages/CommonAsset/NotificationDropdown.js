import React, { useState, useEffect, useCallback } from 'react';
import api from '../../ApiConfig';
import { FaBell } from 'react-icons/fa';
import "../../css_styles/NotificationDropdown.css";

const NotificationDropdown = ({ userId }) => {
  const [notifications, setNotifications] = useState([]);
  const [unreadNotifications, setUnreadNotifications] = useState([]);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [toastNotification, setToastNotification] = useState(null); // State for toast notification
  const [previousUnreadCount, setPreviousUnreadCount] = useState(null); // Track unread count per session

  // Fetch unread notifications
  const fetchUnreadNotifications = useCallback(async () => {
    try {
      const response = await api.get(`/api/notifications/unread/${userId}`);
      const currentUnreadCount = response.data.length;
      setUnreadNotifications(response.data);

      // If the unread notification count has increased and it's different from the previous count
      if (previousUnreadCount !== null && currentUnreadCount > previousUnreadCount) {
        // Show the topmost notification as a toast
        const newNotification = response.data[0]; // Take the first unread notification
        setToastNotification({
          message: newNotification.message,
          id: newNotification.id
        });
      }

      // Update the saved unread count to the current count after comparison
      setPreviousUnreadCount(currentUnreadCount);
    } catch (error) {
      console.error('Error fetching unread notifications:', error);
    }
  }, [userId, previousUnreadCount]);

  // Fetch all notifications
  const fetchAllNotifications = useCallback(async () => {
    try {
      const response = await api.get(`/api/notifications/all/${userId}`);
      setNotifications(response.data);
    } catch (error) {
      console.error('Error fetching notifications:', error);
    }
  }, [userId]);

  // Mark notification as read
  const markAsRead = async (notificationId) => {
    try {
      await api.put(`/api/notifications/read/${notificationId}`);
      setUnreadNotifications(unreadNotifications.filter(notification => notification.id !== notificationId));
      setNotifications(notifications.map(notification => 
        notification.id === notificationId ? { ...notification, read: true } : notification
      ));
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  useEffect(() => {
    // Fetch notifications initially when component mounts
    fetchUnreadNotifications();
    fetchAllNotifications();
  }, [fetchUnreadNotifications, fetchAllNotifications]);

  // Polling mechanism to check for new notifications every 60 seconds
  useEffect(() => {
    const interval = setInterval(() => {
      fetchUnreadNotifications();
      fetchAllNotifications();
    }, 60000); // 60 seconds

    return () => clearInterval(interval); // Cleanup interval on component unmount
  }, [fetchUnreadNotifications, fetchAllNotifications]);

  // Automatically dismiss toast after 5 seconds
  useEffect(() => {
    if (toastNotification) {
      const timer = setTimeout(() => {
        setToastNotification(null);
      }, 5000); // 5 seconds auto-dismiss
      return () => clearTimeout(timer); // Cleanup timeout on component unmount or toast change
    }
  }, [toastNotification]);

  const toggleDropdown = () => {
    setIsDropdownOpen(!isDropdownOpen);
    if (!isDropdownOpen) {
      fetchUnreadNotifications(); // Fetch unread notifications when dropdown opens
    }
  };

  const refreshNotifications = () => {
    fetchUnreadNotifications();
    fetchAllNotifications();
  };

  const formatDate = (dateArray) => {
    let date = `${dateArray[0]}/${dateArray[1]}/${dateArray[2]} `;
    let hours = dateArray[3];
    let minutes = dateArray[4];
    let seconds = dateArray[5];

    const ampm = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12;
    hours = hours ? hours : 12;
    minutes = minutes < 10 ? '0' + minutes : minutes;
    seconds = seconds < 10 ? '0' + seconds : seconds;

    date += `${hours}:${minutes}:${seconds} ${ampm}`;
    return date;
  };

  // Function to close the toast notification manually
  const closeToast = () => {
    setToastNotification(null);
  };

  return (
    <div className="notification-container">
      <button className="notification-icon" onClick={toggleDropdown}>
        <FaBell size={24} color="white" />
        <span className="notification-count">{unreadNotifications.length}</span>
      </button>

      {isDropdownOpen && (
        <div className="notification-dropdown">
          <button
            className="refresh-button"
            onClick={refreshNotifications}
            title="Refresh Notifications"
          >
            ⟳
          </button>
          <h5>Unread Notifications</h5>
          {unreadNotifications.length > 0 ? (
            <ul>
              {unreadNotifications.map((notification) => (
                <li
                  className="notification unreadnotification"
                  key={notification.id}
                  onClick={() => markAsRead(notification.id)}
                >
                  <span className="unread">{notification.message}</span>
                  <div className="notification-time">
                    {formatDate(notification.createdAt)}
                  </div>
                </li>
              ))}
            </ul>
          ) : (
            <p>No unread notifications.</p>
          )}
          <hr />
          <h5>All Notifications</h5>
          <ul>
            {notifications.map((notification) => (
              <li
                className={`notification ${notification.read ? 'readnotification' : 'unreadnotification'}`}
                key={notification.id}
              >
                <span>{notification.message}</span>
                <div className="notification-time">
                  {formatDate(notification.createdAt)}
                </div>
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* Toast Notification */}
      {toastNotification && (
        <div className="toast-notification">
          <span>{toastNotification.message}</span>
          <button className="close-toast" onClick={closeToast}>×</button>
        </div>
      )}
    </div>
  );
};

export default NotificationDropdown;







// import React, { useState, useEffect, useCallback } from 'react';
// import api from '../../ApiConfig';
// import { FaBell } from 'react-icons/fa';
// import "../../css_styles/NotificationDropdown.css"; 

// const NotificationDropdown = ({ userId }) => {
//   const [notifications, setNotifications] = useState([]);
//   const [unreadNotifications, setUnreadNotifications] = useState([]);
//   const [isDropdownOpen, setIsDropdownOpen] = useState(false);

//   // Fetch unread notifications
//   const fetchUnreadNotifications = useCallback(async () => {
//     try {
//       const response = await api.get(`/api/notifications/unread/${userId}`);
//       setUnreadNotifications(response.data);
//     } catch (error) {
//       console.error('Error fetching unread notifications:', error);
//     }
//   }, [userId]);

//   // Fetch all notifications
//   const fetchAllNotifications = useCallback(async () => {
//     try {
//       const response = await api.get(`/api/notifications/all/${userId}`);
//       setNotifications(response.data);
//     } catch (error) {
//       console.error('Error fetching notifications:', error);
//     }
//   }, [userId]);

//   // Mark notification as read
//   const markAsRead = async (notificationId) => {
//     try {
//       await api.put(`/api/notifications/read/${notificationId}`);
//       // Update local state after marking as read
//       setUnreadNotifications(unreadNotifications.filter(notification => notification.id !== notificationId));
//       setNotifications(notifications.map(notification => 
//         notification.id === notificationId ? { ...notification, read: true } : notification
//       ));
//     } catch (error) {
//       console.error('Error marking notification as read:', error);
//     }
//   };

//   useEffect(() => {
//     fetchUnreadNotifications();
//     fetchAllNotifications();
//   }, [fetchUnreadNotifications, fetchAllNotifications]);

//   // Polling mechanism to check for new notifications every 60 seconds
//   useEffect(() => {
//     const interval = setInterval(() => {
//       fetchUnreadNotifications();
//       fetchAllNotifications();
//     }, 60000); // 60 seconds

//     return () => clearInterval(interval); // Cleanup interval on component unmount
//   }, [fetchUnreadNotifications, fetchAllNotifications]);

//   const toggleDropdown = () => {
//     setIsDropdownOpen(!isDropdownOpen);
//     if (!isDropdownOpen) {
//       fetchUnreadNotifications(); // Fetch unread notifications when dropdown opens
//     }
//   };

//   const refreshNotifications = () => {
//     fetchUnreadNotifications();
//     fetchAllNotifications();
//   };

//   const formatDate = (dateArray) => {
//     let date = `${dateArray[0]}/${dateArray[1]}/${dateArray[2]} `;
//     let hours = dateArray[3];
//     let minutes = dateArray[4];
//     let seconds = dateArray[5];

//     const ampm = hours >= 12 ? 'PM' : 'AM';
//     hours = hours % 12;
//     hours = hours ? hours : 12;
//     minutes = minutes < 10 ? '0' + minutes : minutes;
//     seconds = seconds < 10 ? '0' + seconds : seconds;

//     date += `${hours}:${minutes}:${seconds} ${ampm}`;
//     return date;
//   };

//   return (
//     <div className="notification-container">
//       <button className="notification-icon" onClick={toggleDropdown}>
//         <FaBell size={24} color="white" />
//         <span className="notification-count">{unreadNotifications.length}</span>
//       </button>
//       {isDropdownOpen && (
//         <div className="notification-dropdown">
//           <button
//             className="refresh-button"
//             onClick={refreshNotifications}
//             title="Refresh Notifications"
//           >
//             ⟳
//           </button>
//           <h5>Unread Notifications</h5>
//           {unreadNotifications.length > 0 ? (
//             <ul>
//               {unreadNotifications.map((notification) => (
//                 <li
//                   className="notification unreadnotification"
//                   key={notification.id}
//                   onClick={() => markAsRead(notification.id)}
//                 >
//                   <span className="unread">{notification.message}</span>
//                   <div className="notification-time">
//                     {formatDate(notification.createdAt)}
//                   </div>
//                 </li>
//               ))}
//             </ul>
//           ) : (
//             <p>No unread notifications.</p>
//           )}
//           <hr />
//           <h5>All Notifications</h5>
//           <ul>
//             {notifications.map((notification) => (
//               <li
//                 className={`notification ${notification.read ? 'readnotification' : 'unreadnotification'}`}
//                 key={notification.id}
//               >
//                 <span>{notification.message}</span>
//                 <div className="notification-time">
//                   {formatDate(notification.createdAt)}
//                 </div>
//               </li>
//             ))}
//           </ul>
//         </div>
//       )}
//     </div>
//   );
// };

// export default NotificationDropdown;
