import React from 'react';
import Modal from './Modal';

const NotificationModal = ({ 
    isOpen, 
    onClose, 
    title, 
    message, 
    type = 'info' // 'info', 'success', 'error'
}) => {
    const isError = type === 'error';
    const isSuccess = type === 'success';

    return (
        <Modal isOpen={isOpen} onClose={onClose} title={title} maxWidth="max-w-sm">
            <div className="text-center space-y-6">
                {isError && (
                    <div className="mx-auto w-12 h-12 rounded-full bg-red-900/30 flex items-center justify-center mb-4">
                        <svg className="w-6 h-6 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                    </div>
                )}
                {isSuccess && (
                     <div className="mx-auto w-12 h-12 rounded-full bg-green-900/30 flex items-center justify-center mb-4">
                        <svg className="w-6 h-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                        </svg>
                    </div>
                )}
                
                <p className="text-neutral-300">
                    {message}
                </p>

                <button 
                    onClick={onClose}
                    className={`w-full py-2 rounded-lg text-white font-bold transition-colors ${
                        isError ? 'bg-red-600 hover:bg-red-700' : 'bg-blue-600 hover:bg-blue-700'
                    }`}
                >
                    Aceptar
                </button>
            </div>
        </Modal>
    );
};

export default NotificationModal;
