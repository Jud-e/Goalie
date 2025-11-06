package com.example.goalie.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Messaging {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private Timestamp timestamp;

    @OneToMany(mappedBy = "message",cascade = CascadeType.ALL)
    private List<Messaging> messages = new ArrayList<>();
}
