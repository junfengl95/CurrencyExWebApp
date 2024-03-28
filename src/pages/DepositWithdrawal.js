import React, { useState, useEffect  } from 'react';
import '../styles/DepositWithdrawal.css';
import axios from 'axios';
import { confirmAlert } from 'react-confirm-alert'; 
import 'react-confirm-alert/src/react-confirm-alert.css';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button } from '@mui/material';
import { useNavigate } from "react-router-dom"


const DepositWithdrawal = () => {

  const [user, setUser] = useState(); 

  const [activeTab, setActiveTab] = useState('withdrawal');
  const [paymentMethod, setPaymentMethod] = useState('');
  const [fromCurrency, setFromCurrency] = useState('');
  const [toCurrency, setToCurrency] = useState('');
  const [amount, setAmount] = useState('');
  const [calculatedAmount, setCalculatedAmount] = useState('');
  const [withdrawalAmount, setwithdrawalAmount] = useState('');
  const [depositAmount, setDepositAmount] = useState('');
  const [currencyHolding, setCurrencyHolding] = useState(null);
  const [userID, setUserID] = useState(''); 
  const [openDialog, setOpenDialog] = useState(false);
  const [dialogAction, setDialogAction] = useState(null);
  const [confirmAmount, setConfirmAmount] = useState('');
  const [actionName, setActionName] = useState('');

      useEffect(() => {
        setUser(localStorage.getItem("username"));
    }, []);

    useEffect(() => {
      getUserDetails();
    }, []);

    const requestOptions = {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('bearer'),
      },
    };

    const getUserDetails = async () =>{
      try {
          const responseID = await axios.get(
              'http://localhost:8080/users/username/' + localStorage.getItem("username"),
              requestOptions      
          );

          const response = await axios.get(
            'http://localhost:8080/users/'+ responseID.data +'/holdingCurrencies',
            requestOptions
          );

          setCurrencyHolding(response.data.SGD);
          
      } catch (error) {
          console.error('Error getting user profile:', error);
      }


    }


  const handleTabClick = (tab) => {
    setActiveTab(tab);
  };

  const handlePaymentMethodChange = (e) => {
    setPaymentMethod(e.target.value);
  };

  const handleFromCurrencyChange = (e) => {
    setFromCurrency(e.target.value);
  };

  const handleToCurrencyChange = (e) => {
    setToCurrency(e.target.value);
  };

  const handleAmountChange = (e) => {
    setAmount(e.target.value);
  };

  const handlewithdrawalAmountChange = (e) => {
    setwithdrawalAmount(e.target.value);
  };

  const handlewithDepositAmountChange = (e) => {
    setDepositAmount(e.target.value);
  };

  const handleWithdraw = (withdrawalAmount) => {
    console.log(user);

    const requestOptions = {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('bearer'),
      },
    };
    
    const getUserIDURL = 'http://localhost:8080/users/username/' + user;
    
    axios
      .get(getUserIDURL, requestOptions)
      .then((response) => {
        setUserID(response.data);
        console.log(response.data);
    
        // Now that we have the userID, let's proceed with the withdrawal
        const baseURL1 = 'http://localhost:8080/users/' + response.data + '/payForTrade/SGD/' + withdrawalAmount;
    
        axios.post(baseURL1, {}, requestOptions)
          .then(response => { alert('Withdraw Successfully!'); 
          getUserDetails();})
          .catch(error => { alert('You have insufficient fund in your trading account!') });
      })
      .catch(error => {
        console.log(error);
      });

      
    
    // Remove the console.log(userID) here, as userID will not be set yet due to the async nature of axios.get
  };

  const handleDeposit = (depositAmount) => {
    const requestOptions = {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('bearer'),
      },
    };
    
    const getUserIDURL = 'http://localhost:8080/users/username/' + user;
    
    axios
    .get(getUserIDURL, requestOptions)
    .then((response) => {
      setUserID(response.data);
      console.log(response.data);
    
      const baseURL1 = 'http://localhost:8080/users/' + response.data + '/earnFromTrade/SGD/' + depositAmount;
      
        axios.post(baseURL1, {} ,requestOptions)
        .then(response => {alert('Deposit Successfully!');
        getUserDetails();})
        .catch(error => {console.log(error); })
    
      })
      .catch(error => {
        console.log(error);
      });

      
  };

  const openWithdrawDialog = (amount) => {
    setConfirmAmount(amount);
    setActionName('withdraw'); // Set the action name to 'withdraw'
    setDialogAction(() => () => handleWithdraw(amount));
    setOpenDialog(true);
  };

  const openDepositDialog = (amount) => {
    setConfirmAmount(amount);
    setActionName('deposit'); // Set the action name to 'deposit'
    setDialogAction(() => () => handleDeposit(amount)); // Assign handleDeposit directly
    setOpenDialog(true);
  };

  const handleClose = () => {
    setOpenDialog(false);
  };

  const confirmAction = () => {
    if (dialogAction) {
      dialogAction();
    }
    setOpenDialog(false);
  };

  


return (
  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', lineHeight: '2.5rem', fontSize: '1.2rem', paddingTop: '50px' , zoom: 1.2 }}>
    <h2>Bank Transfer Page</h2>
    {/* <button onClick={handleRefresh}>Refresh</button> */}
    <p className="holding-display">Your current Holding in SGD: {currencyHolding}</p>
    <div className="tab-container">
      <button onClick={() => handleTabClick('withdrawal')} className={`tab-button ${activeTab === 'withdrawal' ? 'active' : ''}`}>
        Withdrawal
      </button>
      <button onClick={() => handleTabClick('deposit')} className={`tab-button ${activeTab === 'deposit' ? 'active' : ''}`}>
        Deposit
      </button>
    </div>

    {activeTab === 'withdrawal' && (
      <div>
        <div style={{ marginTop: '20px'}}>
          <label htmlFor="amount">Amount: </label>
          <input type="number" id="amount" value={withdrawalAmount} onChange={handlewithdrawalAmountChange} />
        </div>
        <button style={{ marginTop: '20px'}} onClick={() => openWithdrawDialog(withdrawalAmount)}>Withdraw</button>
      </div>
    )}

    {activeTab === 'deposit' && (
      <div>
        <div style={{ marginTop: '20px'}}>
          <label style={{ marginLeft: '-3px'}} htmlFor="paymentMethod">Payment:</label>
          <select style={{ zoom:'1.17', width:'132px'}} id="paymentMethod" value={paymentMethod} onChange={handlePaymentMethodChange}>
            
            <option value="bank transfer">Bank Transfer</option>
            <option value="credit card">Credit Card</option>
            {/* Add more payment methods as needed */}
          </select>
        </div>

        <div style={{ marginTop: '10px'}}>
          <label htmlFor="amount">Amount: </label>
          <input type="number" id="amount" value={depositAmount} onChange={handlewithDepositAmountChange} />
        </div>
        <button style={{ marginTop: '20px'}} onClick={() => openDepositDialog(depositAmount)}>Deposit</button>
      </div>
    )}
    

    <Dialog open={openDialog} onClose={handleClose} style={{zoom: 1.1}}>
      <DialogTitle>Confirm Action</DialogTitle>
      <DialogContent>
        <p>
        Are you sure you want to {actionName} {confirmAmount}SGD from your account?
        </p>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose}>Cancel</Button>
        <Button onClick={confirmAction} color="primary" autoFocus>
          Confirm
        </Button>
      </DialogActions>
    </Dialog>
    
  </div>
  
);
};

export default DepositWithdrawal;