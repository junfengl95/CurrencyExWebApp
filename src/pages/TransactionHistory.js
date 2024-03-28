import React, { useState , useEffect} from 'react';
import "../styles/TransactionHistory.css";
import axios from 'axios';
import { useNavigate } from "react-router-dom"

const TradeHistoryPage = () => {

  const [transactions, setTransactions] = useState([]);// Should be an array to map over it later
  const [userID, setUserID] = useState(''); 
  const [userpersonaldetails, setPersonalDetails] = useState()
  const [searchField, setSearchField] = useState('amountFrom'); // default search field

  const requestOptions = {
    headers: {
        Authorization: "Bearer "+ localStorage.getItem('bearer')
    }
 }

 const navigate = useNavigate()

 useEffect(() => {
   if (localStorage.getItem("bearer") !== null && localStorage.getItem("bearer") !== "") {
    getUserDetails();
   } else {
       localStorage.setItem("previousPage", "TransactionHistory");
       navigate('/login');
   }
}, []);


  const getUserDetails = async () =>{
          try {
              const responseID = await axios.get(
                  'http://localhost:8080/users/username/' + localStorage.getItem("username"),
                  requestOptions
              );
              const response = await axios.get(
                  'http://localhost:8080/TransactionHistory/' + responseID.data,
                  requestOptions
              );
              setTransactions(response.data);
          } catch (error) {
              console.error('Error getting user profile:', error);
          }

  }

  const [searchMonth, setSearchMonth] = useState('');
  const [searchFrom, setSearchFrom] = useState('');
  const [filteredTransactions, setFilteredTransactions] = useState([]);
  const [searchPerformed, setSearchPerformed] = useState(false); // New state to track if search was performed

  const handleSearch = (e) => {
    e.preventDefault();
    setSearchPerformed(true);
    const filtered = transactions.filter((transaction) => {
      const transactionMonth = transaction.executionDate.slice(0, 7);
      let match = false;
  
      switch (searchField) {
        case 'amountFrom':
          match = transaction.currencyFrom.toLowerCase().includes(searchFrom.toLowerCase());
          break;
        case 'orderType':
          match = transaction.orderType.toLowerCase().includes(searchFrom.toLowerCase());
          break;
        case 'status':
          match = transaction.status.toLowerCase().includes(searchFrom.toLowerCase());
          break;
        default:
          match = false;
      }
      
      return transactionMonth === searchMonth && match;
    });
  
    setFilteredTransactions(filtered);
  };

  const handleShowAll = () => {
    setSearchMonth('');
    setSearchFrom('');
    setFilteredTransactions(transactions); // Show all transactions
    setSearchPerformed(false);
    console.log(localStorage.getItem("username"));
  };


  return (
    <div className="transaction-history">
      <h2>Transaction History</h2>
      <form onSubmit={handleSearch}>
        <input
          type="month"
          value={searchMonth}
          onChange={(e) => setSearchMonth(e.target.value)}
        />
        <select value={searchField} onChange={(e) => setSearchField(e.target.value)}>
          <option value="amountFrom">Currency From</option>
          <option value="orderType">Order Type</option>
          <option value="status">Status</option>
        </select>
        <input
          type="text"
          placeholder="Search Currency"
          value={searchFrom}
          onChange={(e) => setSearchFrom(e.target.value)}
        />
        <button type="submit">Search</button>
      </form>
      <div className="show-all-container">
        <button onClick={handleShowAll}>Show All</button>
      </div>
      {searchPerformed && filteredTransactions.length === 0 ? (
        <p>No records found for the selected month.</p>
      ) : (
        <table className="transaction-table">
          <thead>
            <tr>
              <th>Transaction ID</th>
              <th>From</th>
              <th>To</th>
              <th>Amount From</th>
              <th>Amount To</th>
              <th>Price</th>
              <th>Order Type</th>
              <th>Execution Date</th>
              <th>Forward Date</th>
              <th>Status</th>
              <th>Matched Trade ID</th>
              <th>Expiry Date</th>
            </tr>
          </thead>
          <tbody>
            {(searchPerformed ? filteredTransactions : transactions)
              .filter(transaction => {
                // Check the searchField and apply the appropriate filter
                return searchField === 'orderType'
                  ? transaction.orderType?.toLowerCase().includes(searchFrom.toLowerCase())
                  : searchField === 'status'
                  ? transaction.status?.toLowerCase().includes(searchFrom.toLowerCase())
                  : transaction.currencyFrom?.toLowerCase().includes(searchFrom.toLowerCase());
              })
              .map((transaction) => (
                <tr key={transaction.transactionHistoryId}>
                  <td>{transaction.transactionHistoryId}</td>
                  <td>{transaction.currencyFrom}</td>
                  <td>{transaction.currencyTo}</td>
                  <td>{transaction.amountFrom}</td>
                  <td>{transaction.amountTo}</td>
                  <td>{transaction.price}</td>
                  <td>{transaction.orderType}</td>
                  <td>{transaction.executionDate.slice(0, 10)}</td>
                  <td>{transaction.forwardDate ? transaction.forwardDate : "N.A."}</td>
                  <td>{transaction.status}</td>
                  <td>{transaction.matchedTradeId === 0 ? 'N.A.' : transaction.matchedTradeId}</td>
                  <td>{transaction.expiryDate}</td>
                </tr>
              ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default TradeHistoryPage;
