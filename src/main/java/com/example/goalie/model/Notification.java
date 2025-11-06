package com.example.goalie.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private Date date;

    @OneToMany(mappedBy = "notification",cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();
}
