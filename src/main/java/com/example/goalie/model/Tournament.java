package com.example.goalie.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Date startDate;
    private Date endDate;
    private String status;
    private String location;

    @OneToMany(mappedBy = "tournament",cascade = CascadeType.ALL)
    private List<User> participants = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<Match> matches = new ArrayList<>();
}
