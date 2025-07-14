/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { useState, useEffect } from "react";
import { useAuthStore } from "@/stores/auth/auth-store";
import ReactMarkdown from "react-markdown";
import { SportRoutine } from "../sport-routine/page";

export default function HistoryPage() {
  const [generatedRoutine, setGeneratedRoutine] = useState<string>("");
  const [routineHistory, setRoutineHistory] = useState<SportRoutine[]>([]);
  const [isLoadingRoutine, setIsLoadingRoutine] = useState<boolean>(true);
  const [routineError, setRoutineError] = useState<string | null>(null);
  const [isExpandedRoutine, setIsExpandedRoutine] = useState<boolean>(false);
  const [isExpandedHistory, setIsExpandedHistory] = useState<boolean>(false);

  const { token, isAuthenticated } = useAuthStore();

  useEffect(() => {
    async function fetchData() {
      try {
        setIsLoadingRoutine(true);
        setRoutineError(null);

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
        setGeneratedRoutine(data.generatedRoutine);
      } catch (error: any) {
        console.error("Erro ao buscar dados:", error);
        setRoutineError(error.message || "Não foi possível carregar a rotina.");
      } finally {
        setIsLoadingRoutine(false);
      }
    }

    async function fetchHistoryData() {
      try {
        setIsLoadingRoutine(true);
        setRoutineError(null);

        if (!isAuthenticated || !token) {
          setRoutineError("Usuário não autenticado ou token ausente.");
          setIsLoadingRoutine(false);
          return;
        }

        const response = await fetch(
          "http://localhost:8080/api/sport-routine/history",
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

        const data: SportRoutine[] = await response.json();
        setRoutineHistory(data);
      } catch (error: any) {
        console.error("Erro ao buscar dados:", error);
        setRoutineError(error.message || "Não foi possível carregar a rotina.");
      } finally {
        setIsLoadingRoutine(false);
      }
    }

    fetchData();
    fetchHistoryData();
  }, [token, isAuthenticated]);

  return (
    <div className="min-h-screen bg-[var(--gray-bg)] pt-8 p-4 flex flex-col items-center">
      <div className="w-full flex flex-row justify-center items-center my-[8px]">
        <h1 className="text-2xl font-bold mb-4 mr-[12px]">Treino Atual</h1>

        <button
          className="mb-4 p-[12px] bg-[green] text-white rounded-[12px] hover:bg-[gray] border-[1px] transition"
          onClick={() => setIsExpandedRoutine((prev) => !prev)}
        >
          {isExpandedRoutine ? "Ocultar" : "Visualizar"}
        </button>
      </div>

      {isExpandedRoutine && (
        <div className="w-4/5 max-w-sm p-4 rounded-lg bg-[var(--form-white)]">
          <div className="w-full h-64 p-3 border border-[var(--gray-border)] rounded-md bg-[var(--gray-border)] text-[var(--text-main)] overflow-y-auto overflow-x-auto">
            {isLoadingRoutine ? (
              <p className="text-center">Carregando rotina...</p>
            ) : routineError ? (
              <p className="text-center text-red-500">{routineError}</p>
            ) : generatedRoutine ? (
              <ReactMarkdown>{generatedRoutine}</ReactMarkdown>
            ) : (
              <p className="text-center">Nenhuma rotina gerada.</p>
            )}
          </div>
        </div>
      )}

      <div className="w-full flex flex-row justify-center items-center my-[8px]">
        <h1 className="text-2xl font-bold mb-4 mr-[12px]">Treinos Anteriores</h1>

        <button
          className="mb-4 p-[12px] bg-[green] text-white rounded-[12px] hover:bg-[gray] border-[1px] transition"
          onClick={() => setIsExpandedHistory((prev) => !prev)}
        >
          {isExpandedHistory ? "Ocultar" : "Visualizar"}
        </button>
      </div>

      {isExpandedHistory && (
        <div className="w-4/5 max-w-sm p-4 rounded-lg bg-[var(--form-white)]">
          <div className="w-full h-64 p-3 border border-[var(--gray-border)] rounded-md bg-[var(--gray-border)] text-[var(--text-main)] overflow-y-auto overflow-x-auto">
            {isLoadingRoutine ? (
              <p className="text-center">Carregando histórico...</p>
            ) : routineError ? (
              <p className="text-center text-red-500">{routineError}</p>
            ) : routineHistory ? (
              routineHistory.map((routine, index) => (
                <div key={index} className="mb-4">
                  <h2 className="text-[25px] mb-[12px] text-center">Treino {index + 1} ({routine.sportName})</h2>
                  <ReactMarkdown>{routine.generatedRoutine}</ReactMarkdown>
                </div>
              ))
            ) : (
              <p className="text-center">Nenhum histórico encontrado.</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
