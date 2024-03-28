import { Link, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import '../styles/Header.css';
import HeaderNavigation from './HeaderNavigation';

const Header = (props) => {

    const [bearer, setBearer] =useState()

    const navigate = useNavigate()

    useEffect(() => {
      if(bearer == null && localStorage.getItem('bearer') !== null )
      {
        setBearer(localStorage.getItem('bearer'));
      }
    });

    const logout = () => {
      localStorage.clear()
        setBearer()
        navigate("/")
    }

    return (
      <div>
        <nav className="header">
          {bearer ? (
            <div>
              <div className="trade-hover">
                <span onClick={() => navigate('/userhome')}>Velocity Trade</span>
                <div className="nav hidden">
                  <HeaderNavigation />
                </div>
              </div>
              <span className="rounded-button" style={{ float: 'right' }} onClick={logout}>
                Log Out
              </span>
            </div>
          ) : (
            <div>
              <div className="trade-hover">
                <span onClick={() => navigate('/')}>Velocity Trade</span>
                <div className="nav hidden">
                </div>
              </div>
              <span className="rounded-button" style={{ float: 'right' }} onClick={() => navigate('/login')}>
                Log In
              </span>
            </div>
          )}
        </nav>
      </div>
    );
}
export default Header;
