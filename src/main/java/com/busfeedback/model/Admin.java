package com.busfeedback.model;
import jakarta.persistence.*;
import lombok.Data;

@Data @Entity @Table(name = "sbhms_admin")
public class Admin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100) private String email;
    @Column(nullable = false, length = 100)               private String password;
    @Column(nullable = false, length = 100)               private String name;
}
