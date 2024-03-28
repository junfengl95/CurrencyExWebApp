import React, { useContext } from 'react'
import {  Grid, InputAdornment, TextField } from '@mui/material';
import { CurrencyContext } from '../context/CurrencyContext';

const InputAmount = () => {
    const {firstAmount, setFirstAmount} = useContext(CurrencyContext)

    return (
        // xs is width 12 = 100%
        <Grid item xs={12}> 
        <TextField
        value={firstAmount}
        onChange={e => setFirstAmount(e.target.value)}
        label="Amount"
        fullWidth
        InputProps={{
            type:"number",
            startAdornment: <InputAdornment position="start">$</InputAdornment>
        }}
        />
        </Grid>
    )
}

export default InputAmount;