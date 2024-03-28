import { Link, useNavigate } from "react-router-dom";
import '../styles/NavigationBar.css';

const HeaderNavigation = () => {

    const linkStyle = {
        textDecoration: "none",
        color: "#FFF",
        fontSize: 15,
        fontFamily: "system-ui",
      };
      
      const linkHoverStyle = {
        textDecoration: "underline",
        color: "#FFF",
        fontSize: 15,
        fontFamily: "system-ui",
      };

    return(
        <div style={{ backgroundColor: 'white' }}>
        <nav style={{ float: 'left'}}>
         <ul style={{ listStyleType: "none", padding: 0, marginLeft: '20px' }}>
         <li style={{ display: "inline-block", marginRight: "25px" }}>
        <Link to='/personaldetails' style={linkStyle} onMouseOver={(e) => e.currentTarget.style.textDecoration = 'underline'} onMouseOut={(e) => e.currentTarget.style.textDecoration = 'none'}>Personal Details</Link>
        </li>
        <li style={{ display: "inline-block", marginRight: "25px" }}>
        <Link to='/transactionhistory' style={linkStyle} onMouseOver={(e) => e.currentTarget.style.textDecoration = 'underline'} onMouseOut={(e) => e.currentTarget.style.textDecoration = 'none'}>Transaction History</Link>
        </li>
        <li style={{ display: "inline-block", marginRight: "25px" }}>
        <Link to='/depositwithdrawal' style={linkStyle} onMouseOver={(e) => e.currentTarget.style.textDecoration = 'underline'} onMouseOut={(e) => e.currentTarget.style.textDecoration = 'none'}>Deposit/Withdrawal</Link>
        </li>
        <li style={{ display: "inline-block", marginRight: "25px" }}>
        <Link to='/maintrading' style={linkStyle} onMouseOver={(e) => e.currentTarget.style.textDecoration = 'underline'} onMouseOut={(e) => e.currentTarget.style.textDecoration = 'none'}>Main Trading Page</Link>
        </li>
        <li style={{ display: "inline-block", marginRight: "25px" }}>
        <Link to='/outstandingtradesboard' style={linkStyle} onMouseOver={(e) => e.currentTarget.style.textDecoration = 'underline'} onMouseOut={(e) => e.currentTarget.style.textDecoration = 'none'}>Outstanding Trades Board</Link>
        </li>
    </ul>
</nav>
</div>
    )
}
export default HeaderNavigation;