// ReceiptForm.tsx

import React, { useState, useCallback, useMemo, useEffect } from 'react';
import {
    Box, Button, TextField, Grid, Typography, Paper, IconButton, Stack, Divider, MenuItem
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import {
    ReceiptImportDto, MetadataDto, PaymentDto, ItemDto, TaxDto,
    TaxSectionDto, UnitValueDto, CardDto, CouponDto
} from './interfaces'; // Import types from interfaces.ts

// --- Default Structure Constructors ---

const newMetadata = (): MetadataDto => ({
    countryCode: 'DE',
    terminalNumber: '',
    storeNumber: '',
    serialNumber: '',
});

const newItem = (): ItemDto => ({
    code: '',
    title: '',
    quantity: 1.0,
    price: 0.0,
    categoryCode: '',
    unitValue: { unitCode: 'UNIT', value: 1.0 },
});

const newTax = (): TaxDto => ({
    type: 'VAT',
    taxUnit: { value: 19.0, unit: 'PERCENTAGE' },
    taxableAmount: 0.0,
    amount: 0.0,
    itemCategory: 'STANDARD',
});

const newPayment = (): PaymentDto => ({
    amount: 0.0,
    mode: 'CARD',
    currency: 'EUR',
    card: { type: 'VISA', number: '' },
    coupon: null,
});

// --- Initial State for the entire form ---

const initialReceiptState: ReceiptImportDto = {
    // Mandatory Fields
    identifier: '',
    marketPlaceCode: '',
    storeCode: '',
    timestamp: new Date().toISOString().slice(0, 16), // YYYY-MM-DDTHH:MM format
    total: 0.0,
    currency: 'EUR', // Defaulted as requested

    // Nested Objects/Arrays
    metadata: newMetadata(),
    payments: [newPayment()],
    tax: { total: 0.0, taxes: [newTax()] },
    items: [newItem()],
};

// ====================================================================
// Reusable Sub-Components
// ====================================================================

// --- Item Component ---
interface ItemFormProps {
    item: ItemDto;
    index: number;
    onChange: (index: number, newEntry: ItemDto) => void;
    onRemove: (index: number) => void;
}

const ItemForm: React.FC<ItemFormProps> = React.memo(({ item, index, onChange, onRemove }) => (
    <Paper elevation={1} sx={{ p: 2, mb: 2 }}>
        <Stack direction="row" justifyContent="space-between" alignItems="center" mb={1}>
            <Typography variant="subtitle1">Item #{index + 1}</Typography>
            <IconButton onClick={() => onRemove(index)} size="small" color="error"><DeleteIcon fontSize="small" /></IconButton>
        </Stack>
        <Grid container spacing={2}>
            <Grid item xs={6} md={4}><TextField fullWidth label="Title" name="title" value={item.title}
                onChange={(e) => onChange(index, { ...item, title: e.target.value })} /></Grid>
            <Grid item xs={6} md={2}><TextField fullWidth label="Qty" name="quantity" type="number" value={item.quantity}
                onChange={(e) => onChange(index, { ...item, quantity: parseFloat(e.target.value) || 0 })} /></Grid>
            <Grid item xs={6} md={2}>
                <TextField fullWidth label="Price" name="price" type="number" value={item.price} inputMode="decimal"
                onChange={(e) => onChange(index, { ...item, price: parseFloat(e.target.value) || 0 })} />
            </Grid>
            <Grid item xs={6} md={2}><TextField fullWidth label="Code" name="code" value={item.code}
                onChange={(e) => onChange(index, { ...item, code: e.target.value })} /></Grid>
            <Grid item xs={12} md={2}><TextField fullWidth label="Category" name="categoryCode" value={item.categoryCode}
                onChange={(e) => onChange(index, { ...item, categoryCode: e.target.value })} /></Grid>
        </Grid>
    </Paper>
));

// --- Payment Component ---
interface PaymentFormProps {
    payment: PaymentDto;
    index: number;
    onChange: (index: number, newEntry: PaymentDto) => void;
    onRemove: (index: number) => void;
}

const PaymentForm: React.FC<PaymentFormProps> = React.memo(({ payment, index, onChange, onRemove }) => {
    const handleCardChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (payment.card) {
            onChange(index, {
                ...payment,
                card: { ...payment.card, [e.target.name]: e.target.value },
            });
        }
    };

    const handleCouponChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (payment.coupon) {
            onChange(index, {
                ...payment,
                coupon: { ...payment.coupon, [e.target.name]: e.target.value },
            });
        }
    };

    const handleModeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const mode = e.target.value as PaymentDto['mode'];
        let updatedPayment: PaymentDto = { ...payment, mode };

        // Reset card/coupon based on mode for cleaner data (respecting NON_NULL)
        if (mode === 'CARD') {
            updatedPayment.card = { type: 'VISA', number: '' };
            updatedPayment.coupon = null;
        } else if (mode === 'COUPON') {
            updatedPayment.card = null;
            updatedPayment.coupon = { type: 'DISCOUNT', identifier: '' };
        } else {
            updatedPayment.card = null;
            updatedPayment.coupon = null;
        }

        onChange(index, updatedPayment);
    };

    return (
        <Paper elevation={1} sx={{ p: 2, mb: 2 }}>
            <Stack direction="row" justifyContent="space-between" alignItems="center" mb={1}>
                <Typography variant="subtitle1">Payment #{index + 1}</Typography>
                <IconButton onClick={() => onRemove(index)} size="small" color="error"><DeleteIcon fontSize="small" /></IconButton>
            </Stack>
            <Grid container spacing={2}>
                <Grid item xs={6} md={3}>
                    <TextField fullWidth select label="Mode" name="mode" value={payment.mode} onChange={handleModeChange}>
                        <MenuItem value="CARD">Card</MenuItem>
                        <MenuItem value="CASH">Cash</MenuItem>
                        <MenuItem value="COUPON">Coupon</MenuItem>
                        <MenuItem value="OTHER">Other</MenuItem>
                    </TextField>
                </Grid>
                <Grid item xs={6} md={3}><TextField fullWidth label="Amount" name="amount" type="number" value={payment.amount}
                    onChange={(e) => onChange(index, { ...payment, amount: parseFloat(e.target.value) || 0 })} /></Grid>
                <Grid item xs={12} md={3}><TextField fullWidth label="Currency" name="currency" value={payment.currency}
                    onChange={(e) => onChange(index, { ...payment, currency: e.target.value })} /></Grid>
                {payment.mode === 'CARD' && payment.card && (
                    <>
                        <Grid item xs={6} md={3}><TextField fullWidth label="Card Type" name="type" value={payment.card.type} onChange={handleCardChange} /></Grid>
                        <Grid item xs={6} md={3}><TextField fullWidth label="Card Number" name="number" value={payment.card.number} onChange={handleCardChange} /></Grid>
                    </>
                )}
                {payment.mode === 'COUPON' && payment.coupon && (
                    <>
                        <Grid item xs={6} md={3}><TextField fullWidth label="Coupon Type" name="type" value={payment.coupon.type} onChange={handleCouponChange} /></Grid>
                        <Grid item xs={6} md={3}><TextField fullWidth label="Coupon Identifier" name="identifier" value={payment.coupon.identifier} onChange={handleCouponChange} /></Grid>
                    </>
                )}
            </Grid>
        </Paper>
    );
});

