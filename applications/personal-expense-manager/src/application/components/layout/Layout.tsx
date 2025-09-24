import React, { useState } from 'react';
import { AppBar, Toolbar, IconButton, Typography, Box, CssBaseline, useTheme } from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import Sidebar from './Sidebar'; // Relative import for Sidebar
import MainContainer from './MainContainer'; // Relative import for MainContainer
import Footer from './Footer'; // Relative import for Footer

interface LayoutProps {
  children: React.ReactNode;
  currentPage: string;
  onSelectPage: (page: string) => void;
}

const drawerWidth = 240;
const closedDrawerWidth = 60;

const Layout: React.FC<LayoutProps> = ({ children, currentPage, onSelectPage }) => {
  const theme = useTheme();
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  const handleDrawerToggle = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const appBarHeight = theme.mixins.toolbar.minHeight as number;

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', flexDirection: 'column' }}>
      {/* <CssBaseline /> */}

      {/* Header (AppBar) */}
      <AppBar
        position="fixed"
        sx={{
          zIndex: theme.zIndex.drawer + 1,
          width: '100%',
        }}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="toggle drawer"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2 }}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
            Expense Manager
          </Typography>
        </Toolbar>
      </AppBar>

      {/* Sidebar (Drawer) */}
      <Sidebar
        isOpen={isSidebarOpen}
        drawerWidth={drawerWidth}
        closedDrawerWidth={closedDrawerWidth}
        appBarHeight={appBarHeight}
        onSelectPage={onSelectPage}
        currentPage={currentPage}
      />

      {/* Main Content Area */}
      <MainContainer
        isSidebarOpen={isSidebarOpen}
        drawerWidth={drawerWidth}
        closedDrawerWidth={closedDrawerWidth}
        appBarHeight={appBarHeight}
        currentPage={currentPage}
      >
        {children}
      </MainContainer>

      {/* Footer */}
      {/* <Footer appBarHeight={appBarHeight} /> */}
    </Box>
  );
};

export default Layout;
