"use client";

import { useState, useEffect, FormEvent } from "react";
import { useAuthStore } from "@/stores/auth/auth-store"; // Importando o seu store de autenticação
import { toast } from "sonner"; // Importando o toast para notificações

type LanguageExperienceLevel = "BEGINNER" | "INTERMEDIATE" | "ADVANCED";
type LanguageSkill = "SPEAKING" | "WRITING" | "LISTENING" | "READING";

const experienceLevelLabels: Record<LanguageExperienceLevel, string> = {
  BEGINNER: "Iniciante",
  INTERMEDIATE: "Intermediário",
  ADVANCED: "Avançado",
};

const languageSkillLabels: Record<LanguageSkill, string> = {
  SPEAKING: "Fala",
  WRITING: "Escrita",
  LISTENING: "Escuta",
  READING: "Leitura",
};

interface LanguageUserPreferencesDTO {
  languageSkill: LanguageSkill;
  experienceLevel: LanguageExperienceLevel;
}

interface LanguageUserPreferences extends LanguageUserPreferencesDTO {
  id: number;
}

export default function UserPreferencesPage() {
  const { token, isAuthenticated } = useAuthStore(); // Pega o estado de autenticação

  const [languageSkill, setLanguageSkill] = useState<LanguageSkill>("SPEAKING");
  const [experienceLevel, setExperienceLevel] =
    useState<LanguageExperienceLevel>("BEGINNER");

  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [isSaving, setIsSaving] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function fetchPreferences() {
      setIsLoading(true);
      setError(null);

      if (!isAuthenticated || !token) {
        setError("Usuário não autenticado. Por favor, faça login.");
        setIsLoading(false);
        return;
      }

      try {
        const response = await fetch(
          "http://localhost:8080/api/language-preferences",
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(
            errorData.message || "Não foi possível carregar suas preferências."
          );
        }

        const data: LanguageUserPreferences = await response.json();

        // Preenche o formulário com os dados recebidos do backend
        setLanguageSkill(data.languageSkill || "SPEAKING");
        setExperienceLevel(data.experienceLevel || "BEGINNER");
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
      } catch (err: any) {
        console.error("Erro ao buscar preferências:", err);
        setError(err.message || "Ocorreu um erro desconhecido.");
        toast.error(err.message || "Erro ao carregar dados.");
      } finally {
        setIsLoading(false);
      }
    }

    fetchPreferences();
  }, [token, isAuthenticated]);

  async function handleSavePreferences(event: FormEvent) {
    event.preventDefault();

    if (!isAuthenticated || !token) {
      toast.error("Usuário não autenticado. Faça login para salvar.");
      return;
    }

    setIsSaving(true);
    const body: LanguageUserPreferencesDTO = {
      languageSkill: languageSkill,
      experienceLevel: experienceLevel,
    };

    try {
      const response = await fetch(
        "http://localhost:8080/api/language-preferences",
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(body),
        }
      );

      if (response.ok) {
        toast.success("Preferências salvas com sucesso!");
      } else {
        const errorData = await response.json();
        throw new Error(errorData.message || "Erro ao salvar preferências.");
      }
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (err: any) {
      console.error("Erro ao salvar preferências:", err);
      toast.error(err.message || "Erro de conexão ao salvar.");
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <div className="min-h-screen bg-[var(--gray-bg)] pt-8 p-4 flex flex-col items-center">
      <div className="w-3/4 max-w-lg p-8 space-y-6 bg-[var(--form-white)] rounded-lg shadow-md">
        <h1 className="text-3xl font-bold text-center text-[var(--text-main)]">
          Configurar Preferências
        </h1>
        <p className="text-center text-gray-600">
          Suas preferências nos ajudam a criar rotinas de estudo mais eficazes e
          personalizadas para você.
        </p>

        {isLoading ? (
          <p className="text-center text-gray-500">Carregando...</p>
        ) : error ? (
          <p className="text-center text-red-500">{error}</p>
        ) : (
          <form onSubmit={handleSavePreferences} className="space-y-6">
            {/* Campo Idade */}
            <div>
              <label
                htmlFor="language-skill"
                className="block text-xl font-semibold mb-2 text-[var(--text-main)]"
              >
                Principal Habilidade a Praticar
              </label>
              <select
                id="language-skill"
                value={languageSkill}
                onChange={(e) =>
                  setLanguageSkill(e.target.value as LanguageSkill)
                }
                className="p-2 border border-gray-300 rounded-lg w-full"
              >
                {Object.entries(languageSkillLabels).map(([key, label]) => (
                  <option key={key} value={key}>
                    {label}
                  </option>
                ))}
              </select>
            </div>

            {/* Campo Nível de Experiência */}
            <div>
              <label
                htmlFor="experience"
                className="block text-xl font-semibold mb-2 text-[var(--text-main)]"
              >
                Seu Nível de Experiência
              </label>
              <select
                id="experience"
                value={experienceLevel}
                onChange={(e) =>
                  setExperienceLevel(e.target.value as LanguageExperienceLevel)
                }
                className="p-2 border border-gray-300 rounded-lg w-full"
              >
                {Object.entries(experienceLevelLabels).map(([key, label]) => (
                  <option key={key} value={key}>
                    {label}
                  </option>
                ))}
              </select>
            </div>

            {/* Botão de Submissão */}
            <div>
              <button
                type="submit"
                disabled={isSaving}
                className="w-full secondary-button disabled:bg-gray-400 disabled:cursor-not-allowed"
              >
                {isSaving ? "Salvando..." : "Salvar Preferências"}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
