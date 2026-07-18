import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

const apiProxyTarget = process.env.VITE_API_PROXY_TARGET ?? 'http://localhost:8080';

export default defineConfig({
    plugins: [
        react(),
        {
            name: 'tomcat-jsp-session-directive',
            apply: 'build',
            transformIndexHtml(html) {
                return `<%@ page session="false" %>\n${html}`;
            }
        }
    ],
    server: {
        port: 5173,
        proxy: {
            '/api': {
                target: apiProxyTarget,
                changeOrigin: true
            }
        }
    },
    build: {
        outDir: 'build',
        emptyOutDir: true
    }
});
