package com.grupo3.allslife_framework.service;

import java.util.Map;

import org.json.JSONArray; // Importar do org.json
import org.json.JSONObject; // Importar do org.json

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class FachadaLLM {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApikey;

    private final WebClient webClient;

    public FachadaLLM(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String chat(String prompt) {

        Map<String, Object> requestBody = Map.of(
            "contents", new Object[] {
                Map.of("parts", new Object[] {
                    Map.of("text", prompt)
                })}
        );

        String apiResponseJson = null;
        try {
            apiResponseJson = webClient.post()
                .uri(geminiApiUrl + geminiApikey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            if (apiResponseJson != null && !apiResponseJson.isEmpty()) {
                JSONObject jsonResponse = new JSONObject(apiResponseJson);

                if (jsonResponse.has("candidates")) {
                    JSONArray candidates = jsonResponse.getJSONArray("candidates");
                    if (candidates.length() > 0) {
                        JSONObject firstCandidate = candidates.getJSONObject(0);

                        if (firstCandidate.has("content")) {
                            JSONObject content = firstCandidate.getJSONObject("content");

                            if (content.has("parts")) {
                                JSONArray parts = content.getJSONArray("parts");
                                if (parts.length() > 0) {
                                    JSONObject firstPart = parts.getJSONObject(0);

                                    if (firstPart.has("text")) {
                                        return firstPart.getString("text"); // Retorna o texto extraído
                                    }
                                }
                            }
                        }
                    }
                }
                System.err.println("Estrutura JSON inesperada na resposta do Gemini: " + apiResponseJson);
                return "Erro: Formato de resposta do Gemini inválido.";

            } else {
                return "Erro: Resposta vazia ou nula da API do Gemini.";
            }

        } catch (RuntimeException e) {
            System.err.println("Erro ao parsear a resposta JSON do Gemini: " + e.getMessage());
            return "Erro ao processar a resposta da API do Gemini.";
        } catch (Exception e) {
            System.err.println("Erro ao se comunicar com a API do Gemini: " + e.getMessage());
            return "Erro ao se comunicar com a API do Gemini.";
        }
    }
}