import { Grid, TextField } from "@mui/material";
import { useState, useEffect } from 'react'

const LimitExchangeRate = ({ disabled, onChange, value }) => {

    const [limitRate, setLimitRate] = useState(value);

    // Update local state when the value prop changes
    useEffect(() =>{
        setLimitRate(value);
    }, [value])

    const handleInputChange = (e) => {
        const newValue = e.target.value;
        setLimitRate(newValue);
        onChange(newValue);
    };

    return (
        <Grid item xs={12}>
            <TextField
                value={limitRate}
                onChange={handleInputChange}
                label="Exchange Rate"
                fullWidth
                InputProps={{
                    type: "number",
                }}
                disabled={disabled}
            />
        </Grid>
    );
};

export default LimitExchangeRate;