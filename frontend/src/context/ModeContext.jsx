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

  const toggleMode = () => {
    setIsMaster(prev => !prev);
  };

  return (
    <ModeContext.Provider value={{ isMaster, toggleMode }}>
      {children}
    </ModeContext.Provider>
  );
};

export const useMode = () => useContext(ModeContext);
