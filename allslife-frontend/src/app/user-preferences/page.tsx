"use client";

import { useState, useEffect, FormEvent } from "react";
import { useAuthStore } from "@/stores/auth/auth-store"; // Importando o seu store de autenticação
import { toast } from "sonner"; // Importando o toast para notificações

type SportExperienceLevel = "BEGINNER" | "INTERMEDIATE" | "ADVANCED";

const experienceLevelLabels: Record<SportExperienceLevel, string> = {
  BEGINNER: "Iniciante",
  INTERMEDIATE: "Intermediário",
  ADVANCED: "Avançado",
};

interface SportUserPreferencesDTO {
  age: number;
  experienceLevel: SportExperienceLevel;
}

interface SportUserPreferences extends SportUserPreferencesDTO {
  id: number;
}

export default function UserPreferencesPage() {
  const { token, isAuthenticated } = useAuthStore(); // Pega o estado de autenticação

  const [age, setAge] = useState<string>("");
  const [experienceLevel, setExperienceLevel] =
    useState<SportExperienceLevel>("BEGINNER");

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
          "http://localhost:8080/api/user-preferences",
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

        const data: SportUserPreferences = await response.json();

        // Preenche o formulário com os dados recebidos do backend
        setAge(data.age ? data.age.toString() : "");
        setExperienceLevel(data.experienceLevel || "BEGINNER");
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

    const ageAsNumber = parseInt(age, 10);
    if (isNaN(ageAsNumber) || ageAsNumber <= 10) {
      toast.error("Por favor, insira uma idade válida.");
      return;
    }

    if (!isAuthenticated || !token) {
      toast.error("Usuário não autenticado. Faça login para salvar.");
      return;
    }

    setIsSaving(true);
    const body: SportUserPreferencesDTO = {
      age: ageAsNumber,
      experienceLevel: experienceLevel,
    };

    try {
      const response = await fetch(
        "http://localhost:8080/api/user-preferences",
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
    } catch (err: any) {
      console.error("Erro ao salvar preferências:", err);
      toast.error(err.message || "Erro de conexão ao salvar.");
    } finally {
      setIsSaving(false);
    }
  }

  // Função auxiliar para validar a entrada de idade
  const handleAgeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    if (/^\d*$/.test(value) && value.length <= 3) {
      setAge(value);
    }
  };

  return (
    <div className="min-h-screen bg-[var(--gray-bg)] pt-8 p-4 flex flex-col items-center">
      <div className="w-3/4 max-w-lg p-8 space-y-6 bg-[var(--form-white)] rounded-lg shadow-md">
        <h1 className="text-3xl font-bold text-center text-[var(--text-main)]">
          Configurar Preferências
        </h1>
        <p className="text-center text-gray-600">
          Suas preferências nos ajudam a criar rotinas de treino mais eficazes e
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
                htmlFor="age"
                className="block text-xl font-semibold mb-2 text-[var(--text-main)]"
              >
                Sua Idade
              </label>
              <input
                id="age"
                type="text"
                inputMode="numeric"
                value={age}
                onChange={handleAgeChange}
                className="p-2 border border-gray-300 rounded-lg w-full"
                placeholder="Ex: 25"
                required
              />
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
                  setExperienceLevel(e.target.value as SportExperienceLevel)
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
