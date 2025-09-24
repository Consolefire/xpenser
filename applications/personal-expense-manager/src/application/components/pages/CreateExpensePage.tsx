import React, { useState } from 'react';
import { Box, Typography, TextField, Button, Grid, MenuItem, Select, InputLabel, FormControl, Divider, Container } from '@mui/material'; // Added Divider

const CreateExpensePage: React.FC = () => {
    const [formData, setFormData] = useState({
        identifier: '546546-8768768k-jhkj', // Default from JSON
        metadata: {
            terminalNumber: '',
            serialNumber: '',
        },
        marketPlaceCode: 'v-markt', // Default from JSON
        storeCode: 'k', // Default from JSON
        timestamp: new Date().toISOString().slice(0, 16), // Default to current time
        total: 0.00,
        currency: 'EUR',
        payment: {
            amount: 0.00,
            mode: 'CASH',
            currency: 'EUR',
            tax: {
                total: 0.00,
                taxes: [ // Initial tax item
                    {
                        type: 'vat',
                        taxUnit: { value: 7.00, unit: 'PERCENT' },
                        taxableAmount: 0.00,
                        amount: 0.00,
                        itemCategory: 'A'
                    },
                ]
            }
        },
        items: [ // Initial item
            {
                code: '',
                title: '',
                quantity: 1,
                price: 0.00,
                categoryCode: 'A',
                unitValue: { unitCode: 'L', value: 0.00 }
            },
        ]
    });

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | { name?: string; value: unknown }>) => {
        const { name, value } = event.target;
        // Handle top-level fields
        setFormData(prev => ({ ...prev, [name as string]: value }));
    };

    const handleNestedInputChange = (field: string, subField: string, value: any) => {
        setFormData(prev => ({
            ...prev,
            [field]: {
                ...(prev as any)[field],
                [subField]: value,
            },
        }));
    };

    const handlePaymentNestedInputChange = (field: string, subField: string, value: any) => {
        setFormData(prev => ({
            ...prev,
            payment: {
                ...prev.payment,
                [field]: {
                    ...(prev.payment as any)[field],
                    [subField]: value,
                },
            },
        }));
    };


    const handleArrayChange = (arrayName: 'taxes' | 'items', index: number, field: string, value: any, nestedField?: string, subNestedField?: string) => {
        setFormData(prev => {
            // Correctly access the array based on arrayName
            const targetArray = arrayName === 'taxes' ? prev.payment.tax.taxes : prev.items;
            const newArray = [...targetArray];

            if (nestedField && subNestedField) {
                // Handle deeply nested fields like taxUnit.value
                (newArray[index] as any)[field][nestedField][subNestedField] = value;
            } else if (nestedField) {
                // Handle nested fields like unitValue.unitCode
                (newArray[index] as any)[field][nestedField] = value;
            } else {
                (newArray[index] as any)[field] = value;
            }

            if (arrayName === 'taxes') {
                return {
                    ...prev,
                    payment: {
                        ...prev.payment,
                        tax: {
                            ...prev.payment.tax,
                            taxes: newArray,
                        },
                    },
                };
            } else { // arrayName === 'items'
                return {
                    ...prev,
                    items: newArray,
                };
            }
        });
    };

    const addArrayItem = (arrayName: 'taxes' | 'items') => {
        setFormData(prev => {
            if (arrayName === 'taxes') {
                const newTaxes = [
                    ...prev.payment.tax.taxes,
                    { type: 'vat', taxUnit: { value: 0.00, unit: 'PERCENT' }, taxableAmount: 0.00, amount: 0.00, itemCategory: 'A' }
                ];
                return {
                    ...prev,
                    payment: {
                        ...prev.payment,
                        tax: {
                            ...prev.payment.tax,
                            taxes: newTaxes,
                        },
                    },
                };
            } else { // arrayName === 'items'
                const newItems = [
                    ...prev.items,
                    { code: '', title: '', quantity: 1, price: 0.00, categoryCode: 'A', unitValue: { unitCode: 'L', value: 0.00 } }
                ];
                return {
                    ...prev,
                    items: newItems,
                };
            }
        });
    };

    const removeArrayItem = (arrayName: 'taxes' | 'items', index: number) => {
        setFormData(prev => {
            if (arrayName === 'taxes') {
                const newTaxes = prev.payment.tax.taxes.filter((_, i) => i !== index);
                return {
                    ...prev,
                    payment: {
                        ...prev.payment,
                        tax: {
                            ...prev.payment.tax,
                            taxes: newTaxes,
                        },
                    },
                };
            } else { // arrayName === 'items'
                const newItems = prev.items.filter((_, i) => i !== index);
                return {
                    ...prev,
                    items: newItems,
                };
            }
        });
    };


    const handleSubmit = (event: React.FormEvent) => {
        event.preventDefault();
        // Use JSON.stringify for better readability in console/alert
        console.log('Expense Submitted:', JSON.stringify(formData, null, 2));
        alert('Expense submitted! Check console for details.');
    };

    return (
        <Container maxWidth={false}>
            <Box sx={{ p: 3, minWidth: 320, mx: 'auto', mt: 2, bgcolor: 'background.paper', borderRadius: 2, boxShadow: 3 }}>
                <Typography variant="h4" component="h1" gutterBottom align="center">
                    Create New Expense
                </Typography>
                <form onSubmit={handleSubmit}>
                    <Grid container spacing={{ xs: 2, md: 3 }} sx={{ flexGrow: 1 }}>
                        {/* Row 1: Identifier, Timestamp */}
                        <Grid item xs={12} sm={1} size={6}>
                            <TextField fullWidth label="Identifier"
                                name="identifier" value={formData.identifier}
                                onChange={handleInputChange} variant="outlined" margin="normal" />
                        </Grid>
                        <Grid item xs={12} sm={1} size={6}>
                            <TextField fullWidth label="Timestamp"
                                type="datetime-local" name="timestamp" value={formData.timestamp}
                                onChange={handleInputChange} variant="outlined" margin="normal" InputLabelProps={{ shrink: true }} />
                        </Grid>
                    </Grid>
                    <Grid container spacing={{ xs: 2, md: 3 }} sx={{ flexGrow: 1 }}>
                        {/* Row 2: MarketPlaceCode, StoreCode */}
                        <Grid item xs={12} sm={1} size={6}>
                            <TextField fullWidth label="Market Place Code" name="marketPlaceCode" value={formData.marketPlaceCode} onChange={handleInputChange} variant="outlined" margin="normal" />
                        </Grid>
                        <Grid item xs={12} sm={1} size={6}>
                            <TextField fullWidth label="Store Code" name="storeCode" value={formData.storeCode} onChange={handleInputChange} variant="outlined" margin="normal" />
                        </Grid>
                    </Grid>
                    <Grid item xs={12}>
                        <Divider sx={{ my: 2 }} /> {/* Horizontal Rule */}
                    </Grid>

                    <Grid container spacing={{ xs: 2, md: 3 }} sx={{ flexGrow: 1 }}>
                        {/* Total amount */}
                        <Typography variant="h6" sx={{ mt: 2, mb: 1 }}>Total Amount</Typography>
                    
                    </Grid>

                    <Grid container spacing={{ xs: 2, md: 3 }} sx={{ flexGrow: 1 }}>
                        <Typography variant="h6" sx={{ mt: 2, mb: 1 }}>Payment Details</Typography>
                        {/* Payment Details */}
                        <Grid container xs={12}>
                            
                            <Grid container spacing={2} xs={12} sm={12}>
                                <Grid item xs={12} sm={4}>
                                    <TextField fullWidth label="Payment Amount" type="number" value={formData.payment.amount} onChange={(e) => handleNestedInputChange('payment', 'amount', parseFloat(e.target.value) || 0)} variant="outlined" />
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <TextField fullWidth label="Payment Currency" value={formData.payment.currency} onChange={(e) => handleNestedInputChange('payment', 'currency', e.target.value)} variant="outlined" />
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <FormControl fullWidth variant="outlined">
                                        <InputLabel>Payment Mode</InputLabel>
                                        <Select
                                            value={formData.payment.mode}
                                            onChange={(e) => handleNestedInputChange('payment', 'mode', e.target.value)}
                                            label="Payment Mode"
                                        >
                                            <MenuItem value="CASH">CASH</MenuItem>
                                            <MenuItem value="CARD">CARD</MenuItem>
                                            <MenuItem value="OTHER">OTHER</MenuItem>
                                        </Select>
                                    </FormControl>
                                </Grid>
                            </Grid>
                            <Grid container xs={12} sm={12}>
                                {/* Total and Currency (Total) are outside payment object in JSON, so move them here. */}
                                <Grid item xs={12} sm={4}>
                                    <TextField fullWidth label="Total Amount (Overall)" type="number" value={formData.total} onChange={(e) => setFormData(prev => ({ ...prev, total: parseFloat(e.target.value) || 0 }))} variant="outlined" />
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <TextField fullWidth label="Currency (Overall)" value={formData.currency} onChange={(e) => setFormData(prev => ({ ...prev, currency: e.target.value }))} variant="outlined" />
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <TextField fullWidth label="Payment Tax Total" type="number" value={formData.payment.tax.total} onChange={(e) => handlePaymentNestedInputChange('tax', 'total', parseFloat(e.target.value) || 0)} variant="outlined" />
                                </Grid>
                            </Grid>
                        </Grid>
                    </Grid>

                    <Grid item xs={12}>
                        <Divider sx={{ my: 2 }} /> {/* Horizontal Rule */}
                    </Grid>

                    {/* Taxes Array */}
                    <Grid item xs={12}>
                        <Typography variant="h6" sx={{ mt: 2, mb: 1 }}>Taxes</Typography>
                        {formData.payment.tax.taxes.map((tax, index) => (
                            <Box key={index} sx={{ border: '1px solid #ccc', p: 2, mb: 2, borderRadius: 1 }}>
                                <Grid container spacing={2}>
                                    <Grid item xs={12} sm={6}>
                                        <TextField fullWidth label="Tax Type" value={tax.type} onChange={(e) => handleArrayChange('taxes', index, 'type', e.target.value)} variant="outlined" />
                                    </Grid>
                                    <Grid item xs={12} sm={6}>
                                        <TextField fullWidth label="Tax Unit Value" type="number" value={tax.taxUnit.value} onChange={(e) => handleArrayChange('taxes', index, 'taxUnit', parseFloat(e.target.value) || 0, 'value')} variant="outlined" />
                                    </Grid>
                                    <Grid item xs={12} sm={6}>
                                        <TextField fullWidth label="Tax Unit Code" value={tax.taxUnit.unit} onChange={(e) => handleArrayChange('taxes', index, 'taxUnit', e.target.value, 'unit')} variant="outlined" />
                                    </Grid>
                                    <Grid item xs={12} sm={6}>
                                        <TextField fullWidth label="Taxable Amount" type="number" value={tax.taxableAmount} onChange={(e) => handleArrayChange('taxes', index, 'taxableAmount', parseFloat(e.target.value) || 0)} variant="outlined" />
                                    </Grid>
                                    <Grid item xs={12} sm={6}>
                                        <TextField fullWidth label="Tax Amount" type="number" value={tax.amount} onChange={(e) => handleArrayChange('taxes', index, 'amount', parseFloat(e.target.value) || 0)} variant="outlined" />
                                    </Grid>
                                    <Grid item xs={12} sm={6}>
                                        <TextField fullWidth label="Item Category (Tax)" value={tax.itemCategory} onChange={(e) => handleArrayChange('taxes', index, 'itemCategory', e.target.value)} variant="outlined" />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <Button variant="outlined" color="error" onClick={() => removeArrayItem('taxes', index)}>Remove Tax</Button>
                                    </Grid>
                                </Grid>
                            </Box>
                        ))}
                        <Button variant="contained" onClick={() => addArrayItem('taxes')} sx={{ mt: 1 }}>Add Tax</Button>
                    </Grid>

                    <Grid item xs={12}>
                        <Divider sx={{ my: 2 }} /> {/* Horizontal Rule */}
                    </Grid>

                    {/* Items Array */}
                    <Grid item xs={12}>
                        <Typography variant="h6" sx={{ mt: 2, mb: 1 }}>Items</Typography>
                        {formData.items.map((item, index) => (
                            <Box key={index} sx={{ border: '1px solid #ccc', p: 2, mb: 2, borderRadius: 1 }}>
                                <Grid container spacing={2}>
                                    <Grid item xs={12} sm={6}>
                                        <TextField fullWidth label="Item Code" value={item.code} onChange={(e) => handleArrayChange('items', index, 'code', e.target.value)} variant="outlined" />
                                    </Grid>
                                    <Grid item xs={12} sm={6}>
                                        <TextField fullWidth label="Item Title" value={item.title} onChange={(e) => handleArrayChange('items', index, 'title', e.target.value)} variant="outlined" />
                                    </Grid>
                                    <Grid item xs={12} sm={6}>
                                        <TextField fullWidth label="Quantity" type="number" value={item.quantity} onChange={(e) => handleArrayChange('items', index, 'quantity', parseInt(e.target.value) || 0)} variant="outlined" />
                                    </Grid>
                                    <Grid item xs={12} sm={6}>
                                        <TextField fullWidth label="Price" type="number" value={item.price} onChange={(e) => handleArrayChange('items', index, 'price', parseFloat(e.target.value) || 0)} variant="outlined" />
                                    </Grid>
                                    <Grid item xs={12} sm={6}>
                                        <TextField fullWidth label="Category Code (Item)" value={item.categoryCode} onChange={(e) => handleArrayChange('items', index, 'categoryCode', e.target.value)} variant="outlined" />
                                    </Grid>
                                    <Grid item xs={12} sm={6}>
                                        <TextField fullWidth label="Unit Code (Item)" value={item.unitValue.unitCode} onChange={(e) => handleArrayChange('items', index, 'unitValue', e.target.value, 'unitCode')} variant="outlined" />
                                    </Grid>
                                    <Grid item xs={12} sm={6}>
                                        <TextField fullWidth label="Unit Value (Item)" type="number" value={item.unitValue.value} onChange={(e) => handleArrayChange('items', index, 'unitValue', parseFloat(e.target.value) || 0, 'value')} variant="outlined" />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <Button variant="outlined" color="error" onClick={() => removeArrayItem('items', index)}>Remove Item</Button>
                                    </Grid>
                                </Grid>
                            </Box>
                        ))}
                        <Button variant="contained" onClick={() => addArrayItem('items')} sx={{ mt: 1 }}>Add Item</Button>
                    </Grid>

                    <Grid item xs={12}>
                        <Divider sx={{ my: 2 }} /> {/* Horizontal Rule */}
                    </Grid>

                    {/* Metadata */}
                    <Grid item xs={12}>
                        <Typography variant="h6" sx={{ mt: 2, mb: 1 }}>Metadata</Typography>
                        <Grid container spacing={2}>
                            <Grid item xs={12} sm={6}>
                                <TextField fullWidth label="Terminal Number" value={formData.metadata.terminalNumber} onChange={(e) => handleNestedInputChange('metadata', 'terminalNumber', e.target.value)} variant="outlined" />
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <TextField fullWidth label="Serial Number" value={formData.metadata.serialNumber} onChange={(e) => handleNestedInputChange('metadata', 'serialNumber', e.target.value)} variant="outlined" />
                            </Grid>
                        </Grid>
                    </Grid>

                    <Grid item xs={12}>
                        <Divider sx={{ my: 2 }} /> {/* Horizontal Rule */}
                    </Grid>

                    {/* Submit Button */}
                    <Grid item xs={12} sx={{ mt: 3 }}>
                        <Button
                            type="submit"
                            variant="contained"
                            color="primary"
                            fullWidth
                            sx={{ py: 1.5 }}
                        >
                            Submit Expense Data
                        </Button>
                    </Grid>

                </form>
            </Box>
        </Container>

    );
};

export default CreateExpensePage;
