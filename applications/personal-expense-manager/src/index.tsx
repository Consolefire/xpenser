import React from 'react';
import { createRoot } from 'react-dom/client';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssVarsProvider } from '@mui/joy/styles';
import CssBaseline from '@mui/joy/CssBaseline';
import GlobalStyles from '@mui/joy/GlobalStyles';
import Application from './application/Application'; // Updated import path

// ---------------------------------------------------
// Theme Configuration
// ---------------------------------------------------
const theme = createTheme({
  palette: {
    primary: {
      main: '#343a40', // Dark grey for AppBar/Footer
    },
    secondary: {
      main: '#007bff', // Blue for buttons
    },
    background: {
      default: '#f8f9fa', // Light background for main content
      paper: '#ffffff', // White for cards/paper
    },
  },
  typography: {
    fontFamily: [
      '-apple-system',
      'BlinkMacSystemFont',
      '"Segoe UI"',
      'Roboto',
      '"Oxygen"',
      '"Ubuntu"',
      '"Cantarell"',
      '"Fira Sans"',
      '"Droid Sans"',
      '"Helvetica Neue"',
      'sans-serif',
    ].join(','),
  },
  components: {
    MuiAppBar: {
      styleOverrides: {
        root: {
          // Adjustments for AppBar positioning if needed,
          // though 'position="fixed"' handles much of it.
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          // Additional custom styles for the Drawer paper (the visible part)
          // Adjust z-index if needed
        },
      },
    },
  },
});

// ---------------------------------------------------
// Root rendering
// ---------------------------------------------------
document.addEventListener('DOMContentLoaded', () => {
  const container = document.getElementById('root');
  if (container) {
    const root = createRoot(container);
    root.render(
      <React.StrictMode>
        <CssVarsProvider>
          <CssBaseline />
          <ThemeProvider theme={theme}>
            <Application /> {/* Render Application component */}
          </ThemeProvider>
        </CssVarsProvider>
      </React.StrictMode>
    );
  } else {
    console.error("Failed to find the root element. Ensure there is a div with id='root' in your HTML.");
  }
});
