package com.incture.taskmanagement.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Role {
    @Id
    private Long id;
    private String name;
}
