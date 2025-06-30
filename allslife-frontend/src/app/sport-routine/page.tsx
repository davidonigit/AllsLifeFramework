/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { useState, useEffect } from "react";
import { useAuthStore } from "@/stores/auth/auth-store";
import { toast } from "sonner";
import ReactMarkdown from "react-markdown";

// Esportes disponíveis
export const sportsList = [
  "",
  "Futebol",
  "Basquete",
  "Corrida",
  "Natação",
  "Ciclismo",
  "Yoga",
  "Tênis",
  "Vôlei",
  "Musculação",
  "Boxe",
];

// Converter strings do enum para nome do dia
export const dayOfWeekEnum: Record<string, string> = {
  SUNDAY: "Domingo",
  MONDAY: "Segunda-feira",
  TUESDAY: "Terça-feira",
  WEDNESDAY: "Quarta-feira",
  THURSDAY: "Quinta-feira",
  FRIDAY: "Sexta-feira",
  SATURDAY: "Sábado",
};

export const sortedWeeklyAvailability = [
  "SUNDAY",
  "MONDAY",
  "TUESDAY",
  "WEDNESDAY",
  "THURSDAY",
  "FRIDAY",
  "SATURDAY",
];

// Tipos para dados de disponibilidade
export interface DailyAvailability {
  id: number;
  dayOfWeek: string;
  morningAvailable: boolean;
  afternoonAvailable: boolean;
  eveningAvailable: boolean;
}

export interface SportRoutine {
  sportName: string;
  weeklyAvailability: DailyAvailability[];
  generatedRoutine: string;
}

