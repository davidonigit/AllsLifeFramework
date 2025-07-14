/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { useState, useEffect } from "react";
import { useAuthStore } from "@/stores/auth/auth-store";
import { Notification } from "@/entities/notification.entity";
import ReactMarkdown from "react-markdown";

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [isLoadingNotification, setIsLoadingNotification] = useState<boolean>(true);
  const [notificationError, setNotificationError] = useState<string | null>(null);
  const [isExpandedNotifications, setIsExpandedNotifications] = useState<boolean>(false);

  const { token, isAuthenticated } = useAuthStore();

  useEffect(() => {
    async function fetchData() {
      try {
        setIsLoadingNotification(true);
        setNotificationError(null);

        if (!isAuthenticated || !token) {
          setNotificationError("Usuário não autenticado ou token ausente.");
          setIsLoadingNotification(false);
          return;
        }

        const response = await fetch(
          "http://localhost:8080/api/notification",
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

        const data: Notification[] = await response.json();
        setNotifications(data);
      } catch (error: any) {
        console.error("Erro ao buscar dados:", error);
        setNotificationError(error.message || "Não foi possível carregar a rotina.");
      } finally {
        setIsLoadingNotification(false);
      }
    }

    fetchData();
  }, [token, isAuthenticated]);

  return (
    <div className="min-h-screen bg-[var(--gray-bg)] pt-8 p-4 flex flex-col items-center">
      <div className="w-full flex flex-row justify-center items-center my-[8px]">
        <h1 className="text-2xl font-bold mb-4 mr-[12px]">Notificações</h1>

        <button
          className="mb-4 p-[12px] bg-[green] text-white rounded-[12px] hover:bg-[gray] border-[1px] transition"
          onClick={() => setIsExpandedNotifications((prev) => !prev)}
        >
          {isExpandedNotifications ? "Ocultar" : "Visualizar"}
        </button>
      </div>

      {isExpandedNotifications && (
        <div className="w-4/5 max-w-sm p-4 rounded-lg bg-[var(--form-white)]">
          <div className="w-full h-64 p-3 border border-[var(--gray-border)] rounded-md bg-[var(--gray-border)] text-[var(--text-main)] overflow-y-auto overflow-x-auto flex flex-col-reverse">
            {isLoadingNotification ? (
              <p className="text-center">Carregando notificações...</p>
            ) : notificationError ? (
              <p className="text-center text-red-500">{notificationError}</p>
            ) : notifications ? (
              notifications.map((notification, index) => (
                <div key={index} className="mb-[16px]">
                  <h2 className="text-[25px] mb-[12px] text-center">{notification.title}</h2>
                  <p className="text-center">Descrição:</p>
                  <p className="text-justify p-[16px]"> {notification.title.includes("a sua rotina esportiva") ? (
                    <ReactMarkdown>{notification.description}</ReactMarkdown>
                  ) : notification.description}</p>
                </div>
              ))
            ) : (
              <p className="text-center">Nenhuma notificação encontrada.</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
