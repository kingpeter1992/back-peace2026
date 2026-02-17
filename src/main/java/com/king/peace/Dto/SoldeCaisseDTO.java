package com.king.peace.Dto;

import java.time.LocalDateTime;

import com.king.peace.Entitys.Devise;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SoldeCaisseDTO {
    private Double soldeActuel;
    private LocalDateTime dateDerniereOperation;
    private Devise devise;

}
