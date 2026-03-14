import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { Navigate } from "react-router-dom";
// import { Navigate, useNavigate } from "react-router-dom";

export default function CallbackPage() {
    // const navigate = useNavigate();
    const { login } = useAuth();
    const [redirectTo, setRedirectTo] = useState<string | null>(null);
    
    useEffect(()=>{
        const params = new URLSearchParams(window.location.search);
        const jwt = params.get('jwt');
        if(jwt){
            login(jwt);
            setRedirectTo('/profile');
            // navigate('/profile');
        }else{
            setRedirectTo('/');
            // navigate('/');
        }
    }, []);
    if(redirectTo){
        return <Navigate to={redirectTo} />;
    }
    return (
        <div>
            Logging in...
        </div>
    );
}