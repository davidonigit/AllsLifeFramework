"use client";

import { useState, useEffect, useCallback } from "react";
import { toast } from "sonner";
import { useAuthStore } from "@/stores/auth/auth-store";

export enum GoalStatus {
  TO_DO = "TO_DO",
  IN_PROGRESS = "IN_PROGRESS",
  DONE = "DONE",
}

export interface Goal {
  id: number;
  name: string;
  status: GoalStatus;
}

export interface GoalBoard {
  id: number;
  goals: Goal[];
}

interface GoalCardProps {
  goal: Goal;
  onEdit: (goal: Goal) => void;
  onDelete: (goalId: number) => void; // Alterado para number
}

const GoalCard: React.FC<GoalCardProps> = ({ goal, onEdit, onDelete }) => {
  return (
    <div className="bg-[var(--gray-border)] p-4 rounded-lg shadow-md mb-3 border border-gray-200 max-w-[80%] mx-auto">
      <h4 className="font-semibold text-lg text-gray-800 break-all">
        {goal.name}
      </h4>
      <div className="flex justify-center gap-2 mt-4">
        <button onClick={() => onEdit(goal)} className=" main-button flex-1">
          Editar
        </button>
        <button
          onClick={() => onDelete(goal.id)}
          className="secondary-button flex-1"
        >
          Excluir
        </button>
      </div>
    </div>
  );
};

interface EditGoalModalProps {
  goal: Goal | null; // A meta a ser editada, ou null se nenhum modal aberto
  onSave: (updatedGoal: Goal) => void;
  onClose: () => void;
}

