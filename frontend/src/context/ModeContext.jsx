import React, { createContext, useState, useEffect, useContext } from 'react';

const ModeContext = createContext();

export const ModeProvider = ({ children }) => {
  const [isMaster, setIsMaster] = useState(() => {
    const savedMode = localStorage.getItem('isMaster');
    return savedMode === 'true';
  });

  useEffect(() => {
    localStorage.setItem('isMaster', isMaster);
  }, [isMaster]);

  const [hasMasterPrivileges, setHasMasterPrivileges] = useState(() => {
    const savedPrivileges = localStorage.getItem('hasMasterPrivileges');
    return savedPrivileges === 'true';
  });

  useEffect(() => {
    localStorage.setItem('hasMasterPrivileges', hasMasterPrivileges);
  }, [hasMasterPrivileges]);

  const toggleMode = () => {
    setIsMaster(prev => !prev);
  };

  return (
    <ModeContext.Provider value={{ isMaster, toggleMode, setIsMaster, hasMasterPrivileges, setHasMasterPrivileges }}>
      {children}
    </ModeContext.Provider>
  );
};

export const useMode = () => useContext(ModeContext);