// --- Tax Component ---
interface TaxFormProps {
    tax: TaxDto;
    index: number;
    onChange: (index: number, newEntry: TaxDto) => void;
    onRemove: (index: number) => void;
}

const TaxForm: React.FC<TaxFormProps> = React.memo(({ tax, index, onChange, onRemove }) => {
    const handleTaxUnitChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        onChange(index, {
            ...tax,
            taxUnit: { ...tax.taxUnit, [e.target.name]: e.target.value },
        });
    };
    return (
        <Paper elevation={1} sx={{ p: 2, mb: 2 }}>
            <Stack direction="row" justifyContent="space-between" alignItems="center" mb={1}>
                <Typography variant="subtitle1">Tax Entry #{index + 1}</Typography>
                <IconButton onClick={() => onRemove(index)} size="small" color="error"><DeleteIcon fontSize="small" /></IconButton>
            </Stack>
            <Grid container spacing={2}>
                <Grid item xs={6} md={2}><TextField fullWidth label="Type" name="type" value={tax.type}
                    onChange={(e) => onChange(index, { ...tax, type: e.target.value })} /></Grid>
                <Grid item xs={6} md={2}><TextField fullWidth label="Rate (%)" name="value" type="number" value={tax.taxUnit.value}
                    onChange={handleTaxUnitChange} /></Grid>
                <Grid item xs={6} md={3}><TextField fullWidth label="Taxable Amount" name="taxableAmount" type="number" value={tax.taxableAmount}
                    onChange={(e) => onChange(index, { ...tax, taxableAmount: parseFloat(e.target.value) || 0 })} /></Grid>
                <Grid item xs={6} md={3}><TextField fullWidth label="Tax Amount" name="amount" type="number" value={tax.amount}
                    onChange={(e) => onChange(index, { ...tax, amount: parseFloat(e.target.value) || 0 })} /></Grid>
                <Grid item xs={12} md={2}><TextField fullWidth label="Category" name="itemCategory" value={tax.itemCategory}
                    onChange={(e) => onChange(index, { ...tax, itemCategory: e.target.value })} /></Grid>
            </Grid>
        </Paper>
    );
});


