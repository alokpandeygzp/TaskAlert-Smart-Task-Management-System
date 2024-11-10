package com.incture.taskmanagement.repositories;

import com.incture.taskmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    // Find users by their role name (e.g., "ADMIN_USER")
    List<User> findByRoles_Name(String roleName);
}
