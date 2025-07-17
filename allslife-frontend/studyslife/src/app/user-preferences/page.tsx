"use client";

import { useState, useEffect, FormEvent } from "react";
import { useAuthStore } from "@/stores/auth/auth-store"; // Importando o seu store de autenticação
import { toast } from "sonner"; // Importando o toast para notificações

type StudyMaterial = "VIDEO_LESSONS" | "BOOKS" | "EXERCISE_LISTS" | "IN_PERSON" | "ONLINE_COURSES" | "PODCASTS" | "ARTICLES" | "FLASHCARDS" | "PRACTICE_TESTS" | "STUDY_GROUPS";

const materialLabels: Record<StudyMaterial, string> = {
  VIDEO_LESSONS: "Videoaulas",
  BOOKS: "Livros",
  EXERCISE_LISTS: "Listas de Exercícios",
  IN_PERSON: "Presencial",
  ONLINE_COURSES: "Cursos Online",
  PODCASTS: "Podcasts",
  ARTICLES: "Artigos",
  FLASHCARDS: "Flashcards",
  PRACTICE_TESTS: "Testes Práticos",
  STUDY_GROUPS: "Grupos de Estudo",
};

interface StudyUserPreferencesDTO {
  materialPreferred: StudyMaterial;
  maxTime: number;
}

interface StudyUserPreferences extends StudyUserPreferencesDTO {
  id: number;
}

export default function UserPreferencesPage() {
  const { token, isAuthenticated } = useAuthStore(); // Pega o estado de autenticação

  const [materialPreferred, setMaterialPreferred] = useState<StudyMaterial>("VIDEO_LESSONS");
  const [maxTime, setMaxTime] = useState<string>("");

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
          "http://localhost:8080/api/study-preferences",
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

        const data: StudyUserPreferences = await response.json();

        // Preenche o formulário com os dados recebidos do backend
        setMaterialPreferred(data.materialPreferred || "VIDEO_LESSONS");
        setMaxTime(data.maxTime ? data.maxTime.toString() : "");
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

    const maxTimeAsNumber = parseInt(maxTime, 10);
    if (isNaN(maxTimeAsNumber) || maxTimeAsNumber <= 0) {
      toast.error("Por favor, insira um tempo máximo válido.");
      return;
    }

    if (!isAuthenticated || !token) {
      toast.error("Usuário não autenticado. Faça login para salvar.");
      return;
    }

    setIsSaving(true);
    const body: StudyUserPreferencesDTO = {
      materialPreferred: materialPreferred,
      maxTime: maxTimeAsNumber,
    };

    try {
      const response = await fetch(
        "http://localhost:8080/api/study-preferences",
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

  // Função auxiliar para validar a entrada de tempo máximo
  const handleMaxTimeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    if (/^\d*$/.test(value) && value.length <= 3) { // Limita a 3 dígitos para tempo máximo
      setMaxTime(value);
    }
  };

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
            {/* Campo Material Preferido */}
            <div>
              <label
                htmlFor="material"
                className="block text-xl font-semibold mb-2 text-[var(--text-main)]"
              >
                Material de Estudo Preferido
              </label>
              <select
                id="material"
                value={materialPreferred}
                onChange={(e) =>
                  setMaterialPreferred(e.target.value as StudyMaterial)
                }
                className="p-2 border border-gray-300 rounded-lg w-full"
              >
                {Object.entries(materialLabels).map(([key, label]) => (
                  <option key={key} value={key}>
                    {label}
                  </option>
                ))}
              </select>
            </div>

            {/* Campo Tempo Máximo por Sessão */}
            <div>
              <label
                htmlFor="maxTime"
                className="block text-xl font-semibold mb-2 text-[var(--text-main)]"
              >
                Tempo Máximo por Sessão (minutos)
              </label>
              <input
                id="maxTime"
                type="text"
                inputMode="numeric"
                value={maxTime}
                onChange={handleMaxTimeChange}
                className="p-2 border border-gray-300 rounded-lg w-full"
                placeholder="Ex: 60"
                required
              />
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
