"use client";

import { useAuthStore } from "@/stores/auth/auth-store";
import { useRouter } from "next/navigation";

export default function Home() {
  const { user, isAuthenticated } = useAuthStore();
  const router = useRouter();

  return (
    <div className="grid grid-rows-[20px_1fr_20px] p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
      <main className="flex flex-col gap-[16px] row-start-2 items-center">
        <h1>
          {isAuthenticated && `Olá, ${user?.name}!`} Seja bem-vindo ao
          LanguagesLife!
        </h1>
        {isAuthenticated ? (
          <>
            <div className="flex flex-col w-full items-center">
              <h2>Configure suas preferências</h2>
              <button
                onClick={() => router.push("/language-preferences")}
                className="secondary-button max-w-[200px]"
              >
                Preferências do Usuário
              </button>
            </div>
            <div className="flex flex-col w-full items-center">
              <h2>Acesse sua rotina de idioma</h2>
              <button
                onClick={() => router.push("/language-routine")}
                className="secondary-button max-w-[200px]"
              >
                Rotina de Idioma
              </button>
            </div>
            <div className="flex flex-col w-full items-center">
              <h2>Acesse seu histórico de rotinas</h2>
              <button
                onClick={() => router.push("/history")}
                className="secondary-button max-w-[200px]"
              >
                Histórico de Rotinas
              </button>
            </div>
            <div className="flex flex-col w-full items-center">
              <h2>Acesse suas notificações</h2>
              <button
                onClick={() => router.push("/notifications")}
                className="secondary-button max-w-[200px]"
              >
                Notificações
              </button>
            </div>
            <div className="flex flex-col w-full items-center">
              <h2>Acesse seu quadro de metas</h2>
              <button
                onClick={() => router.push("/goal-board")}
                className="secondary-button max-w-[200px]"
              >
                Quadro de Metas
              </button>
            </div>
          </>
        ) : (
          <p>Faça login ou se cadastre para acessar sua conta.</p>
        )}
      </main>
    </div>
  );
}
