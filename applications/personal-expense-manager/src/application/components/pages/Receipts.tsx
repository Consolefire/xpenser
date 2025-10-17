import React, { useState, useEffect } from 'react';
import {
    Container,
    Typography,
    Paper,
    TableContainer,
    Table,
    TableHead,
    TableBody,
    TableRow,
    TableCell,
    Button,
    Box,
    IconButton,
    styled,
    CircularProgress,
    Alert,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
// Removed: import { MOCK_RECEIPTS, SimpleReceiptDto } from './mock-data';
import { fetchAllReceipts, SimpleReceiptDto } from '../../../clients/expense/ExpenseClient'; // New client import

// Styled Button for the Add action to match the requested <<add>> look
const AddButton = styled(Button)(({ theme }) => ({
    minWidth: '70px',
    padding: theme.spacing(0.5, 1),
    fontSize: '0.8rem',
    textTransform: 'none',
    border: '1px dashed',
    borderRadius: theme.shape.borderRadius,
}));

// Function to format the ISO timestamp to a readable date/time string
const formatTimestamp = (isoString: string): string => {
    // Simple formatting for display purposes (e.g., "15 Oct, 10:00")
    try {
        const date = new Date(isoString);
        if (isNaN(date.getTime())) return isoString; // Return original if invalid
        return date.toLocaleString('en-GB', {
            day: '2-digit',
            month: 'short',
            hour: '2-digit',
            minute: '2-digit',
        });
    } catch (e) {
        return isoString;
    }
};

const Receipts: React.FC = () => {
    const [receipts, setReceipts] = useState<SimpleReceiptDto[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    // Effect to fetch data on component mount
    useEffect(() => {
        const loadReceipts = async () => {
            setLoading(true);
            setError(null);
            try {
                const data = await fetchAllReceipts();
                setReceipts(data);
            } catch (err: any) {
                console.error("Failed to load receipts:", err);
                setError(err.message || 'Failed to fetch receipts from the service.');
            } finally {
                setLoading(false);
            }
        };

        loadReceipts();
    }, []);


    const handleAddReceipt = () => {
        alert('Action: Navigate to MinimalReceiptForm or ReceiptForm to <<add>> a new receipt.');
    };

    const handleUpdateReceipt = (receiptId: string) => {
        alert(`Action: Open edit form for receipt ID: ${receiptId} (<<update>>)`);
    };

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ p: 2, borderRadius: 2 }}>
                
                {/* Header Row: Receipts | <<add>> */}
                <Box
                    display="flex"
                    justifyContent="space-between"
                    alignItems="center"
                    mb={2}
                    sx={{ borderBottom: '1px solid #ccc', pb: 1 }}
                >
                    <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                        Receipts
                    </Typography>
                    <AddButton 
                        variant="outlined" 
                        color="primary" 
                        startIcon={<AddIcon fontSize="small" />} 
                        onClick={handleAddReceipt}
                    >
                        &lt;&lt;add&gt;&gt;
                    </AddButton>
                </Box>

                {/* Error/Loading state */}
                {loading && (
                    <Box display="flex" justifyContent="center" py={4} alignItems="center">
                        <CircularProgress size={24} />
                        <Typography variant="body1" ml={2}>Loading receipts...</Typography>
                    </Box>
                )}

                {error && (
                    <Alert severity="error" sx={{ mb: 2 }}>
                        {error}
                    </Alert>
                )}

                {/* Table Section (Visible only when not loading and no major error) */}
                {!loading && !error && (
                    <TableContainer>
                        <Table size="small">
                            <TableHead>
                                <TableRow sx={{ backgroundColor: '#f5f5f5' }}>
                                    <TableCell sx={{ fontWeight: 'bold' }}>Timestamp</TableCell>
                                    <TableCell sx={{ fontWeight: 'bold' }}>Identifier</TableCell>
                                    <TableCell sx={{ fontWeight: 'bold' }}>Marketplace</TableCell>
                                    <TableCell sx={{ fontWeight: 'bold' }}>Store</TableCell>
                                    <TableCell align="right" sx={{ fontWeight: 'bold' }}>Amount</TableCell>
                                    <TableCell align="center" sx={{ fontWeight: 'bold' }}>&lt;&lt;update&gt;&gt;</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {receipts.map((receipt: SimpleReceiptDto) => (
                                    <TableRow key={receipt.identifier} hover>
                                        <TableCell>{formatTimestamp(receipt.timestamp)}</TableCell>
                                        <TableCell>{receipt.identifier}</TableCell>
                                        <TableCell>{receipt.marketPlaceCode}</TableCell>
                                        <TableCell>{receipt.storeCode}</TableCell>
                                        <TableCell align="right">
                                            {receipt.total.toFixed(2)} {receipt.currency}
                                        </TableCell>
                                        <TableCell align="center">
                                            <IconButton 
                                                size="small" 
                                                color="primary" 
                                                onClick={() => handleUpdateReceipt(receipt.identifier)}
                                                aria-label={`Update receipt ${receipt.identifier}`}
                                            >
                                                <EditIcon fontSize="small" />
                                            </IconButton>
                                        </TableCell>
                                    </TableRow>
                                ))}
                                {receipts.length === 0 && (
                                    <TableRow>
                                        <TableCell colSpan={6} align="center">
                                            No receipts found.
                                        </TableCell>
                                    </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}
            </Paper>
        </Container>
    );
};

export default Receipts;
