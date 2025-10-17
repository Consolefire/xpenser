// MinimalReceiptForm.tsx

import React, { useState, useCallback } from 'react';
import {
  Box,
  Button,
  TextField,
  Grid,
  Typography,
  Paper,
} from '@mui/material';

// Import the API function from the new module
import { postMinimalReceipt } from '../../../clients/receipt-import/receipt-import-client';
import { ReceiptRequestDto } from '../../types/ReceiptTypes'; 

// --- Initial State for the minimal mandatory fields ---
// ... (initialMinimalReceiptState remains the same) ...
const initialMinimalReceiptState: ReceiptRequestDto = {
  identifier: '',
  marketPlaceCode: '',
  storeCode: '',
  timestamp: new Date().toISOString().slice(0, 16), 
  total: 0.0,
  currency: 'EUR', 
  
  metadata: { countryCode: '', terminalNumber: '', storeNumber: '', serialNumber: '' },
  payments: [],
  tax: { total: 0.0, taxes: [] },
  items: [],
};


const MinimalReceiptForm: React.FC = () => {
  const [receipt, setReceipt] = useState<ReceiptRequestDto>(initialMinimalReceiptState);

  // --- General Change Handler ---
  const handleInputChange = useCallback((e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setReceipt(prev => ({
      ...prev,
      [name]: name === 'total' ? parseFloat(value) || 0 : value,
    }));
  }, []);

  // --- Submission Handler (SIMPLIFIED) ---
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Construct the minimal JSON payload, matching the module's expected interface
    const payload = {
      identifier: receipt.identifier,
      marketPlaceCode: receipt.marketPlaceCode,
      storeCode: receipt.storeCode,
      timestamp: receipt.timestamp,
      total: receipt.total,
      currency: receipt.currency,
    };
    
    try {
      // **CALL THE EXTERNAL MODULE FUNCTION**
      const result = await postMinimalReceipt(payload);

      alert(`Receipt submitted successfully! ID: ${result.id || 'N/A'}`);
      // Optionally, reset the form here
      // setReceipt(initialMinimalReceiptState); 

    } catch (error: any) {
      // Handle errors thrown from the module
      alert(`Submission failed: ${error.message || 'Unknown Error'}`);
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ maxWidth: 600, margin: 'auto', p: 3 }}>
      <Typography variant="h5" gutterBottom>
        Minimal Receipt Entry (Mandatory Fields Only)
      </Typography>
      <Paper sx={{ p: 3, mb: 3 }}>
        <Grid container spacing={3}>
          {/* Form fields remain identical */}
          <Grid item xs={12}><TextField fullWidth required label="Identifier" name="identifier" value={receipt.identifier} onChange={handleInputChange} /></Grid>
          <Grid item xs={12} sm={6}><TextField fullWidth required label="Market Place Code" name="marketPlaceCode" value={receipt.marketPlaceCode} onChange={handleInputChange} /></Grid>
          <Grid item xs={12} sm={6}><TextField fullWidth required label="Store Code" name="storeCode" value={receipt.storeCode} onChange={handleInputChange} /></Grid>
          <Grid item xs={12} sm={6}>
            <TextField fullWidth required label="Timestamp" name="timestamp" type="datetime-local"
              InputLabelProps={{ shrink: true }} value={receipt.timestamp} onChange={handleInputChange}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField fullWidth required label="Total Amount" name="total" type="text" 
              inputProps={{ inputMode: 'decimal' }} value={receipt.total} onChange={handleInputChange}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField fullWidth required label="Currency" name="currency" value={receipt.currency} onChange={handleInputChange} helperText="Defaulted to EUR" />
          </Grid>
        </Grid>
      </Paper>

      <Box textAlign="center" pt={2}>
        <Button type="submit" variant="contained" size="large">
          Submit Minimal Receipt
        </Button>
      </Box>
    </Box>
  );
};

export default MinimalReceiptForm;