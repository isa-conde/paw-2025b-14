import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Button } from '../components/Button.jsx';

describe('Button Component', () => {
    it('renders with text', () => {
        render(<Button text="Click me" />);
        expect(screen.getByText('Click me')).toBeInTheDocument();
    });
});