import React from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button } from '@mui/material';

const TradingHelp = ({ title, message, onCancel, isOpen }) => {
    return (
        <Dialog open={true} onClose={onCancel}>
            <DialogTitle>{title}</DialogTitle>
            <DialogContent>
                <p>{message}</p>
            </DialogContent>
            <DialogActions style={{ justifyContent: 'center' }}>
                <Button onClick={onCancel} color="primary" style={{ border: '1px solid #000', borderRadius: '5px' }}>
                    Close
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default TradingHelp;