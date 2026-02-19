package com.king.peace.Entitys;

import java.time.LocalDateTime;

import org.hibernate.annotations.Table;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class ReponsePlainte {
    @Id @GeneratedValue
    private Long id;

    private String reponse;
    private LocalDateTime dateReponse = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "plainte_id")
    private Plaintes plainte;
}
