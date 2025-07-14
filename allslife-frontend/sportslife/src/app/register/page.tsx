"use client";

import { useState } from "react";
import Link from "next/link";
import { useCreateUser } from "@/hooks/use-create-user";
import { useRouter } from "next/navigation";

export default function RegisterPage() {
  const [form, setForm] = useState({ name: "", email: "", password: "" });
  const { mutateAsync } = useCreateUser();
  const router = useRouter();

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();

    if (form.name && form.email && form.password) {
      try {
        await mutateAsync(form);
        router.push("/login");
      } catch (error) {
        console.error("Erro ao registrar:", error);
      }
    }
  }

  return (
    <div className="w-full min-h-screen flex flex-col bg-[var(--gray-bg)]">
      <div className="flex-1 flex flex-col items-center justify-center">
        <div className="form-wrapper">
          <div className="form-title">Crie sua conta</div>
          <div className="form-desc">
            Preencha os campos abaixo para se registrar
          </div>
          <form onSubmit={handleSubmit} className="w-full space-y-3">
            <div>
              <label htmlFor="name">Nome</label>
              <input
                id="name"
                type="text"
                name="name"
                value={form.name}
                onChange={handleChange}
                placeholder="Seu nome"
                required
                autoFocus
              />
            </div>
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
                placeholder="Crie uma senha"
                required
              />
            </div>
            <button type="submit" className="main-button">
              Registrar
            </button>
          </form>
          <Link href="/login" className="secondary-button">
            Já tem conta? Fazer Login
          </Link>
        </div>
      </div>
    </div>
  );
}
