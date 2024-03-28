import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormControl from '@mui/material/FormControl';
import FormLabel from '@mui/material/FormLabel';
import { Grid } from '@mui/material';

const TradeType = ({ value, onChange }) => {
    
    const handleChange = (event) => {
        onChange(event.target.value);
    };

    return (
        <Grid item xs={12} md="auto">
            <FormControl>
                <FormLabel id="trade-group"></FormLabel>
                <RadioGroup
                    row
                    aria-labelledby="trade-group"
                    name="controlled-radio-buttons-group"
                    value={value}
                    onChange={handleChange}
                >
                    <FormControlLabel value="market" control={<Radio />} label="Market" />
                    <FormControlLabel value="limit" control={<Radio />} label="Limit" />
                    <FormControlLabel value="forward" control={<Radio />} label="Forward" />
                </RadioGroup>
            </FormControl>
        </Grid>
    )
}


export default TradeType;