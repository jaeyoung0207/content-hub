import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App.tsx';
import './i18n.ts';
import { sentryInit } from './sentry.ts';

// Sentry 초기화
sentryInit();

createRoot(document.getElementById('root')!).render(<App />);
