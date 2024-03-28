import ReactDatePicker from "react-datepicker";
import { useState } from "react";
import 'react-datepicker/dist/react-datepicker.css'
import { addMonths, addDays } from "date-fns";

const ForwardDate = ({ disabled, onChange }) => {

    const [startDate, setStartDate] = useState(new Date());

    // Exclude Sunday (0) and Saturday(6)
    const isWeekday = (date) => {
        const day = date.getDay();
        return day !== 0 && day !== 6;
    }

    const handleDateChange = (date) => {
        // Set the time to midnight for the same date
        const newDate = new Date(date);
        newDate.setHours(17, 0, 0, 0);

        setStartDate(newDate);

        // Call the prop onChange with the updated date
        if (onChange) {
            onChange(newDate);
        }
    };

    const startDateLimit = new Date();
    const endDateLimit = addMonths(startDateLimit, 6);

    const includeDates = [];
    let currentDate = startDateLimit;

    while (currentDate <= endDateLimit){
        if (isWeekday(currentDate)){
            includeDates.push(new Date(currentDate));
        }
        currentDate = addDays(currentDate, 1);
    }

    return (
        <ReactDatePicker
        showIcon
        selected={startDate}
        onChange={handleDateChange}
        disabled ={disabled}
        includeDates={includeDates}
        placeholderText="Set Forward Date"
      />
    )
}


export default ForwardDate;