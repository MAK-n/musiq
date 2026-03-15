import { Route, Routes } from "react-router-dom";
import LandingPage from "../pages/LandingPage";
import CallbackPage from "../pages/CallbackPage";
import ProfilePage from "../pages/ProfilePage";
import Layout from "../components/Layout";

export default function AppRoutes() {
    return (
        <Layout>
            <Routes>
                <Route path="/" element={<LandingPage />} />
                <Route path="/callback" element={<CallbackPage />} />
                <Route path="/profile" element={<ProfilePage />} />
            </Routes>
        </Layout>
    );
}