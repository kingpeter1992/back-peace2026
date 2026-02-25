package com.king.peace.Dao.dashDto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class DashboardGlobalDTO {

  // Contrats
  private long totalContrats;
  private long contratsActifs;
  private long contratsExpires;

  // Clients
  private long totalClients;
  private long clientsActifs;

  // Affectations
  private long totalAffectations;
  private long affectationsActives;
  private long gardiensNonAffectes;     // optionnel (si table gardien)
  private long contratsSansGardien;     // optionnel

  // Plaintes
  private long totalPlaintes;
  private long plaintesOuvertes;
  private long plaintesCloturees;
  private double moyenneNote;

  // Facturation
  private long totalFactures;
  private long facturesEmises;
  private long facturesPaid;
  private long facturesPartial;
  private double caTotal;
  private double impayeTotal;

  // Caisse
  private double encaissementTotal;
  private double decaissementTotal;

  // Soldes (si tu as solde avant/après ou session)
  private double soldeUSD;
  private double soldeCDF;
}