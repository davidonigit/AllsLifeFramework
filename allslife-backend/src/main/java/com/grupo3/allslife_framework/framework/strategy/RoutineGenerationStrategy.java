package com.grupo3.allslife_framework.framework.strategy;

import com.grupo3.allslife_framework.framework.model.AbstractRoutine;

public interface RoutineGenerationStrategy<T extends AbstractRoutine> {
    void validateRoutineForGeneration(T routine);
    String buildGenerationPrompt(T routine, String availabilityString, String... feedback);
    void saveHistory(T routine);
    void sendSuccessNotification(T routine);
}
