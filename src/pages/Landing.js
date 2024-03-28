import React from 'react';
import '../styles/Box.css';
import { Container } from '@mui/material';
import '../styles/Landing.css';
import tradingImage from './pho.PNG';

const Landing = () => {
  // Inline style for the img tag to give rounded corners
  const imageStyle = {
    borderTopLeftRadius: '500px',
    borderBottomLeftRadius: '500px'// Adjust this value to increase or decrease the roundness
    // Other styles as needed
  };

  return (
    <div className="Landing-background">
      <div className="welcome-text">
        <h2 className='welcome-velocitytrade'>Welcome To <span className="special-velocitytrade">VelocityTrade</span></h2>
        <h2 className="welcome-velocitytrade-des">- A Professional Forex Trading Platform -</h2>
      </div>
      <div>
        <img src={tradingImage} alt="Trading" style={imageStyle} className="landing-image"/>
      </div>
    </div>
  );
};

export default Landing;

  // https://i.pinimg.com/736x/43/36/e1/4336e1aba06b22fcdfc3e51c3aa9e5bd.jpg


  // const Landing = () => {
  //   //Inline style object for the background image
  //   const backgroundStyle = {
  //     backgroundImage: `url(${tradingImage})`,
  //     backgroundSize: 'cover', // or contain depending on what you need
  //     backgroundPosition: 'center',
  //     backgroundRepeat: 'no-repeat',
  //     // Add more styles if needed
  //   }; 
  
  //   return (
  //     // Apply the backgroundStyle object to the outer div using the style attribute
  //     <div className="Landing-background" style={backgroundStyle}>
  //       <div className="welcome-text">
  //         <h2 className='welcome-velocitytrade'>Welcome To <span className="special-velocitytrade">VelocityTrade</span></h2>
  //         <h2 className="welcome-velocitytrade-des">- A Professional Forex Trading Platform -</h2>
  //       </div>
        
  //     </div>
  //   );
  // };
  
  // export default Landing;