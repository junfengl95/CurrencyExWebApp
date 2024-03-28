import axios from "axios"
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom"
import '../styles/Login.css'
import { Dialog, DialogTitle, DialogContent, DialogActions, Button } from '@mui/material';



const Login = () => {

  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [errorMsg, setErrorMsg] = useState("")

  const [openregisteruser, setOpenRegisterUser] = useState(false);
  const [registerusername, setRegisterUsername] = useState("")
  const [registeremail, setRegisterEmail] = useState("")
  const [registerpassword, setRegisterPassword] = useState("")

  const [openmessagedialog, setOpenMessageDialog] = useState(false);
  const [dialogtitle, setDialogTitle] = useState('');
  const [dialogmessage, setDialogmMessage] = useState('');

  const navigate = useNavigate()

  useEffect(() => {
    if (localStorage.getItem("previousPage") !== null && localStorage.getItem("previousPage") !== "") {
      setErrorMsg("Please Log In to access " + localStorage.getItem("previousPage"))
    }
    else {
      setErrorMsg()
    }
  }, []);

  useEffect(() => {
    const handleBeforeUnload = (event) => {
      console.log('User is leaving the page');
      setErrorMsg("Please Log In to access " + localStorage.getItem("previousPage"));
      localStorage.removeItem("previousPage");
      // Do something before the user leaves the page
    };

    const unlisten = () => {
      console.log('User is navigating within the application');
      // Do something when the user navigates within the application
    };

    window.addEventListener('beforeunload', handleBeforeUnload);

    return () => {
      //console.log('Component is unmounting');
      window.removeEventListener('beforeunload', handleBeforeUnload);
      // Do something before the component unmounts
    };
  }, [])

  const handleSubmit = (event) => {

    const endpoint = 'http://localhost:8080/auth/login'
    const requestOptions = {
      auth: {
        username: username,
        password: password
      }
    }
    axios.post(endpoint, {}, requestOptions)
      .then(response => {
        localStorage.setItem("bearer", response.data)
        localStorage.setItem("username", username)
        getUserID()

      })
      .catch(error => {

        setErrorMsg("Invalid Username or Password")
      })

    event.preventDefault()
  }

  const getUserID = async () => {
    try {
      const responseID = await axios.get(
        'http://localhost:8080/users/username/' + username,
        { Authorization: "Bearer " + localStorage.getItem('bearer') }
      );
      localStorage.setItem("userID", responseID.data)
      if (localStorage.getItem("previousPage") !== null && localStorage.getItem("previousPage") !== "") {
        navigate("/" + localStorage.getItem("previousPage"))
      }
      else {
        navigate('/userhome/')
      }
    } catch (error) {
      console.error('Error getting user ID:', error);
    }

  }

  const registerNewUser = async () => {

    if (registerusername.trim() === '' || registerpassword.trim() === '' || registeremail.trim() === '') {
      setOpenMessageDialog(true);
      let temperrorstring = "";
      if(registerusername.trim() === '')
      {
        temperrorstring += " Username"
      }
      if(registerpassword.trim() === '')
      {
        temperrorstring += " Password"
      }
      if(registeremail.trim() === '')
      {
        temperrorstring += " Email"
      }
      setDialogTitle("ERROR");
      setDialogmMessage("Following fields are empty "+ temperrorstring + " Please fill in these fields and try again");
    } else {
      try {
        const response = await axios.get(
          'http://localhost:8080/users/username/' + registerusername
        );
        //console.log("Username: " + registerusername + " already exists, please pick a different username")
        setOpenMessageDialog(true);
        setDialogTitle("ERROR");
        setDialogmMessage("Username: " + registerusername + " already exists, please pick a different username");
      } catch (error) {
        console.error('Error checking username existence:', error);
        const newUser = {
          username: registerusername,
          password: registerpassword,
          email: registeremail
        };
  
        const endpoint = 'http://localhost:8080/users/register'
        try {
          axios.post(endpoint, newUser)
        } catch (error) {
          console.error('Error Creating New User', error);
        }
        closeRegisterNewUser()
        setDialogTitle("SUCCESS");
        setDialogmMessage("Account with Username: " + registerusername + " Password: " + registerpassword + " Email: " + registeremail + " has been created successfully!");
        setOpenMessageDialog(true);
      }
    }





  }

  const closeRegisterNewUser = () => {



    setRegisterUsername('');
    setRegisterEmail('');
    setRegisterPassword('');
    setOpenRegisterUser(false)
  }

  return (
    <div className="login-container">
      <form onSubmit={handleSubmit} className="login-form">
        <h2>Login to Your Account</h2>
        <input
          type="text"
          placeholder="Username"
          onChange={e => setUsername(e.target.value)}
        />
        <input
          type="password"
          placeholder="Password"
          onChange={e => setPassword(e.target.value)}
        />
        <button type="submit">Log in</button>
        <button type="button" onClick={() => setOpenRegisterUser(true)}>Register New User</button>
      </form>

      {errorMsg && <p className="error-msg">{errorMsg}</p>}
      <Dialog open={openregisteruser} onClose={closeRegisterNewUser} style={{ zoom: 1.1 }}>
        <DialogTitle>Create New Account</DialogTitle>
        <DialogContent>
          <form className="login-form">
            Username: <input
              type="text"
              placeholder="Username"
              onChange={e => setRegisterUsername(e.target.value)}
            />
            Password: <input
              type="text"
              placeholder="Password"
              onChange={e => setRegisterPassword(e.target.value)}
            />
            Email: <input
              type="text"
              placeholder="Email"
              onChange={e => setRegisterEmail(e.target.value)}
            />
            <button type="button" onClick={() => registerNewUser()}>Register</button>
          </form>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => closeRegisterNewUser(false)}>Back</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={openmessagedialog} onClose={() => setOpenMessageDialog(false)} style={{ zoom: 1.1 }}>
        <DialogTitle>{dialogtitle}</DialogTitle>
        <DialogContent>
          <p>
            <br />
            {dialogmessage}
          </p>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenMessageDialog(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default Login;