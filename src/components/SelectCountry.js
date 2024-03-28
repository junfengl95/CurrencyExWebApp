import React from 'react'
import { Grid, Autocomplete, TextField, Skeleton } from '@mui/material';
import useAxios from "..//hooks/useAxios"
import { useState, useEffect } from 'react';

const SelectCountry = (props) => {

    const { value, setValue, label } = props;
    const [data, loaded, error] = useAxios("https://restcountries.com/v3.1/all")
    const [exchangeRates, setExchangeRates] = useState(null);

    useEffect(() => {
        // Fetch exchange rates data
        fetch('/freeCurrencyRate.json')
            .then((res) => res.json())
            .then((data) => {
                setExchangeRates(data.data);
            })
            .catch((error) => {
                console.error('Error fetching exchange rates:', error);
            });
    }, []); 

    if (loaded){
        return (
            <Grid item xs={12} md={3}>
                <Skeleton variant="rounded" height={60} />
            </Grid>
        )
    };

    if (error){
        return "Something went wrong!"
    };

    if (!exchangeRates) {
        // Waiting for exchangeRates to be set
        return null;
    };

    const dataFilter = data.filter(item => "currencies" in item);
    const dataCountries = dataFilter.map(item => {
        // no need flag = item.flag
        return `${Object.keys(item.currencies)[0]}`
    });

    const filteredCountries = [...new Set(dataCountries)].filter(item => {
        const currencyCode = item.split(' - ')[0];
        return currencyCode in exchangeRates;
    });



    return (
        <Grid item xs={12} md={3}>
            <Autocomplete
                value={value}
                disableClearable
                onChange={(event, newValue) => {
                    setValue(newValue);
                }}
                options={filteredCountries}
                renderInput={(params) => (
                    <TextField {...params} label={label} />
                )}
            />
        </Grid>
    )
}

export default SelectCountry;