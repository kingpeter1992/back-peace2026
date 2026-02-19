package com.king.peace.Dto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ClientDto {
    private Long id;
    private String nom;
    private String adresse;
    private String contact;
    private String contact2;
    private String email;
    private String typeClient;
    private boolean actif;
}