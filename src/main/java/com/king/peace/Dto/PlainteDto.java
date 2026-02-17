package com.king.peace.Dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class PlainteDto {
    public PlainteDto(Long id2, LocalDate datePlaite2, String description2, String response2, LocalDate dateRespnse2,
			String statut2, LocalDate createdAt2) {
                this.id=id2;
                this.datePlaite=datePlaite2;
                this.description=description2;
                this.response=response2;
                this.dateRespnse=dateRespnse2;
                this.statut=statut2;
                this.createdAt=createdAt2;
            }
            public PlainteDto() {   
            }
	private Long id;
    private LocalDate datePlaite;
    private String description;
    private String response;
    private LocalDate dateRespnse;
    private String statut;
    private LocalDate createdAt;

    
}
