import React from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button } from '@mui/material';

const ConfirmationDialog = ({ title, message, onConfirm, onCancel }) => {
    return (
        <Dialog open={true} onClose={onCancel}>
            <DialogTitle>{title}</DialogTitle>
            <DialogContent>
                <div style={{ textAlign: 'left' }}>
                    {message}
                </div>
            </DialogContent>
            <DialogActions style={{ justifyContent: 'center' }}>
                <Button onClick={onCancel} color="primary" style={{ border: '1px solid #000', borderRadius: '5px' }}>
                    No
                </Button>
                <Button onClick={onConfirm} color="primary" style={{ border: '1px solid #000', borderRadius: '5px' }}>
                    Yes
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default ConfirmationDialog;