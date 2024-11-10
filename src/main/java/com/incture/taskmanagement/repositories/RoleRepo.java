package com.incture.taskmanagement.repositories;

import com.incture.taskmanagement.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Integer> {

}
