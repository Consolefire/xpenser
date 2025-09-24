import React from 'react';
import { Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, useTheme, styled } from '@mui/material';
import DashboardIcon from '@mui/icons-material/Dashboard';
import AddCircleOutline from '@mui/icons-material/AddCircleOutline'

interface SidebarProps {
  isOpen: boolean;
  drawerWidth: number;
  closedDrawerWidth: number;
  appBarHeight: number;
  onSelectPage: (page: string) => void;
  currentPage: string;
}

const StyledDrawer = styled(Drawer)(({ theme, open, drawerWidth, closedDrawerWidth }) => ({
  width: drawerWidth,
  flexShrink: 0,
  whiteSpace: 'nowrap',
  boxSizing: 'border-box',
  '& .MuiDrawer-paper': {
    width: drawerWidth,
    boxSizing: 'border-box',
    overflowX: 'hidden',
    transition: theme.transitions.create('width', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
    ...(!open && {
      transition: theme.transitions.create('width', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
      }),
      width: closedDrawerWidth,
      [theme.breakpoints.up('sm')]: {
        width: closedDrawerWidth,
      },
    }),
  },
}));

const Sidebar: React.FC<SidebarProps> = ({ isOpen, drawerWidth, closedDrawerWidth, appBarHeight, onSelectPage, currentPage }) => {
  const theme = useTheme();

  const menuItems = [
    { text: 'Dashboard', icon: <DashboardIcon />, page: 'Dashboard' },
    { text: 'Create Expense', icon: <AddCircleOutline />, page: 'Create Expense' }
  ];

  return (
    <StyledDrawer
      variant="permanent"
      anchor="left"
      open={isOpen}
      drawerWidth={drawerWidth}
      closedDrawerWidth={closedDrawerWidth}
      sx={{
        [`& .MuiDrawer-paper`]: {
          top: `${appBarHeight}px`,
          height: `calc(100vh - ${appBarHeight * 2}px)`,
          overflowY: 'auto',
          backgroundColor: theme.palette.background.paper,
          borderRight: `1px solid ${theme.palette.divider}`,
        },
      }}
    >
      <List>
        {menuItems.map((item) => (
          <ListItem key={item.text} disablePadding sx={{ display: 'block' }}>
            <ListItemButton
              onClick={() => onSelectPage(item.page)}
              selected={currentPage === item.page}
              sx={{
                minHeight: 48,
                justifyContent: isOpen ? 'initial' : 'center',
                px: 2.5,
              }}
            >
              <ListItemIcon
                sx={{
                  minWidth: 0,
                  mr: isOpen ? 3 : 'auto',
                  justifyContent: 'center',
                }}
              >
                {item.icon}
              </ListItemIcon>
              <ListItemText primary={item.text} sx={{ opacity: isOpen ? 1 : 0 }} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
    </StyledDrawer>
  );
};

export default Sidebar;
