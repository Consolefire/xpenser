import React, { useState } from 'react';
import { Box, Typography } from '@mui/material';
import Layout from './components/layout/Layout'; // Updated import path

function Application() { // Renamed from App to Application
  const [currentPage, setCurrentPage] = useState('Dashboard');

  const handleSelectPage = (page: string) => {
    setCurrentPage(page);
  };

  return (
    <Layout currentPage={currentPage} onSelectPage={handleSelectPage}>
      <Box sx={{ p: 3, textAlign: 'center' }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Welcome!
        </Typography>
        <Typography variant="body1">
          Select an option from the sidebar.
        </Typography>
      </Box>
    </Layout>
  );
}

export default Application; // Exporting Application
