"use client";

import { useState } from "react";
import Link from "next/link";
import { useLogin } from "@/hooks/use-login";
import { toast } from "sonner";

export default function LoginPage() {
  const [form, setForm] = useState({ email: "", password: "" });
  const { mutate } = useLogin();

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();

    if (form.email && form.password) {
      try {
        await mutate({ email: form.email, password: form.password });
      } catch (error) {
        console.error("Erro ao autenticar:", error);
        toast.error("Falha ao realizar login.");
      }
    } else {
      toast.error("Preencha todos os campos.");
    }
  }

  return (
    <div className="w-full mt-[8%] flex flex-col items-center bg-[var(--gray-bg)]">
      <div className="flex-1 flex flex-col justify-center items-center p-6 w-full max-w-md">
        <div className="form-wrapper p-6 bg-white rounded-lg shadow-lg w-full">
          <div className="form-title">Entrar</div>
          <div className="form-desc">Acesse sua conta</div>
          <form onSubmit={handleSubmit} className="w-full space-y-3">
            <div>
              <label htmlFor="email">E-mail</label>
              <input
                id="email"
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                placeholder="Seu e-mail"
                required
                autoFocus
              />
            </div>
            <div>
              <label htmlFor="password">Senha</label>
              <input
                id="password"
                type="password"
                name="password"
                value={form.password}
                onChange={handleChange}
                placeholder="Sua senha"
                required
              />
            </div>
            <button type="submit" className="main-button">
              Entrar
            </button>
          </form>
          <Link href="/register" className="secondary-button">
            Ainda não tem conta? Criar agora
          </Link>
        </div>
      </div>
    </div>
  );
}
