import './App.css';
import { Route, Routes, BrowserRouter } from 'react-router-dom';

import Login from './pages/Login';
import Landing from './pages/Landing';
import UserHome from './pages/UserHome';
import DepositWithdrawal from './pages/DepositWithdrawal';
import MainTrading from './pages/MainTrading';
import PersonalDetails from './pages/PersonalDetails'
import TransactionHistory from './pages/TransactionHistory';
import OutstandingTradesBoard from './pages/OutstandingTradesBoard';
import ErrorPage from './pages/ErrorPage';

import Header from './components/Header';


function App() {
  return (
      <div className="App">
        <Header/>
      <Routes>
        <Route path='/' element={<Landing/>}></Route>
        <Route path='/login' element={<Login/>} ></Route>
        <Route path='/userhome' element={<UserHome/>} ></Route>
        <Route path='/personaldetails' element={<PersonalDetails />} ></Route>
        <Route path='/transactionhistory' element={<TransactionHistory/>} ></Route>
        <Route path='/depositwithdrawal' element={<DepositWithdrawal/>} ></Route>
        <Route path='/maintrading' element={<MainTrading/>} ></Route>
        <Route path='/outstandingtradesboard' element={<OutstandingTradesBoard/>} ></Route>
        <Route path="*" element={<ErrorPage/>} ></Route>
      </Routes>
      </div>)

}

export default App;