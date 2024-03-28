import React from 'react'
import { createContext } from 'react';
import { useState } from 'react';

export const CurrencyContext = createContext();

const CurrencyProvider = ({ children }) => {
    const [fromCurrency, setFromCurrency] = useState("USD")
    const [toCurrency, setToCurrency] = useState("HKD")
    const [firstAmount, setFirstAmount] = useState(0)

    const value = {
        fromCurrency,
        setFromCurrency,
        toCurrency,
        setToCurrency,
        firstAmount,
        setFirstAmount,
    };

    return (
        <CurrencyContext.Provider value={value}>
            {children}
        </CurrencyContext.Provider>
    )
}

export default CurrencyProvider;