// ====================================================================
// Main Receipt Form Component
// ====================================================================

const ReceiptForm: React.FC = () => {
    const [receipt, setReceipt] = useState<ReceiptImportDto>(initialReceiptState);

    // --- General Change Handler ---
    const handleInputChange = useCallback((e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value, type } = e.target;
        setReceipt(prev => ({
            ...prev,
            [name]: type === 'number' ? parseFloat(value) || 0 : value,
        }));
    }, []);

    const handleMetadataChange = useCallback((e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setReceipt(prev => ({
            ...prev,
            metadata: {
                ...prev.metadata,
                [name]: value,
            },
        }));
    }, []);

    // --- List Handlers Factory ---
    const createListHandlers = <T,>(key: keyof ReceiptImportDto, newFn: () => T) => {
        const handleAdd = () => {
            setReceipt(prev => ({
                ...prev,
                [key]: [...(prev[key] as T[]), newFn()],
            }));
        };

        const handleRemove = (index: number) => {
            setReceipt(prev => ({
                ...prev,
                [key]: (prev[key] as T[]).filter((_, i) => i !== index),
            }));
        };

        const handleChange = (index: number, newEntry: T) => {
            setReceipt(prev => ({
                ...prev,
                [key]: (prev[key] as T[]).map((item, i) => (i === index ? newEntry : item)),
            }));
        };

        return { handleAdd, handleRemove, handleChange };
    };

    const itemHandlers = createListHandlers<ItemDto>('items', newItem);
    const paymentHandlers = createListHandlers<PaymentDto>('payments', newPayment);

    // Special handler for tax array nested under the 'tax' object
    const handleAddTax = () => {
        setReceipt(prev => ({
            ...prev,
            tax: { ...prev.tax, taxes: [...prev.tax.taxes, newTax()] },
        }));
    };
    const handleRemoveTax = (index: number) => {
        setReceipt(prev => ({
            ...prev,
            tax: { ...prev.tax, taxes: prev.tax.taxes.filter((_, i) => i !== index) },
        }));
    };
    const handleChangeTax = (index: number, newEntry: TaxDto) => {
        setReceipt(prev => ({
            ...prev,
            tax: { ...prev.tax, taxes: prev.tax.taxes.map((item, i) => (i === index ? newEntry : item)) },
        }));
    };

    // --- Auto-Calculation of Total (Items + Tax - Coupons) ---
    const calculateTotal = useMemo(() => {
        // Sum of (Item Quantity * Price)
        const itemsSubtotal = receipt.items.reduce((sum, item) => sum + (item.quantity * item.price), 0);
        // Sum of all payment modes marked as COUPON
        const couponTotal = receipt.payments
            .filter(p => p.mode === 'COUPON')
            .reduce((sum, p) => sum + p.amount, 0);

        // Sum of Tax Amounts from the 'taxes' array
        const taxTotal = receipt.tax.taxes.reduce((sum, tax) => sum + tax.amount, 0);

        return (itemsSubtotal - couponTotal + taxTotal);
    }, [receipt.items, receipt.payments, receipt.tax.taxes]);

    // Sync calculated total back to state
    useEffect(() => {
        const newTotal = parseFloat(calculateTotal.toFixed(2));
        if (receipt.total !== newTotal) {
            setReceipt(prev => ({ ...prev, total: newTotal }));
        }
        // Simple sync for tax.total
        const newTaxTotal = parseFloat(receipt.tax.taxes.reduce((sum, tax) => sum + tax.amount, 0).toFixed(2));
        if (receipt.tax.total !== newTaxTotal) {
            setReceipt(prev => ({ ...prev, tax: { ...prev.tax, total: newTaxTotal } }));
        }
    }, [calculateTotal, receipt.total, receipt.tax.taxes]);


    // --- Submission Handler ---
    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        console.log('Final Receipt JSON:', JSON.stringify(receipt, null, 2));
        alert('Receipt Data Submitted! Check console for JSON output.');
        // API Submission Logic here
    };

    return (
        <Box component="form" onSubmit={handleSubmit} sx={{ maxWidth: 1200, margin: 'auto', p: 3 }}>
            <Typography variant="h4" gutterBottom>
                New Receipt Import
            </Typography>
            <Paper sx={{ p: 3, mb: 3 }}>
                <Typography variant="h6" gutterBottom color="primary">
                    Mandatory Receipt Details
                </Typography>
                <Grid container spacing={2}>
                    {/* Mandatory Fields */}
                    <Grid item xs={12} sm={6} md={3}><TextField fullWidth required label="Identifier" name="identifier" value={receipt.identifier} onChange={handleInputChange} /></Grid>
                    <Grid item xs={12} sm={6} md={3}><TextField fullWidth required label="Market Place Code" name="marketPlaceCode" value={receipt.marketPlaceCode} onChange={handleInputChange} /></Grid>
                    <Grid item xs={12} sm={6} md={3}><TextField fullWidth required label="Store Code" name="storeCode" value={receipt.storeCode} onChange={handleInputChange} /></Grid>
                    <Grid item xs={12} sm={6} md={3}>
                        <TextField fullWidth required label="Timestamp" name="timestamp" type="datetime-local"
                            InputLabelProps={{ shrink: true }} value={receipt.timestamp} onChange={handleInputChange}
                        />
                    </Grid>
                    <Grid item xs={6} md={3}>
                        <TextField fullWidth required label="Total" name="total" type="number" value={receipt.total.toFixed(2)}
                            onChange={handleInputChange} disabled helperText="Calculated from Items + Tax - Coupons"
                        />
                    </Grid>
                    <Grid item xs={6} md={3}><TextField fullWidth required label="Currency" name="currency" value={receipt.currency} onChange={handleInputChange} /></Grid>
                </Grid>
            </Paper>

            <Paper sx={{ p: 3, mb: 3 }}>
                <Typography variant="h6" gutterBottom>Metadata</Typography>
                <Grid container spacing={2}>
                    {Object.keys(receipt.metadata as MetadataDto).map(key => (
                        <Grid item xs={6} md={3} key={key}><TextField fullWidth label={key.charAt(0).toUpperCase() + key.slice(1).replace(/([A-Z])/g, ' $1')}
                            name={key} value={receipt.metadata[key as keyof MetadataDto]} onChange={handleMetadataChange}
                        /></Grid>
                    ))}
                </Grid>
            </Paper>

            {/* --- Items Section --- */}
            <Box mb={3}><Typography variant="h5" sx={{ mb: 2 }}>Items 🛍️</Typography><Divider sx={{ mb: 2 }} />
                {receipt.items.map((item, index) => (
                    <ItemForm key={index} item={item} index={index} onChange={itemHandlers.handleChange} onRemove={itemHandlers.handleRemove} />
                ))}
                <Button startIcon={<AddIcon />} onClick={itemHandlers.handleAdd} variant="outlined" color="primary">Add Item</Button>
            </Box>

            {/* --- Tax Section --- */}
            <Box mb={3}><Typography variant="h5" sx={{ mb: 2 }}>Tax Details 🧾</Typography><Divider sx={{ mb: 2 }} />
                <TextField label="Total Tax Amount" type="number" value={receipt.tax.total.toFixed(2)} disabled sx={{ mb: 2 }} helperText="Sum of Tax Amounts below" />
                {receipt.tax.taxes.map((tax, index) => (
                    <TaxForm key={index} tax={tax} index={index} onChange={handleChangeTax} onRemove={handleRemoveTax} />
                ))}
                <Button startIcon={<AddIcon />} onClick={handleAddTax} variant="outlined" color="secondary">Add Tax Entry</Button>
            </Box>

            {/* --- Payments Section --- */}
            <Box mb={3}><Typography variant="h5" sx={{ mb: 2 }}>Payments 💳</Typography><Divider sx={{ mb: 2 }} />
                {receipt.payments.map((payment, index) => (
                    <PaymentForm key={index} payment={payment} index={index} onChange={paymentHandlers.handleChange} onRemove={paymentHandlers.handleRemove} />
                ))}
                <Button startIcon={<AddIcon />} onClick={paymentHandlers.handleAdd} variant="outlined" color="primary">Add Payment</Button>
            </Box>

            {/* --- Submit Button --- */}
            <Box textAlign="right" pt={2}><Button type="submit" variant="contained" size="large" sx={{ minWidth: 200 }}>Submit Receipt</Button></Box>
        </Box>
    );
};

export default ReceiptForm;