export default function SportRoutinePage() {
  const [selectedSport, setSelectedSport] = useState<string>(sportsList[0]);
  const [weeklyAvailability, setWeeklyAvailability] = useState<
    DailyAvailability[]
  >([]);
  const [generatedRoutine, setGeneratedRoutine] = useState<string>("");
  const [routineUpdateTrigger, setRoutineUpdateTrigger] = useState(0);

  const [isLoadingRoutine, setIsLoadingRoutine] = useState<boolean>(true);
  const [routineError, setRoutineError] = useState<string | null>(null);

  const { token, isAuthenticated } = useAuthStore();

  useEffect(() => {
    async function fetchData() {
      try {
        setIsLoadingRoutine(true); // Inicia o carregamento da rotina
        setRoutineError(null); // Limpa erros anteriores

        // Verifica se o token existe antes de fazer a requisição
        if (!isAuthenticated || !token) {
          setRoutineError("Usuário não autenticado ou token ausente.");
          setIsLoadingRoutine(false);
          return;
        }

        const response = await fetch(
          "http://localhost:8080/api/sport-routine",
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
            errorData.message || `HTTP error! status: ${response.status}`
          );
        }

        const data: SportRoutine = await response.json();
        console.log("Dados recebidos:", data);
        setSelectedSport(data.sportName);
        setGeneratedRoutine(data.generatedRoutine);
        console.log("Rotina gerada:", data.generatedRoutine);
        const sortedAvailability = data.weeklyAvailability.sort((a, b) => {
          const indexA = sortedWeeklyAvailability.indexOf(a.dayOfWeek);
          const indexB = sortedWeeklyAvailability.indexOf(b.dayOfWeek);
          return indexA - indexB;
        });
        setWeeklyAvailability(sortedAvailability);
      } catch (error: any) {
        console.error("Erro ao buscar dados:", error);
        setRoutineError(error.message || "Não foi possível carregar a rotina.");
      } finally {
        setIsLoadingRoutine(false);
      }
    }
    fetchData();
  }, [token, isAuthenticated, routineUpdateTrigger]);

  function toggleAvailability(dayIndex: number, time: keyof DailyAvailability) {
    setWeeklyAvailability((prev) => {
      const updated = prev.map((item, index) =>
        index === dayIndex ? { ...item, [time]: !item[time] } : item
      );
      return updated;
    });
  }

  async function saveSchedule() {
    console.log("Salvando rotina com os dados:", {
      sport: selectedSport,
      weeklyAvailability,
    });
    if (selectedSport === "" || selectedSport === null) {
      toast.error("Por favor, selecione um esporte.");
      return;
    }
    try {
      if (!isAuthenticated || !token) {
        toast.error("Usuário não autenticado. Faça login para salvar.");
        return;
      }
      const response = await fetch("http://localhost:8080/api/sport-routine", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ sport: selectedSport, weeklyAvailability }),
      });
      if (response.ok) {
        toast.success("Rotina salva com sucesso!");
      } else {
        toast.error("Erro ao salvar rotina.");
      }
    } catch (error: any) {
      toast.error("Erro de conexão.", {
        description: error,
      });
    }
  }

  async function generateRoutine() {
    try {
      setIsLoadingRoutine(true);
      setRoutineError(null);

      if (!isAuthenticated || !token) {
        toast.error("Usuário não autenticado. Faça login para gerar a rotina.");
        setIsLoadingRoutine(false);
        return;
      }

      const response = await fetch(
        "http://localhost:8080/api/sport-routine/generate",
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(
          `Erro ao gerar rotina: ${response.status} - ${
            errorData || "Resposta vazia"
          }`
        );
      }

      toast.success("Rotina gerada com sucesso!");
      setRoutineUpdateTrigger((prev) => prev + 1);
    } catch (error: any) {
      console.error("Erro ao gerar rotina:", error);
      setRoutineError(error.message || "Não foi possível gerar a rotina.");
      toast.error(error.message || "Erro ao gerar a rotina.");
    } finally {
      setIsLoadingRoutine(false);
      (document.querySelector("textarea") as HTMLTextAreaElement).value = "";
    }
  }

  async function submitFeedback() {
    //Pegar o feedback da textarea
    const feedback = (document.querySelector("textarea") as HTMLTextAreaElement)
      .value;
    if (!feedback || feedback.trim() === "") {
      toast.error("Por favor, escreva um feedback antes de enviar.");
      return;
    }
    try {
      setIsLoadingRoutine(true);
      setRoutineError(null);

      if (!isAuthenticated || !token) {
        toast.error("Usuário não autenticado. Faça login para gerar a rotina.");
        setIsLoadingRoutine(false);
        return;
      }
      console.log("Enviando feedback:", feedback);
      const response = await fetch(
        "http://localhost:8080/api/sport-routine/feedback",
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ feedback }),
        }
      );

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(
          `Erro ao gerar rotina: ${response.status} - ${
            errorData || "Resposta vazia"
          }`
        );
      }

      toast.success("Rotina gerada com sucesso!");
      setRoutineUpdateTrigger((prev) => prev + 1);
    } catch (error: any) {
      console.error("Erro ao gerar rotina:", error);
      setRoutineError(error.message || "Não foi possível gerar a rotina.");
      toast.error(error.message || "Erro ao gerar a rotina.");
    } finally {
      setIsLoadingRoutine(false);
    }
  }

  return (
    <div className="min-h-screen bg-[var(--gray-bg)] pt-8 p-4 flex flex-col items-center">
      <div className="mb-8">
        {" "}
        <label className="text-xl font-semibold mb-2 block section-header">
          Escolha seu esporte:
        </label>
        <select
          value={selectedSport}
          onChange={(e) => setSelectedSport(e.target.value)}
          className="p-2 border border-gray-300 rounded-lg w-full"
        >
          {sportsList.map((sport) => (
            <option key={sport} value={sport}>
              {sport}
            </option>
          ))}
        </select>
      </div>

      <div className="flex flex-col w-4/5 items-center">
        <h1>Sua Rotina</h1>
        <table className="w-full max-w-sm mb-6 table-fixed-layout">
          <thead>
            <tr>
              <th className="p-2"></th>
              {weeklyAvailability.map((item) => (
                <th key={item.id} className="p-2 text-center">
                  {dayOfWeekEnum[item.dayOfWeek as keyof typeof dayOfWeekEnum]}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {["morningAvailable", "afternoonAvailable", "eveningAvailable"].map(
              (time) => (
                <tr key={time}>
                  <td className="p-2 text-center font-medium">
                    {time === "morningAvailable"
                      ? "Manhã"
                      : time === "afternoonAvailable"
                      ? "Tarde"
                      : "Noite"}
                  </td>
                  {weeklyAvailability.map((item, index) => (
                    <td key={index} className="p-2 text-center">
                      <button
                        onClick={() =>
                          toggleAvailability(
                            index,
                            time as keyof DailyAvailability
                          )
                        }
                        className={`${
                          item[time as keyof DailyAvailability]
                            ? "bg-green-500"
                            : "bg-red-500"
                        } p-2 rounded`}
                      >
                        {item[time as keyof DailyAvailability]
                          ? "Disponível"
                          : "Ocupado"}
                      </button>
                    </td>
                  ))}
                </tr>
              )
            )}
          </tbody>
        </table>

        <button onClick={saveSchedule} className="secondary-button mb-6">
          {" "}
          Salvar Rotina
        </button>
      </div>

      <h1>Treino Personalizada</h1>
      <div className="w-4/5 max-w-sm p-4 rounded-lg bg-[var(--form-white)]">
        <div className="w-full h-64 p-3 border border-[var(--gray-border)] rounded-md bg-[var(--gray-border)] text-[var(--text-main)] overflow-y-auto overflow-x-auto">
          {isLoadingRoutine ? (
            <p className="text-center">Carregando rotina...</p>
          ) : routineError ? (
            <p className="text-center text-red-500">{routineError}</p>
          ) : generatedRoutine ? (
            <ReactMarkdown>{generatedRoutine}</ReactMarkdown>
          ) : (
            <p className="text-center">
              Nenhuma rotina gerada ainda. Salve sua disponibilidade para gerar
              uma.
            </p>
          )}
        </div>
        <button onClick={generateRoutine} className="secondary-button mb-6">
          {" "}
          Gerar Treino Personalizado
        </button>
      </div>
      <h1>Enviar feedback sobre a Rotina de Treino</h1>
      <div className="w-4/5 max-w-sm p-4 rounded-lg bg-[var(--form-white)]">
        <textarea
          className="w-full max-w-sm p-2 border border-gray-300 rounded-lg mb-4"
          placeholder="Escreva seu feedback aqui..."
          rows={2}
          maxLength={300}
        ></textarea>

        <button onClick={submitFeedback} className="secondary-button mb-6">
          {" "}
          Enviar Feedback e Criar Nova Rotina
        </button>
      </div>
    </div>
  );
}
