import { createContext, useContext, useEffect, useState } from "react";

interface AuthContextType {
    token: string | null;
    login: (jwt: string) => void;
    logout: () => void;
    isAuthenticated: boolean;
    isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({children}: {children: React.ReactNode}) {
    const [token, setToken] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('jwt');
        if (token) {
            setToken(token);
        }
        setIsLoading(false);
    }, []);

    const login = (jwt: string) => {
        localStorage.setItem('jwt', jwt);
        setToken(jwt);
    };

    const logout = () => {
        localStorage.removeItem('jwt');
        setToken(null);
    };

    return (
        <AuthContext.Provider value={{token, login, logout, isAuthenticated: !!token, isLoading}}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}