import React from 'react';
import { AppBar, Typography, useTheme } from '@mui/material';

interface FooterProps {
  appBarHeight: number;
}

const Footer: React.FC<FooterProps> = ({ appBarHeight }) => {
  const theme = useTheme();

  return (
    <AppBar
      position="fixed"
      component="footer"
      sx={{
        bottom: 0,
        width: '100%',
        backgroundColor: theme.palette.primary.dark,
        color: theme.palette.common.white,
        py: 2,
        textAlign: 'center',
        zIndex: theme.zIndex.appBar + 1,
      }}
    >
      <Typography variant="body2">
        &copy; {new Date().getFullYear()} My Company. All rights reserved.
      </Typography>
    </AppBar>
  );
};

export default Footer;
