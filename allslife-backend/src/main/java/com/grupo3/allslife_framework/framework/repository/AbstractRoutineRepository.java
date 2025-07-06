package com.grupo3.allslife_framework.framework.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.grupo3.allslife_framework.framework.model.AbstractRoutine;

/**
 * Interface de repositório base para todas as entidades de Rotina.
 * * @param <T> O tipo da entidade de rotina, que deve estender AbstractRoutine.
 */
@NoRepositoryBean // ANOTAÇÃO CRÍTICA!
public interface AbstractRoutineRepository<T extends AbstractRoutine> extends JpaRepository<T, Long> {

    Optional<T> findByUserId(Long userId);
}