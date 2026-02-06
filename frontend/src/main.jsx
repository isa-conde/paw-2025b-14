import React, { Suspense } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App.jsx';
import './styles/app.css';
import '../i18n.js';

const rootElement = document.getElementById('root');

createRoot(rootElement).render(
    <React.StrictMode>
        <Suspense fallback={<div>Loading...</div>}>
            <BrowserRouter>
                <App />
            </BrowserRouter>
        </Suspense>
    </React.StrictMode>
);