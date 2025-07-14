"use client";

import React from "react";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import { useAuthStore } from "@/stores/auth/auth-store";

export default function Navbar() {
  const { isAuthenticated, logout, user } = useAuthStore();
  const router = useRouter();
  console.log("isAuthenticated:", isAuthenticated);
  console.log("user", user);

  const handleLogout = () => {
    logout();
    toast.success("Saiu com sucesso!");
    router.push("/");
  };

  const handleLoginRedirect = () => {
    router.push(`/login`);
  };

  return (
    <header
      className="w-full flex justify-between items-center"
      style={{
        color: "#ffffff",
        fontWeight: "bold",
        boxShadow: "0 1px 8px rgba(34, 197, 94, 0.12)",
        padding: "8px 24px",
      }}
    >
      <h1
        style={{
          fontSize: "2rem",
          letterSpacing: "0.05em",
        }}
        className="cursor-pointer"
        onClick={() => router.push("/")}
      >
        LanguagesLife
      </h1>
      <div>
        {isAuthenticated ? (
          <button
            onClick={handleLogout}
            style={{
              backgroundColor: "#ffffff",
              color: "#00a6ff",
              fontWeight: 600,
              padding: "8px 16px",
              borderRadius: "8px",
              border: "none",
              cursor: "pointer",
              transition: "background 0.2s",
            }}
            onMouseOver={(e) =>
              (e.currentTarget.style.backgroundColor = "#006297")
            }
            onMouseOut={(e) =>
              (e.currentTarget.style.backgroundColor = "#ffffff")
            }
          >
            Sair
          </button>
        ) : (
          <button
            onClick={handleLoginRedirect}
            style={{
              backgroundColor: "#ffffff",
              color: "#00a6ff",
              fontWeight: 600,
              padding: "8px 16px",
              borderRadius: "8px",
              border: "none",
              cursor: "pointer",
              transition: "background 0.2s",
            }}
            onMouseOver={(e) =>
              (e.currentTarget.style.backgroundColor = "#006297")
            }
            onMouseOut={(e) =>
              (e.currentTarget.style.backgroundColor = "#ffffff")
            }
          >
            Login
          </button>
        )}
      </div>
    </header>
  );
}
