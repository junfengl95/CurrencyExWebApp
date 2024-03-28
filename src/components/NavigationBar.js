import { Link, useNavigate } from "react-router-dom";
import '../styles/NavigationBar.css';


const NavigationBar = () => {

    return(
        <div>
        <nav class="navigation" style={{ float: 'right'}}>
            <ul>
               <li> <Link to='/personaldetails'>Personal Details</Link></li>
                <li><Link to='/transactionhistory'>Transaction History</Link></li>
        <li><Link to='/depositwithdrawal'>Deposit/Withdrawal</Link></li>
        <li><Link to='/maintrading'>Main Trading Page</Link></li>
        <li><Link to='/outstandingtradesboard'>Outstanding Trades Board</Link></li>
    </ul>
</nav>
</div>
    )
}
export default NavigationBar;