import React from 'react';
import { Box, useTheme } from '@mui/material';
import DashboardPage from '../pages/DashboardPage'; // Updated import path
import CreateExpensePage from '../pages/CreateExpensePage';

interface MainContainerProps {
  children: React.ReactNode;
  isSidebarOpen: boolean;
  drawerWidth: number;
  closedDrawerWidth: number;
  appBarHeight: number;
  currentPage: string;
}

const MainContainer: React.FC<MainContainerProps> = ({
  children,
  isSidebarOpen,
  drawerWidth,
  closedDrawerWidth,
  appBarHeight,
  currentPage,
}) => {
  const theme = useTheme();

  return (
    <Box
      component="main"
      sx={{
        flexGrow: 1,
        p: 3,
        mt: `${appBarHeight}px`,
        pb: `${appBarHeight}px`,
        display: 'flex',
        flexDirection: 'column',
        boxSizing: 'border-box',
        ml: isSidebarOpen ? `${drawerWidth}px` : `${closedDrawerWidth}px`,
        width: isSidebarOpen
          ? `calc(100% - ${drawerWidth}px)`
          : `calc(100% - ${closedDrawerWidth}px)`,
        transition: theme.transitions.create(['margin', 'width'], {
          easing: theme.transitions.easing.sharp,
          duration: theme.transitions.duration.leavingScreen,
        }),
        backgroundColor: theme.palette.background.default,
        overflowY: 'auto',
      }}
    >
      {/* Conditionally render content based on currentPage */}
      {currentPage === 'Dashboard' ? <DashboardPage /> : children}
      {currentPage === 'Create Expense' ? <CreateExpensePage /> : children}
    </Box>
  );
};

export default MainContainer;
