import React from 'react';
import "../styles/OutstandingTradesBoard.css";
import { useState, useEffect } from "react";
import axios from "axios"
import { Dialog, DialogTitle, DialogContent, DialogActions, Button } from '@mui/material';
import { useNavigate } from "react-router-dom"

const OrderBoard = () => {

    const [openDialog, setOpenDialog] = useState(false);
    const [dialogAction, setDialogAction] = useState(null);
    const [confirmAmount, setConfirmAmount] = useState('');
    const [actionName, setActionName] = useState('');
    
    const [orders, setOrders] = useState([]);
    const [forwardOrders, setForwardOrders] = useState([]);

    const requestOptions = {
      headers: {
          Authorization: "Bearer "+ localStorage.getItem('bearer')
      }
   }
    
   const navigate = useNavigate()

   useEffect(() => {
     if (localStorage.getItem("bearer") !== null && localStorage.getItem("bearer") !== "") {
     } else {
         localStorage.setItem("previousPage", "OutstandingTradesboard");
         navigate('/login');
     }
 }, []);

    useEffect(() => {
      getUserDetails();
    }, []);

    const getUserDetails = async () =>{

            try {
                const responseID = await axios.get(
                    'http://localhost:8080/users/getPendingTrades/name/' + localStorage.getItem("username"),
                    requestOptions
                );
                setOrders(responseID.data);
            } catch (error) {
                console.error('Error getting user profile:', error);
            }

            try {

              const responseID = await axios.get(
                'http://localhost:8080/users/username/' + localStorage.getItem("username"),
                requestOptions
              );

              const response = await axios.get(
                  'http://localhost:8080/api/v1/trades/allForward/' + responseID.data,
                  requestOptions
              );
              setForwardOrders(response.data);
          } catch (error) {
              console.error('Error getting user profile:', error);
          }



    }

    const orders2 = [
      {
        currencyFrom: "USD",
        currencyTo: "EUR",
        price: 7.0,
        amountFrom: 100,
        amountTo: 120,
        orderType: "limit",
        executionDate: "2022-01-15",
        expiryDate: "2022-01-18"
      },
      {
        currencyFrom: "GBP",
        currencyTo: "JPY",
        price: 8.5,
        amountFrom: 200,
        amountTo: 300,
        orderType: "market",
        executionDate: "2022-02-20",
        expiryDate: "2022-02-25"
      }
    ];
  
    const handleCancel = async(currencyFrom, currencyTo, tradeId, amountFrom, orderType) => {
      if (orderType !== "forward") {

            try {
                  const responseID = await axios.get(
                    'http://localhost:8080/users/username/' + localStorage.getItem("username"),
                    requestOptions
                );

                  await axios.post(
                    'http://localhost:8080/users/' + responseID.data + '/earnFromTrade/' + currencyFrom + '/' + amountFrom, {},
                    requestOptions
                );

            } catch (error) {
                console.error('Error restoring the currency:', error);
            }
      }
      try {
        
        await axios.post(
            'http://localhost:8080/api/v1/trades/delete/' + tradeId, {},
            requestOptions
        );

          // Manually update the orders state to filter out the deleted order
          setOrders(forwardOrders.filter(order => order.tradeId !== tradeId));
          alert(`Cancelled order: ${currencyFrom} to ${currencyTo}`);
      } catch (error) {
          console.error('Error cancelling the order:', error);
      }

      getUserDetails();




  };
  
    const handleRefresh = () => {
      // Implement refresh logic here
      getUserDetails();
      console.log('Refresh order board');
    };

    const openCancelDialog = (currencyFrom, currencyTo, tradeId, amountFrom, orderType) => {
      console.log(typeof dialogAction)
      setDialogAction(() => () => handleCancel(currencyFrom, currencyTo, tradeId, amountFrom, orderType)); 
      setOpenDialog(true);
    };

    const handleClose = () => {
      setOpenDialog(false);
    };
  
    const confirmAction = () => {
      console.log(typeof dialogAction)
      if (dialogAction) {
        dialogAction();
      }
      setOpenDialog(false);
    };

    const test =  [
      {
          "tradeId": 4,
          "currencyFrom": "SGD",
          "currencyTo": "HKD",
          "orderType": "forward",
          "price": 5.75,
          "amountFrom": 1000.0,
          "amountTo": 5750.0,
          "expiryDate": "30/12/2024",
          "executionDate": "2024-02-07T12:06:08.846695",
          "forwardDate": null,
          "tradeStatus": "pending",
          "empty": false
      }
  ]
  
    return (
      <div >
        <div className="order-board2">
        <h2>Your Pending Orders </h2>
        <button onClick={handleRefresh} className="refresh-button">Refresh</button>
        <table className="order-table">
          <thead>
            <tr>
              <th>TradeID</th>
              <th>From</th>
              <th>To</th>
              <th>Price</th>
              <th>Amount From</th>
              <th>Amount To</th>
              <th>Order Type</th>
              <th>Execution Date</th>
              <th>Forward Date</th>
              <th>Expiry Date</th>
              <th>Actions</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order, index) => (
              <tr key={index}>
                <td>{order.tradeId}</td>
                <td>{order.currencyFrom}</td>
                <td>{order.currencyTo}</td>
                <td>{order.price}</td>
                <td>{order.amountFrom}</td>
                <td>{order.amountTo}</td>
                <td>{order.orderType}</td>
                <td>{order.executionDate.slice(0, 10)}</td>
                <td>{order.forwardDate ? order.forwardDate : "N.A."}</td>
                <td>{order.expiryDate}</td>
                <td>{order.tradeStatus}</td>
                <td>
                  <button className="cancel-button" onClick={() => openCancelDialog(order.currencyFrom, order.currencyTo, order.tradeId, order.amountFrom, order.orderType)}>Cancel</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        </div>
        <div className="order-board2">
          <h2>Foward Trades Board</h2>
          <button onClick={handleRefresh} className="refresh-button">Refresh</button>
          <table className="order-table">
            <thead>
              <tr>
                <th>TradeID</th>
                <th>From</th>
                <th>To</th>
                <th>Forward Price</th>
                <th>Amount From</th>
                <th>Amount To</th>
                <th>Order Type</th>
                <th>Contract Purchasing Date</th>
                <th>Forward Date</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {forwardOrders.map((forwardOrder, index) => (
                <tr key={index}>
                  <td>{forwardOrder.tradeId}</td>
                  <td>{forwardOrder.currencyFrom}</td>
                  <td>{forwardOrder.currencyTo}</td>
                  <td>{forwardOrder.price}</td>
                  <td>{forwardOrder.amountFrom}</td>
                  <td>{forwardOrder.amountTo}</td>
                  <td>{forwardOrder.orderType}</td>             
                  <td>{forwardOrder.executionDate.slice(0, 10)}</td>
                  <td>{forwardOrder.forwardDate ? forwardOrder.forwardDate : "N.A."}</td>
                  <td>{forwardOrder.tradeStatus}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
               
        <Dialog open={openDialog} onClose={handleClose} style={{zoom: 1.1}}>
          <DialogTitle>Confirm Action</DialogTitle>
          <DialogContent>
            <p>
            Are you sure you want to cancel this trade?
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
  
  export default OrderBoard;
