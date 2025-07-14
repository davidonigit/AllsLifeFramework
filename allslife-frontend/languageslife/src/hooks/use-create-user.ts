import { useMutation } from "@tanstack/react-query";
import { toast } from "sonner";

interface CreateUserDTO {
  name: string;
  email: string;
  password: string;
}

async function createUser(data: CreateUserDTO) {
  const response = await fetch("http://localhost:8080/users", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const error = await response.text();
    throw new Error(error || "Erro ao criar usuário");
  }

  return response.json();
}

export function useCreateUser() {
  return useMutation({
    mutationFn: createUser,
    onSuccess: () => {
      toast.success("Registro realizado com sucesso!",
        {
          description: "Realize o login agora para acessar nosso sistema.",}
      );
    },
    onError: () => {
      toast.error("Erro ao realizar registro. Verifique os dados.");
    },
  });
}
