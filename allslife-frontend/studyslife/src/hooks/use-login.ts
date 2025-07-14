'use client'

import { useMutation } from "@tanstack/react-query";
import { login } from "@/api/auth/login";
import { useAuthStore } from "@/stores/auth/auth-store";
import { toast } from "sonner";
import { useRouter } from "next/navigation";

export function useLogin() {
  const { setUser, setToken, setAuthenticated } = useAuthStore();
  
  const router = useRouter();

  const mutation = useMutation({
    mutationFn: login,
    onSuccess: (data) => {
      const user = {
        id: data.user.userId.toString(),
        name: data.user.name,
        email: data.user.email,
      }
      setUser(user);
      setToken(data.token);
      setAuthenticated(true);
      toast.success("Login realizado com sucesso!");
      router.push("/home")
    },
    onError: () => {
      toast.error("Email ou senha inválidos");
    },
  });

  return mutation;
}
