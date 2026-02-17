package com.king.peace.ImplementServices;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.king.peace.Dao.*;
import com.king.peace.Entitys.*;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class UserServiceImpl {

    @Autowired private UserRepository userRepository;

    @Autowired private RoleRepository roleRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        user.setActive(false);
        return userRepository.save(user);
    }

    public User unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        user.setActive(true);
        return userRepository.save(user);
    }

private Role getRoleOrThrow(ERole roleName) {
    return roleRepository.findByName(roleName)
            .orElseThrow(() -> new RuntimeException("Rôle " + roleName + " introuvable"));
}

public User assignRoles(Long userId, Set<ERole> roleNames) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

    Set<Role> roles = roleNames.stream()
            .map(this::getRoleOrThrow)
            .collect(Collectors.toSet());

    user.getRoles().addAll(roles);
    return userRepository.save(user);
}

}
