import { useEffect, useState } from 'react';
import axios from "axios"
import '../styles/PersonalDetails.css';
import { useNavigate } from "react-router-dom"
import { Dialog, DialogTitle, DialogContent, DialogActions, Button } from '@mui/material';

const PersonalDetails = () => {
  const [userpersonaldetails, setPersonalDetails] = useState()
  const [wallet, setWallet] = useState([]);

  const [editusername, setEditUsername] = useState(false);
  const [editemail, setEditEmail] = useState(false);
  const [editpassword, setEditPassword] = useState(false);

  const [newusername, setNewUsername] = useState('');
  const [newemail, setNewEmail] = useState('');
  const [newpassword, setNewPassword] = useState('');

  const [openDialog, setOpenDialog] = useState(false);
  const [confirmationfunction, setConfirmationFunction] = useState(() => { });
  const [closingfunction, setClosingFunction] = useState(() => { });

  const [originalvaluemessage, setOriginalValueMessage] = useState('');
  const [changingvaluemessage, setChangingValueMessage] = useState('');

  const [openerrorDialog, setOpenErrorDialog] = useState(false);
  const [errormessage, setErrorMessage] = useState('');
  const requestOptions = {
    headers: {
      Authorization: "Bearer " + localStorage.getItem('bearer')
    }
  }

  const navigate = useNavigate()

  useEffect(() => {
    if (localStorage.getItem("bearer") !== null && localStorage.getItem("bearer") !== "") {
      getUserDetails();
      fetchWalletDate();
    } else {
      localStorage.setItem("previousPage", "PersonalDetails");
      navigate('/login');
    }
  }, []);

  const getUserDetails = async () => {
    try {
      const response = await axios.get(
        'http://localhost:8080/users/' + localStorage.getItem("userID"),
        requestOptions
      );
      setPersonalDetails(response.data);
      console.log(userpersonaldetails);
      console.log(response.data);
    } catch (error) {
      console.error('Error getting user profile:', error);
    }
  }

  const updateUserName = async () => {
    try {
      const response = await axios.put(
        'http://localhost:8080/users/' + localStorage.getItem("userID") + '/changeUsername/' + newusername,
        requestOptions
      );
      setPersonalDetails(response.data);
      localStorage.setItem("username", newusername)
    } catch (error) {
      console.error('Error getting user profile:', error);
    }
    setEditUsername(false)
  }

  const updateEmail = async () => {
    try {
      const response = await axios.put(
        'http://localhost:8080/users/' + localStorage.getItem("userID") + '/changeUserEmail/' + newemail,
        requestOptions
      );
      setPersonalDetails(response.data);
    } catch (error) {
      console.error('Error getting user profile:', error);
    }
    setEditEmail(false)
  }


  const updatePassword = async () => {
    try {
      const response = await axios.put(
        'http://localhost:8080/users/' + localStorage.getItem("userID") + '/changeUserPassword/' + newpassword,
        requestOptions
      );
      setPersonalDetails(response.data);
    } catch (error) {
      console.error('Error getting user profile:', error);
    }
    setEditPassword(false)
  }

  const fetchWalletDate = async () => {
    try {
      const responseWallet = await axios.get(
        'http://localhost:8080/users/' + localStorage.getItem("userID") + '/holdingCurrencies'
      );
      setWallet(responseWallet.data)
    } catch (error) {
      console.error('Error getting wallet data:', error);
    };
  };


  const openConfirmDialog = async (dialogType) => {
    switch (dialogType) {
      case "USERNAME":
        console.log("saving username");
        try {
          const response = await axios.get(
            'http://localhost:8080/users/username/' + newusername,
            requestOptions
          );
          setOpenErrorDialog(true);
          setErrorMessage("Username: " + newusername + " already exists, please pick a different username");
        } catch (error) {
          console.error('Error checking username existence:', error);
          // Username doesn't exist, proceed with the confirmation dialog
          setConfirmationFunction(() => () => updateUserName());
          setClosingFunction(() => () => setEditUsername(false));
          setOriginalValueMessage("Original Value: " + userpersonaldetails.username)
          setChangingValueMessage("New Value: " + newusername)
          setOpenDialog(true);
        }
        break;
      case "EMAIL":
        console.log("saving email");
        // Perform actions or set states for editing email
        setConfirmationFunction(() => () => updateEmail());
        setClosingFunction(() => () => setEditEmail(false));
        setOriginalValueMessage("Original Value: " + userpersonaldetails.email)
        setChangingValueMessage("New Value: " + newemail)
        setOpenDialog(true);
        break;
      case "PASSWORD":
        console.log("saving password");
        // Perform actions or set states for editing password
        setConfirmationFunction(() => () => updatePassword());
        setClosingFunction(() => () => setEditPassword(false));
        setOriginalValueMessage("Original Value: ********")
        setChangingValueMessage("New Value: " + newpassword)
        setOpenDialog(true);
        break;
      default:
        break;
    }
  };

  const handleClose = () => {
    if (closingfunction) {
      closingfunction(false)
      // console.log("Closing function called!")
    }
    setOpenDialog(false);
  };

  const confirmAction = () => {
    if (confirmationfunction) {
      confirmationfunction();
      // console.log("Confirmation function called!")
    }
    setOpenDialog(false);
  };

  return (
    <div>
      <h1>This is the PersonalDetails Page</h1>
      <h2>{localStorage.getItem("username")}'s Personal Details</h2>

      {userpersonaldetails &&
        <table class="personaldetail">
          <tr>
            <th>Username</th>
            {editusername ? (
              <div class="personaldetail">
                <td><input
                  type="text"
                  placeholder={userpersonaldetails.username}
                  onChange={e => setNewUsername(e.target.value)}
                /></td>
                <td><button onClick={() => openConfirmDialog("USERNAME")}>Save</button></td>
              </div>
            ) : (
              <div class="personaldetail">
                <td>{userpersonaldetails.username}</td>
                <td><button onClick={() => setEditUsername(true)}>Edit</button></td>
              </div>
            )}
          </tr>
          <tr>
            <th>Email</th>
            {editemail ? (
              <div class="personaldetail">
                <td><input
                  type="text"
                  placeholder={userpersonaldetails.email}
                  onChange={e => setNewEmail(e.target.value)}
                /></td>
                <td><button onClick={() => openConfirmDialog("EMAIL")}>Save</button></td>
              </div>
            ) : (
              <div class="personaldetail">
                <td>{userpersonaldetails.email}</td>
                <td><button onClick={() => setEditEmail(true)}>Edit</button></td>
              </div>
            )}
          </tr>
          <tr>
            <th>Password</th>
            {editpassword ? (
              <div class="personaldetail">
                <td><input
                  type="text"
                  placeholder="**********"
                  onChange={e => setNewPassword(e.target.value)}
                /></td>
                <td><button onClick={() => openConfirmDialog("PASSWORD")}>Save</button></td>
              </div>
            ) : (
              <div class="personaldetail">
                <td>**********</td>
                <td><button onClick={() => setEditPassword(true)}>Edit</button></td>
              </div>
            )}
          </tr>
        </table>}

      <table border="1">
        <thead>
          <tr>
            <th>Currency</th>
            <th>Amount</th>
          </tr>
        </thead>
        <tbody>
          {Object.entries(wallet).map(([currency, amount]) => (
            <tr key={currency}>
              <td>{currency}</td>
              <td>{amount}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <Dialog open={openDialog} onClose={handleClose} style={{ zoom: 1.1 }}>
        <DialogTitle>Confirm Action</DialogTitle>
        <DialogContent>
          <p>
            Confirm Changes?
            <br />
            <br />
            {changingvaluemessage}
            <br />
            {originalvaluemessage}
            <br />
          </p>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Revert</Button>
          <Button onClick={confirmAction} color="primary" autoFocus>
            Confirm
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={openerrorDialog} style={{ zoom: 1.1 }}>
        <DialogTitle>Error</DialogTitle>
        <DialogContent>
          <p>
            ERROR
            <br />
            <br />
            {errormessage}
          </p>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenErrorDialog(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </div>
  )

}
export default PersonalDetails;