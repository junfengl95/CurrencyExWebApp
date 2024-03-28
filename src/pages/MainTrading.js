import { Container, Grid, Typography, Box, Button } from '@mui/material';
import ConfirmationDialog from '../components/ConfirmationDialog';
import { format } from 'date-fns';
import InputAmount from '../components/InputAmount';
import SelectCountry from '../components/SelectCountry';
import SwitchCurrency from '../components/SwitchCurrency';
import { CurrencyContext } from '../context/CurrencyContext'
import { useContext, useEffect, useState } from 'react';
import axios from 'axios';
import TradeType from '../components/TradeType';
import LimitExchangeRate from '../components/LimitExchangeRate';
import '../styles/Box.css'
import TradeExpiryDate from '../components/TradeExpiryDate';
import ForwardDate from '../components/ForwardDate';
import { useNavigate } from "react-router-dom"
import Info from '@mui/icons-material/Info';
import '../styles/MainTrading.css';
import TradingHelp from '../components/TradingHelp';

const MainTrading = () => {
    const [userpersonaldetails, setPersonalDetails] = useState();

    // Setting up confirmation Dialog
    const [confirmationVisible, setConfirmationVisible] = useState(false);

    const showConfirmationDialog = () => {
        setConfirmationVisible(true);
    };

    const closeConfirmationDialog = () => {
        setConfirmationVisible(false);
    };

    // Setting up Need help Dialog
    const [needHelpVisible, setNeedHelpVisible] = useState(false);

    const handleShowDialog = () => {
        setNeedHelpVisible(true);
    };

    const handleCloseDialog = () => {
        setNeedHelpVisible(false);
    };

    const {
        fromCurrency,
        setFromCurrency,
        toCurrency,
        setToCurrency,
        firstAmount,
    } = useContext(CurrencyContext);



    const [tradeType, setTradeType] = useState("market");
    const [forwardDate, setForwardDate] = useState(() => {
        const defaultDate = new Date();
        defaultDate.setHours(17, 0, 0, 0);
        return defaultDate;
    });
    
    const [expiryDate, setExpiryDate] = useState(() => {
        const defaultDate = new Date();
        defaultDate.setHours(17, 0, 0, 0);
        return defaultDate;
    });
    const [executionDate, setExecutionDate] = useState(null);

    const [exchangeRate, setExchangeRate] = useState(0);
    const [limitRateEnabled, setLimitRateEnabled] = useState(false);
    const [forwardTradeEnabled, setForwardTradeEnabled] = useState(false);
    const [resultCurrency, setResultCurrency] = useState(0);
    const [defaultExchangeRate, setDefaultExchangeRate] = useState(0);
    const [tradeStatus, setTradeStatus] = useState("pending")

    const codeFromCurrency = fromCurrency.split(" ")[0];
    const codeToCurrency = toCurrency.split(" ")[0];
    const [amountTo, setAmountTo] = useState(0);

    const [wallet, setWallet] = useState([]);
    const [userId, setUserId] = useState();
    const [searchTerm, setSearchTerm] = useState('');

    const requestOptions = {
        headers: {
            Authorization: "Bearer " + localStorage.getItem('bearer')
        }
    }

    const navigate = useNavigate()

    useEffect(() => {
        if (localStorage.getItem("bearer") !== null && localStorage.getItem("bearer") !== "") {
        } else {
            localStorage.setItem("previousPage", "MainTrading");
            navigate('/login');
        }
    }, []);

    useEffect(() => {
        getUserDetails();
        getAllTrades();
    }, []);

    const getUserDetails = async () => {
        try {
            const response = await axios.get(
                'http://localhost:8080/users/' + localStorage.getItem("userID"),
                requestOptions
            );
            setPersonalDetails(response.data);
        } catch (error) {
            console.error('Error getting user profile:', error);
        }

    }

    useEffect(() => {
        setUserId(localStorage.getItem("userID"));
    }, []);

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


    // State to store the debounced amount
    const [debouncedAmount, setDebouncedAmount] = useState(firstAmount);

    // Update debouncedAmount when firstAmount changes, but with a delay
    useEffect(() => {
        const delay = 3000; // Set your desired delay in milliseconds
        const timeoutId = setTimeout(() => {
            setDebouncedAmount(firstAmount);
        }, delay);

        // Cleanup function to clear the timeout on unmount or when firstAmount changes
        return () => clearTimeout(timeoutId);
    }, [firstAmount]);

    // For reading from API
    useEffect(() => {
        if (debouncedAmount) {
            const fetchData = async () => {
                try {
                    const response = await axios("https://api.freecurrencyapi.com/v1/latest", {
                        params: {
                            apikey: "fca_live_hovlu5gaOUPK0qsqmPPvpd46IvmJ29XYnKbpTZt2",
                            base_currency: codeFromCurrency,
                            currencies: codeToCurrency
                        }
                    });
                    console.log("API response:", response);
                    const responseData = await response.data;
                    setResultCurrency(responseData.data[codeToCurrency]);
                    setDefaultExchangeRate(responseData.data[codeToCurrency]);
                } catch (error) {
                    console.log('Error fetching data', error);
                }
            };
            console.log("Fetching data for currencies:", codeFromCurrency, codeToCurrency);
            fetchData();

            // Set up the interval to call fetchData every 15 minutes
            const intervalId = setInterval(fetchData, 15 * 60 * 1000); // 15 minutes

            // Clean up the interval on unmount
            return () => clearInterval(intervalId);

        }

    }, [debouncedAmount, codeFromCurrency, codeToCurrency]);

    // Update limitRate when resultCurrency changes
    useEffect(() => {
        if (resultCurrency !== undefined) {
            // Only update the exchange rate when not in "market" trade
            if (!limitRateEnabled) {
                setExchangeRate(resultCurrency);
            } else {
                // Reset to default when switching to "market" trade
                setExchangeRate(defaultExchangeRate);
            }
        }

    }, [resultCurrency, limitRateEnabled, defaultExchangeRate]);

    // Send to api
    const handleSubmit = async (event) => {

        // Check if firstAmount is negative
        if (firstAmount < 0) {
            alert(`Please enter a valid trade amount. The value ${firstAmount} is not valid value`);
            return;
        }

        // Check if the FromAmount in the user's wallet is sufficient
        const fromCurrencyCode = fromCurrency.split(" ")[0];
        const fromCurrencyAmount = wallet[fromCurrencyCode] || 0;

        if (fromCurrencyAmount < firstAmount) {
            console.error(`Insufficient ${fromCurrency} to exchange`);
            alert(`Insufficient ${fromCurrency} to exchange. Please add funds or adjust the trade amount.`);
            return
        }

        setExecutionDate(new Date());

        showConfirmationDialog();
    }

    useEffect(() => {
        if (!limitRateEnabled) {
            setAmountTo((resultCurrency * firstAmount).toFixed(2));
        } else {
            setAmountTo((exchangeRate * firstAmount).toFixed(2));
        }
    }, [limitRateEnabled, resultCurrency, firstAmount, exchangeRate]);

    const handleConfirm = async () => {

        closeConfirmationDialog();

        const formattedExpiryDate = format(expiryDate, 'yyyy-MM-dd HH:mm:ss');
        const formattedForwardDate = forwardDate ? format(forwardDate, 'yyyy-MM-dd HH:mm:ss') : null;

        const endpoint = `http://localhost:8080/api/v1/trades/save`

        const tradeData = {
            currencyTo: toCurrency,
            currencyFrom: fromCurrency,
            amountFrom: debouncedAmount,
            amountTo: amountTo,
            orderType: tradeType,
            expiryDate: formattedExpiryDate,
            executionDate: executionDate,
            price: exchangeRate,
            user: userpersonaldetails,
            forwardDate: formattedForwardDate,
            tradeStatus: tradeStatus

        };

        console.log("Data before sending:", {
            toCurrency,
            fromCurrency,
            firstAmount,
            amountTo,
            tradeType,
            expiryDate,
            executionDate,
            forwardDate,
            exchangeRate,
            userpersonaldetails,
            tradeStatus
        });

        axios.post(endpoint, tradeData)
            .then(response => {
                console.log('Trade submitted successfully:', response.data);
                alert(`Trade order successfully created`)
            })
            .catch(error => {
                console.error(`Trade credential error`, error);
            })

        // Wallet update
        const updateWalletAfterTrade = (userId, currencyType, amount) => {

            const walletUpdateEndpoint = `http://localhost:8080/users/${userId}/payForTrade/${currencyType}/${amount}`

            axios.post(walletUpdateEndpoint)
                .then(response => {
                    console.log(`Wallet updated successfully`, response.data);
                })
                .catch(error => {
                    console.error(`Error updating wallet`, error);
                })
        }
    }

    const handleTradeExpiryChange = (date) => {
        console.log(date);
        setExpiryDate(date);
    }

    const handleForwardTradeChange = (date) => {
        setForwardDate(date);
    }

    const handleLimitTradeRate = (e) => {
        setExchangeRate(e);
    }

    // Display all outstanding Trades
    const [orders, setOrders] = useState([]);

    const [filteredOrders, setFilteredOrders] = useState([]);


    const getAllTrades = async () => {
        try {
            const responseID = await axios.get(
                'http://localhost:8080/api/v1/trades/allStatusPending',
            );
            setOrders(responseID.data); 
            setFilteredOrders(responseID.data);
        } catch (error) {
            console.error('Error getting trades:', error);
        }
    }

    const handleRefresh = () => {
        // Implement refresh logic here
        getAllTrades();
        console.log('Refresh order board');
    };

    const handleSearch = () => {
        console.log("searching")

        if (searchTerm === '') {
            setFilteredOrders(orders); // If search term is empty, show all orders
        } else {
            const filtered = orders.filter(order =>
                order.orderType.toLowerCase().includes(searchTerm.toLowerCase())
            );
            setFilteredOrders(filtered);
        }
    };

    useEffect(() => {
        const filterOrders = () => {
            if (searchTerm === '') {
                setFilteredOrders(orders);
            } else {
                const filtered = orders.filter(order =>
                    order.orderType.toLowerCase().includes(searchTerm.toLowerCase())
                );
                setFilteredOrders(filtered);
            }
        };

        filterOrders();
    }, [searchTerm, orders]);




    return (
        <div>
            <Container maxWidth="md" className='box-style' style={{ marginTop: '20px', paddingTop: '30px', boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)', border: '4px solid #0056b3', borderColor: 'black'}}>
                <Grid container alignItems="center" justifyContent="center" spacing={2}>
                    <Grid item>
                        <Typography variant="h2" alignContent="center">Main Trading Page</Typography>
                    </Grid>
                    <Grid item>
                        {/* Use the Info icon to represent the "Need Help" functionality */}
                        <Info onClick={handleShowDialog} style={{ cursor: 'pointer' }} />
                        {needHelpVisible && (
                            <TradingHelp
                                title='Need Help?'
                                message={(
                                    <div>
                                        <p>Market Trade</p>
                                        <p>Buy/Sell at the current market exchange rate</p>
                                        <p>Trades expirys at 17:00 of execution date</p>

                                        <br />
                                        <p> Limit Trade</p>
                                        <p>Buy/Sell at current market</p>
                                        <p>Parameters: Exchange Rate, Expiry Date</p>
                                        <br />
                                        <p>Trade Type: Forward</p>
                                        <p>Description: Buy/Sell at forward Rate and Trade settled on Forward Date</p>
                                        <p>Parameters: Exchange Rate, Expiry Date, Forward Date</p>
                                    </div>
                                )}
                                onCancel={handleCloseDialog}
                                isOpen={needHelpVisible}
                            />
                        )}
                    </Grid>
                </Grid>
                <h2>Trade Type</h2>
                <Grid container spacing={2}>
                    <TradeType value={tradeType} onChange={(value) => {
                        setTradeType(value);
                        setLimitRateEnabled(value === 'limit' || value === 'forward');
                        setForwardTradeEnabled(value === 'forward');
                        // Reset to default exchange rate when switching to "market" trade
                        if (value === "market") {
                            setExchangeRate(defaultExchangeRate);
                        }
                    }} />
                </Grid>
                <h2>Exchange Rate</h2>
                <Grid container spacing={2}>
                    <InputAmount />
                    <SelectCountry value={fromCurrency} setValue={setFromCurrency}
                        label="From" />
                    <SwitchCurrency />
                    <SelectCountry value={toCurrency} setValue={setToCurrency}
                        label="To" />
                </Grid>
                {/* If debouncedAmount is not null */}
                {debouncedAmount !== undefined && (
                    <Box sx={{ textAlign: "left", marginTop: "1rem" }}>
                        <Typography>{debouncedAmount} {fromCurrency} =</Typography>
                        <Typography variant='h5' sx={{ marginTop: "5px", fontWeight: "bold" }}>
                            {limitRateEnabled ? (
                                // Use updated exchange rate for limit trades
                                (exchangeRate * debouncedAmount).toFixed(2)
                            ) : (
                                // Use updated resultCurrency for market trades
                                (resultCurrency * debouncedAmount).toFixed(2)
                            )} {toCurrency}
                        </Typography>
                    </Box>
                )}
                <Box sx={{ marginTop: "2rem", marginBottom: "2rem" }}>
                    <LimitExchangeRate disabled={!limitRateEnabled} onChange={handleLimitTradeRate} value={exchangeRate} />
                </Box>
                <Box sx={{ textAlign: "center", marginTop: "1rem" }}>
                    <label>Trade Expiry Date</label><br></br>
                    <TradeExpiryDate
                        value={expiryDate}
                        disabled={!limitRateEnabled}
                        onChange={handleTradeExpiryChange}
                    />
                </Box>
                <Box sx={{ textAlign: "center", marginTop: "1rem" }}>
                    <label>Forward Date</label><br></br>
                    <ForwardDate disabled={!forwardTradeEnabled} onChange={handleForwardTradeChange} />
                </Box>
                <Box sx={{ textAlign: "center", marginTop: "1rem" }}>
                    <Button variant="contained" onClick={handleSubmit}>
                        Submit
                    </Button>
                </Box>
            </Container>
            {/* Render the confirmation dialog when confirmationVisible is true */}
            {confirmationVisible && (
                <ConfirmationDialog
                    title='Confirm Trade'
                    message={(
                        <div>
                            Are you sure you want to submit this trade?
                            <br />
                            <br />
                            Trade Type: {tradeType}
                            <br />
                            Amount: {firstAmount}
                            <br />
                            From Currency: {fromCurrency}
                            <br />
                            To Currency: {toCurrency}
                            <br />
                            Exchange Rate: {exchangeRate}
                        </div>
                    )}
                    onConfirm={handleConfirm}
                    onCancel={() => {
                        closeConfirmationDialog();
                        console.log('Trade submission cancelled.');
                        alert(`Trade submission cancelled.`)
                    }}
                />
            )}
            <br />

            <div style={{ paddingBottom: '130px', marginTop:'50px' }}>
                 <div className="order-board" style={{ paddingBottom: '120px', backgroundColor:'#f2f2f2'}}>
                        <h2>Outstanding Trades Board</h2>
                        <div className="search-container">
                            <input
                                type="text"
                                placeholder="Search by Order Type"
                                value={searchTerm}
                                onChange={e => setSearchTerm(e.target.value)}
                            />
                            <button onClick={handleSearch} className="search-button">Search</button>
                        </div>
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
                                    <th>Expiry Date</th>
                                    <th>Forward Date</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredOrders.map((order, index) => (
                                    <tr key={index}>
                                        <td>{order.tradeId}</td>
                                        <td>{order.currencyFrom}</td>
                                        <td>{order.currencyTo}</td>
                                        <td>{order.price}</td>
                                        <td>{order.amountFrom}</td>
                                        <td>{order.amountTo}</td>
                                        <td>{order.orderType}</td>
                                        <td>{order.executionDate.slice(0, 10)}</td>
                                        <td>{order.expiryDate}</td>
                                        <td>{order.forwardDate}</td>
                                        <td>{order.tradeStatus}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
            </div>
        </div>
    )
}


export default MainTrading;
