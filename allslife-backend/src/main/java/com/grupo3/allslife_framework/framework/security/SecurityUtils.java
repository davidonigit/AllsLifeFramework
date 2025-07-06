package com.grupo3.allslife_framework.framework.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.grupo3.allslife_framework.framework.model.User;
import com.grupo3.allslife_framework.framework.repository.UserRepository;

@Component
public class SecurityUtils {

    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Obtém o ID do usuário autenticado atualmente
     * @return ID do usuário atual
     * @throws UsernameNotFoundException se o usuário não for encontrado
     * @throws SecurityException se não houver usuário autenticado
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new SecurityException("Nenhum usuário autenticado encontrado");
        }
        
        // Se o principal já for seu objeto User completo
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getId();
        }
        
        // Caso contrário, busque o usuário pelo identificador usado no login
        String identifier = authentication.getName();
        User user = userRepository.findByEmail(identifier)  // Suponho que você usa email para login
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + identifier));
        
        return user.getId();
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getPrincipal());
    }
}
