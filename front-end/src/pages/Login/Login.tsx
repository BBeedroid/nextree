import React, { useState, ReactElement } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { SPRING_API_URL } from "../../config";
import {
    Box,
    Container,
    Input,
    Button,
    RightButtonDiv,
    LeftButtonDiv,
} from "../../styles/theme";
import NavigateButton from "../Util/NavigateButton";

interface LoginProps {
    handleLogin: (memberEmail: string, memberPassword: string) => Promise<void>;
}

const LoginComponent = ({ handleLogin }: LoginProps): ReactElement => {
    const [memberEmail, setMemberEmail] = useState<string>("");
    const [memberPassword, setMemberPassword] = useState<string>("");

    const submitLogin = async (
        event: React.FormEvent<HTMLFormElement>,
    ): Promise<void> => {
        event.preventDefault();
        await handleLogin(memberEmail, memberPassword);
    };

    return (
        <Box>
            <Container width="350px" height="250px">
                <form onSubmit={submitLogin}>
                    <Input
                        type="email"
                        value={memberEmail}
                        onChange={(e) => setMemberEmail(e.target.value)}
                        placeholder="Email"
                    />
                    <Input
                        type="password"
                        value={memberPassword}
                        onChange={(e) => setMemberPassword(e.target.value)}
                        placeholder="Password"
                    />
                    <RightButtonDiv>
                        <Button type="submit">로그인</Button>
                    </RightButtonDiv>
                    <LeftButtonDiv>
                        <NavigateButton path="/signup" label="회원 가입" />
                    </LeftButtonDiv>
                </form>
            </Container>
        </Box>
    );
};

const loginHandler = (
    navigate: ReturnType<typeof useNavigate>,
): {
    handleLogin: (memberEmail: string, memberPassword: string) => Promise<void>;
} => {
    const handleLogin = async (
        memberEmail: string,
        memberPassword: string,
    ): Promise<void> => {
        try {
            const response = await axios.post(`${SPRING_API_URL}/auth`, {
                memberEmail,
                memberPassword,
            });
            const { token, message } = response.data.item;
            if (token) {
                localStorage.setItem("token", token);
                navigate("/my-club-list");
            } else {
                console.error("token not received");
                alert(`"${message}"`);
            }
        } catch (error) {
            if (axios.isAxiosError(error) && error.response) {
                const errorMessage =
                    error.response.data.message || "Login failed";
                alert(errorMessage);
                console.error("An error occurred:", error);
            } else {
                console.error("An error occurred:", error);
                alert("에러가 발생했습니다.");
            }
        }
    };

    return { handleLogin };
};

const Login = (): ReactElement => {
    const navigate = useNavigate();
    const { handleLogin } = loginHandler(navigate);

    return <LoginComponent handleLogin={handleLogin} />;
};

export default Login;
