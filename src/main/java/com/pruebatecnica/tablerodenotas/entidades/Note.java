package com.pruebatecnica.tablerodenotas.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table (name = "notes")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 

public class Note {
    @Id 
    @GeneratedValue (strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    
     @Column(nullable = false)
    private String title;

    @Lob
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotesStatus status;

    @Column(nullable = false)
    private double positionX;

    @Column(nullable = false)
    private double positionY;

}