const EditGoalModal: React.FC<EditGoalModalProps> = ({
  goal,
  onSave,
  onClose,
}) => {
  const [name, setName] = useState(goal ? goal.name : "");
  const [status, setStatus] = useState<GoalStatus>(
    goal ? goal.status : GoalStatus.TO_DO
  );

  useEffect(() => {
    if (goal) {
      setName(goal.name);
      setStatus(goal.status);
    }
  }, [goal]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!goal) return;

    onSave({ ...goal, name, status });
    // Não fecha o modal aqui, onSave irá fechar após a requisição
  };

  if (!goal) return null;

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white p-6 rounded-lg shadow-xl w-full max-w-md">
        <h3 className="text-2xl font-bold mb-4 text-gray-800">Editar Meta</h3>
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label
              htmlFor="goalName"
              className="block text-gray-700 text-sm font-bold mb-2"
            >
              Nome da Meta:
            </label>
            <input
              type="text"
              id="goalName"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              required
            />
          </div>
          <div className="mb-6">
            <label
              htmlFor="goalStatus"
              className="block text-gray-700 text-sm font-bold mb-2"
            >
              Status:
            </label>
            <select
              id="goalStatus"
              value={status}
              onChange={(e) => setStatus(e.target.value as GoalStatus)}
              className="shadow border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            >
              {Object.values(GoalStatus).map((s) => (
                <option key={s} value={s}>
                  {s
                    .replace(/_/g, " ")
                    .toLowerCase()
                    .split(" ")
                    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
                    .join(" ")}
                </option>
              ))}
            </select>
          </div>
          <div className="flex justify-end gap-3">
            <button type="submit" className="main-button">
              Salvar
            </button>
            <button
              type="button"
              onClick={onClose}
              className="secondary-button"
            >
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default function GoalBoardPage() {
  const [goals, setGoals] = useState<Goal[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editingGoal, setEditingGoal] = useState<Goal | null>(null);

  const { token, isAuthenticated } = useAuthStore(); // Obtém o token e o status de autenticação

  // Função para buscar todas as metas do backend (GET)
  const fetchGoals = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    if (!isAuthenticated || !token) {
      setError("Usuário não autenticado ou token ausente.");
      setIsLoading(false);
      return;
    }
    try {
      const response = await fetch("http://localhost:8080/api/goal-board", {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`, // Adiciona o token de autenticação
        },
      });
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(
          errorData.message || `Erro ao buscar metas: ${response.statusText}`
        );
      }
      const data: GoalBoard = await response.json();
      const goals: Goal[] = data.goals; // Garante que goals seja um array
      setGoals(goals);
    } catch (err: any) {
      setError(err.message || "Falha ao carregar metas.");
      toast.error(err.message || "Erro ao carregar metas.");
    } finally {
      setIsLoading(false);
    }
  }, [token, isAuthenticated]); // Dependências do useCallback

  // Carrega as metas na montagem do componente ou quando token/auth muda
  useEffect(() => {
    fetchGoals();
  }, [fetchGoals]);

  // --- Funções de Manipulação das Metas ---

  // Lida com a abertura do modal de edição
  const handleEdit = (goal: Goal) => {
    setEditingGoal(goal);
  };

  // Lida com o salvamento da meta editada (PUT)
  const handleSaveEdit = async (updatedGoal: Goal) => {
    if (!isAuthenticated || !token) {
      toast.error("Usuário não autenticado. Faça login para salvar.");
      return;
    }
    try {
      const response = await fetch(
        `http://localhost:8080/api/goal-board/${updatedGoal.id}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`, // Adiciona o token de autenticação
          },
          body: JSON.stringify(updatedGoal), // Envia a meta completa atualizada
        }
      );
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Erro ao salvar meta.");
      }
      setGoals((prevGoals) =>
        prevGoals.map((g) => (g.id === updatedGoal.id ? updatedGoal : g))
      );
      toast.success("Meta atualizada com sucesso!");
    } catch (err: any) {
      toast.error(err.message || "Erro ao atualizar meta.");
    } finally {
      setEditingGoal(null); // Fecha o modal após a requisição
    }
  };

  // Lida com a exclusão de uma meta (DELETE)
  const handleDelete = async (goalId: number) => {
    // Alterado para number
    if (!window.confirm("Tem certeza que deseja excluir esta meta?")) {
      return;
    }
    if (!isAuthenticated || !token) {
      toast.error("Usuário não autenticado. Faça login para excluir.");
      return;
    }
    try {
      const response = await fetch(
        `http://localhost:8080/api/goal-board/${goalId}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`, // Adiciona o token de autenticação
          },
        }
      );
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Erro ao excluir meta.");
      }
      setGoals((prevGoals) => prevGoals.filter((g) => g.id !== goalId));
      toast.success("Meta excluída com sucesso!");
    } catch (err: any) {
      toast.error(err.message || "Erro ao excluir meta.");
    }
  };

  // Lida com a adição de uma nova meta (POST)
  const handleAddGoal = async () => {
    const newGoalName = prompt("Nome da nova meta:");
    if (!newGoalName || newGoalName.trim() === "") {
      return; // Sai se o nome estiver vazio ou cancelado
    }
    if (!isAuthenticated || !token) {
      toast.error("Usuário não autenticado. Faça login para criar uma meta.");
      return;
    }
    try {
      const response = await fetch("http://localhost:8080/api/goal-board", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          name: newGoalName,
          status: GoalStatus.TO_DO,
        }),
      });
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Erro ao criar meta.");
      }
      const createdGoals: GoalBoard = await response.json();
      setGoals(createdGoals.goals);
      toast.success("Meta criada com sucesso!");
    } catch (err: any) {
      toast.error(err.message || "Erro ao criar meta.");
    }
  };

  // --- Renderização do Layout ---
  return (
    <div className="min-h-screen bg-gray-100 p-8 w-4/5 center mx-auto">
      <h1 className="text-4xl font-bold text-center mb-10 text-gray-800">
        Quadro de Metas
      </h1>

      <button
        onClick={handleAddGoal}
        className="main-button mb-8 mx-auto block"
      >
        Adicionar Nova Meta
      </button>

      {isLoading && (
        <p className="text-center text-gray-600 text-lg">Carregando metas...</p>
      )}
      {error && <p className="text-center text-red-600 text-lg">{error}</p>}
      {/* Exibe mensagem se não houver metas e não estiver carregando/erro */}
      {!isLoading && !error && goals.length === 0 && (
        <p className="text-center text-gray-600 text-lg">
          Nenhuma meta encontrada. Adicione uma nova!
        </p>
      )}

      {!isLoading && !error && goals.length > 0 && (
        <div className="flex md:flex-row justify-around">
          {/* Coluna "TO DO" */}
          <div className="w-full md:w-1/3 bg-gray-200 p-5 rounded-xl shadow-lg border-t-4 border-blue-500">
            <h2 className="text-2xl font-bold mb-5 text-gray-800 text-center">
              A Fazer (
              {goals.filter((g) => g.status === GoalStatus.TO_DO).length})
            </h2>
            {goals
              .filter((goal) => goal.status === GoalStatus.TO_DO)
              .map((goal) => (
                <GoalCard
                  key={goal.id}
                  goal={goal}
                  onEdit={handleEdit}
                  onDelete={handleDelete}
                />
              ))}
            {goals.filter((g) => g.status === GoalStatus.TO_DO).length ===
              0 && (
              <p className="text-center text-gray-500 italic mt-4">
                Nenhuma meta nesta coluna.
              </p>
            )}
          </div>

          {/* Coluna "IN PROGRESS" */}
          <div className="w-full md:w-1/3 bg-gray-200 p-5 rounded-xl shadow-lg border-t-4 border-yellow-500">
            <h2 className="text-2xl font-bold mb-5 text-gray-800 text-center">
              Em Progresso (
              {goals.filter((g) => g.status === GoalStatus.IN_PROGRESS).length})
            </h2>
            {goals
              .filter((goal) => goal.status === GoalStatus.IN_PROGRESS)
              .map((goal) => (
                <GoalCard
                  key={goal.id}
                  goal={goal}
                  onEdit={handleEdit}
                  onDelete={handleDelete}
                />
              ))}
            {goals.filter((g) => g.status === GoalStatus.IN_PROGRESS).length ===
              0 && (
              <p className="text-center text-gray-500 italic mt-4">
                Nenhuma meta nesta coluna.
              </p>
            )}
          </div>

          {/* Coluna "DONE" */}
          <div className="w-full md:w-1/3 bg-gray-200 p-5 rounded-xl shadow-lg border-t-4 border-green-500">
            <h2 className="text-2xl font-bold mb-5 text-gray-800 text-center">
              Concluído (
              {goals.filter((g) => g.status === GoalStatus.DONE).length})
            </h2>
            {goals
              .filter((goal) => goal.status === GoalStatus.DONE)
              .map((goal) => (
                <GoalCard
                  key={goal.id}
                  goal={goal}
                  onEdit={handleEdit}
                  onDelete={handleDelete}
                />
              ))}
            {goals.filter((g) => g.status === GoalStatus.DONE).length === 0 && (
              <p className="text-center text-gray-500 italic mt-4">
                Nenhuma meta nesta coluna.
              </p>
            )}
          </div>
        </div>
      )}

      {/* Modal de Edição (renderiza apenas se editingGoal não for null) */}
      <EditGoalModal
        goal={editingGoal}
        onSave={handleSaveEdit}
        onClose={() => setEditingGoal(null)}
      />
    </div>
  );
}
