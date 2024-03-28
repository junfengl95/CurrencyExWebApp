import React, { useEffect, useState} from 'react'

const BASE_URL = 'https://api.exchangeratesapi.io/v1/latest'

function App(){
    // const [currencyOptions, setCurrencies] = useState([])
    
    

    useEffect(() =>{
        fetch(BASE_URL)
        .then(res=> res.json())
        .then(data => console.log(data))
    }, [])
}

export default function CurrencyRow(){
    return(
        <div>
            <input type="number" className='input'/>
            <select>
                <option value="Hi">Hi </option>
            </select>
        </div>
    )
}
