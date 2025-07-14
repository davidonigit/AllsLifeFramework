import axios from "axios";

interface LoginRequest {
  email: string;
  password: string;
}

interface UserDTO {
  userId: number;
  name: string;
  email: string;
}

interface LoginResponse {
  email: string;
  token: string;
  user: UserDTO;
}

export async function login(data: LoginRequest): Promise<LoginResponse> {
    const response = await axios.post<LoginResponse>("http://localhost:8080/auth/login", data);
    return response.data;
}
