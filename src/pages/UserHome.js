import NavigationBar from "../components/NavigationBar";
import { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom"
import {
    Container,
    Typography,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Select,
    MenuItem,
    Box,
  } from "@mui/material";


const UserHome = () => {
    const [userId, setUserId] = useState();
    const [wallet, setWallet] = useState([]);
    const [baseCurrency, setBaseCurrency] = useState('USD');
    const [latestExchangeRates, setLatestExchangeRates] = useState();

    const navigate = useNavigate()

    const requestOptions = {
        headers: {
            Authorization: "Bearer " + localStorage.getItem('bearer')
        }
    }

    // For reading from wallet when ready
    useEffect(() => {
        if (localStorage.getItem("bearer") !== null && localStorage.getItem("bearer") !== "") {
            setUserId(localStorage.getItem("userID"));
        } else {
            localStorage.setItem("previousPage", "UserHome");
            navigate('/login');
        }
    }, []);

    console.log("userID: ", userId)

    useEffect(() => {
        if (userId) {
            fetchWalletDate();
        }
    }, [userId]);

    const fetchWalletDate = async () => {
        try {
            const responseWallet = await axios.get(
                'http://localhost:8080/users/' + userId + '/holdingCurrencies'
            );
            setWallet(responseWallet.data)
        } catch (error) {
            console.error('Error getting wallet data:', error);
        };
    };

    console.log("wallet: ", wallet);


    useEffect(() => {
        if (wallet && userId) {
            fetchExchangeRates();
        }
    }, [wallet, userId])

    const fetchExchangeRates = async () => {
        try {
            const responseAPI = await axios.get("https://api.freecurrencyapi.com/v1/latest", {
                params: {
                    apikey: "fca_live_nxWBCjDPFZIReaFvPVaTSaWQz2G3CGNdg9JOdA4R",
                    base_currency: baseCurrency
                }
            });
            setLatestExchangeRates(responseAPI.data.data);
            sessionStorage.setItem('latestExchangeRates', JSON.stringify(responseAPI.data.data));
        } catch (error) {
            console.error('Error getting wallet data:', error);
        }
    };


    const calculateTotalInBaseCurrency = (wallet, latestExchangeRates, baseCurrency) => {
        if (wallet && latestExchangeRates, baseCurrency) {
            try {
                let totalBaseCurrency = 0;
                Object.keys(wallet).forEach(currency => {
                    if (latestExchangeRates[currency]) {
                        totalBaseCurrency += wallet[currency] / latestExchangeRates[currency]
                    }
                });
                totalBaseCurrency *= latestExchangeRates[baseCurrency];
                return totalBaseCurrency.toFixed(2);
            } catch (error) {
                console.error('Error getting data:', error);
            }
        }

    };

    const totalInBaseCurrency = calculateTotalInBaseCurrency(wallet, latestExchangeRates, baseCurrency);
    console.log(`total amount in ${baseCurrency}: `, totalInBaseCurrency);

    console.log(Object.entries(wallet))
    
      return (
        <div >
          <NavigationBar />
          <Container maxWidth="md" sx={{ mt: 4 }}>
            <Typography variant="h4" align="center" gutterBottom  >
              Portfolio
            </Typography>
            <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column', mt: 6}}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2}}>
                <Typography variant="h6">
                  Total Amount in {baseCurrency}: <span style={{ 
                    fontSize: '2rem', 
                    color: 'tomato', // Example color
                    fontWeight: 'bold', // Make the text bold
                  }}>
                    {totalInBaseCurrency}
                  </span>
                </Typography>
                <Select
                  value={baseCurrency}
                  onChange={(e) => setBaseCurrency(e.target.value)}
                  variant="outlined"
                  sx={{ minWidth: 120 }}
                >
                  <MenuItem value="USD">USD</MenuItem>
                  <MenuItem value="EUR">EUR</MenuItem>
                </Select>
              </Box> 
              <TableContainer component={Paper}>
                <Table sx={{ minWidth: 650 }} aria-label="portfolio table">
                  <TableHead>
                    <TableRow>
                      <TableCell sx={{ fontSize: '1.1rem' }}>Currency</TableCell>
                      <TableCell sx={{ fontSize: '1.1rem' }} align="right">Amount</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {Object.entries(wallet).map(([currency, amount]) => (
                      <TableRow key={currency}>
                        <TableCell component="th" scope="row" sx={{ fontSize: '1.05rem' }}>
                          {currency}
                        </TableCell>
                        <TableCell align="right" sx={{ fontSize: '1.05rem' }}>{amount} </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </Paper>
          </Container>
        </div>
      );
    };
    
    export default UserHome;


    // return (
    //   <div>
    //     <NavigationBar />
    //     <Container maxWidth="md" sx={{ mt: 4, bgcolor: 'transparent' }}>
    //       <Typography variant="h4" align="center" gutterBottom sx={{ bgcolor: 'transparent' }}>
    //         Portfolio
    //       </Typography>
    //       <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column', mt: 6, bgcolor: 'transparent', boxShadow: 'none' ,boxShadow: '0px 8px 24px rgba(0, 0, 0, 0.3)'}}>
    //         <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2, bgcolor: 'transparent' }}>
    //           <Typography variant="h6" sx={{ bgcolor: 'transparent' }}>
    //             Total Amount in {baseCurrency}: {totalInBaseCurrency}
    //           </Typography>
    //           <Select
    //             value={baseCurrency}
    //             onChange={(e) => setBaseCurrency(e.target.value)}
    //             variant="outlined"
    //             sx={{ minWidth: 120, bgcolor: 'transparent' }}
    //           >
    //             <MenuItem value="USD">USD</MenuItem>
    //             <MenuItem value="EUR">EUR</MenuItem>
    //           </Select>
    //         </Box>
    //         <TableContainer component={Paper} sx={{ bgcolor: 'transparent', boxShadow: 'none' }}>
    //           <Table sx={{ minWidth: 650, bgcolor: 'transparent' }} aria-label="portfolio table">
    //             <TableHead>
    //               <TableRow>
    //                 <TableCell sx={{ bgcolor: 'transparent' }}>Currency</TableCell>
    //                 <TableCell align="right" sx={{ bgcolor: 'transparent' }}>Amount</TableCell>
    //               </TableRow>
    //             </TableHead>
    //             <TableBody>
    //               {Object.entries(wallet).map(([currency, amount]) => (
    //                 <TableRow key={currency}>
    //                   <TableCell component="th" scope="row" sx={{ bgcolor: 'transparent' }}>
    //                     {currency}
    //                   </TableCell>
    //                   <TableCell align="right" sx={{ bgcolor: 'transparent' }}>{amount}</TableCell>
    //                 </TableRow>
    //               ))}
    //             </TableBody>
    //           </Table>
    //         </TableContainer>
    //       </Paper>
    //     </Container>
    //   </div>
    // );





