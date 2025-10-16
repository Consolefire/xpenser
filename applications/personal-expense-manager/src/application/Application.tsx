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
      
    </Layout>
  );
}

export default Application; // Exporting Application
