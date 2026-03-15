package com.king.peace.Web;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.king.peace.Dao.GardienPhotoRepository;
import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dao.PointageRepository;
import com.king.peace.Dto.AffectationDto;
import com.king.peace.Dto.GardienDetailsDto;
import com.king.peace.Dto.GardienDto;
import com.king.peace.Dto.GardienPresenceSalaireDto;
import com.king.peace.Dto.GardienStatsDto;
import com.king.peace.Dto.PointageDto;
import com.king.peace.Dto.PointageMasseDTO;
import com.king.peace.Dto.PresenceDto;
import com.king.peace.Entitys.Affectation;
import com.king.peace.Entitys.Contrats;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.GardienPhoto;
import com.king.peace.Entitys.Pointage;
import com.king.peace.Entitys.StatutPointage;
import com.king.peace.ImplementServices.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gardiens")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GardienController {

    private final GardienService gardienService;
    private final GardienPhotoRepository gardienPhotoRepository;
    private final ObjectMapper objectMapper;
    private final PresenceService pointagerepo;
    private final AffectationService affectationService;


    // ===========================
    // CRUD GARDIEN
    // ===========================
    @PostMapping("/create")
    public ResponseEntity<?> createGardien(
            @RequestPart("gardien") String gardienJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {

        try {
            Gardien gardien = objectMapper.readValue(gardienJson, Gardien.class);

            // Sauvegarder le gardien d'abord
            Gardien savedGardien = gardienService.create(gardien);

            // Sauvegarder la photo dans la table GardienPhoto
            if (photo != null && !photo.isEmpty()) {
                GardienPhoto gardienPhoto = new GardienPhoto();
                gardienPhoto.setGardien(savedGardien);
                gardienPhoto.setNomFichier(photo.getOriginalFilename());
                gardienPhoto.setPhoto(photo.getBytes());
                gardienPhotoRepository.save(gardienPhoto);
            }
            GardienDto dto = new GardienDto(savedGardien, null);
            dto.setId(savedGardien.getId());
            dto.setNom(savedGardien.getNom());
            dto.setPrenom(savedGardien.getPrenom());
            dto.setEmail(savedGardien.getEmail());
            dto.setFonction(savedGardien.getFonction());
            dto.setTelephone1(savedGardien.getTelephone1());
            dto.setTelephone2(savedGardien.getTelephone2());
            dto.setAdresse(savedGardien.getAdresse());
            dto.setGenre(savedGardien.getGenre());
            dto.setStatut(savedGardien.getStatut());
            dto.setSalaireBase(savedGardien.getSalaireBase());
            dto.setDevise(savedGardien.getDevise());
            dto.setDateEmbauche(savedGardien.getDateEmbauche());
            dto.setCreatedAt(savedGardien.getCreatedAt());
            dto.setDepartement(savedGardien.getDepartement());
            dto.setFonction(savedGardien.getFonction());
            dto.setSite(savedGardien.getSite());
            dto.setNbrjours(savedGardien.getNbrjours());


            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

 @GetMapping("/presences-salaires/periode")
    public List<GardienPresenceSalaireDto> getPresenceSalaireParPeriode(
            @RequestParam LocalDate dateDebut,
            @RequestParam LocalDate dateFin
    ) {
        return pointagerepo.getPresenceSalaireParPeriode(dateDebut, dateFin);
    }

    // ===========================
    // UPDATE GARDIEN
    // ===========================
    @PutMapping("/update/{id}")
    @Transactional // 🔑 essentiel pour manipuler le LOB
    public ResponseEntity<?> updateGardien(
            @PathVariable Long id,
            @RequestPart("gardien") String gardienJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {

        try {
            // 1️⃣ Récupérer le gardien existant

            Gardien existingGardien = gardienService.findById(id);
            if (existingGardien == null) {
                return ResponseEntity.notFound().build();
            }

            // 2️⃣ Mapper les nouvelles valeurs
            Gardien updatedGardien = objectMapper.readValue(gardienJson, Gardien.class);

            existingGardien.setNom(updatedGardien.getNom());
            existingGardien.setPrenom(updatedGardien.getPrenom());
            existingGardien.setEmail(updatedGardien.getEmail());
            existingGardien.setFonction(updatedGardien.getFonction());
            existingGardien.setTelephone1(updatedGardien.getTelephone1());
            existingGardien.setTelephone2(updatedGardien.getTelephone2());
            existingGardien.setAdresse(updatedGardien.getAdresse());
            existingGardien.setGenre(updatedGardien.getGenre());
            existingGardien.setStatut(updatedGardien.getStatut());
            existingGardien.setSalaireBase(updatedGardien.getSalaireBase());
            existingGardien.setDevise(updatedGardien.getDevise());
            existingGardien.setDateEmbauche(updatedGardien.getDateEmbauche());
            existingGardien.setDepartement(updatedGardien.getDepartement());
            existingGardien.setFonction(updatedGardien.getFonction());
            existingGardien.setSite(updatedGardien.getSite());
            existingGardien.setNbrjours(updatedGardien.getNbrjours());
            

            // 3️⃣ Sauvegarder les modifications
            Gardien savedGardien = gardienService.update(id, existingGardien);

            // 4️⃣ Mettre à jour la photo si nécessaire
            if (photo != null && !photo.isEmpty()) {
                // Chercher l'ancienne photo
                GardienPhoto existingPhoto = gardienPhotoRepository.findByGardienId(savedGardien.getId())
                        .orElse(new GardienPhoto());

                existingPhoto.setGardien(savedGardien);
                existingPhoto.setNomFichier(photo.getOriginalFilename());
                existingPhoto.setPhoto(photo.getBytes());

                gardienPhotoRepository.save(existingPhoto); // insert ou update automatiquement

            }

            // 5️⃣ Retourner DTO
            GardienDto dto = new GardienDto(savedGardien, null);
            dto.setId(savedGardien.getId());
            dto.setNom(savedGardien.getNom());
            dto.setPrenom(savedGardien.getPrenom());
            dto.setEmail(savedGardien.getEmail());
            dto.setFonction(savedGardien.getFonction());
            dto.setTelephone1(savedGardien.getTelephone1());
            dto.setTelephone2(savedGardien.getTelephone2());
            dto.setAdresse(savedGardien.getAdresse());
            dto.setGenre(savedGardien.getGenre());
            dto.setStatut(savedGardien.getStatut());
            dto.setSalaireBase(savedGardien.getSalaireBase());
            dto.setDevise(savedGardien.getDevise());
            dto.setDateEmbauche(savedGardien.getDateEmbauche());
            dto.setCreatedAt(savedGardien.getCreatedAt());
               dto.setDepartement(savedGardien.getDepartement());
            dto.setFonction(savedGardien.getFonction());
            dto.setSite(savedGardien.getSite());
            dto.setNbrjours(savedGardien.getNbrjours());

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/with-photo")
    public List<GardienDto> getAllGardiensWithPhoto() {
        return gardienService.findAllWithPhoto();
    }

    @GetMapping("/stats")
    public ResponseEntity<GardienStatsDto> getStats() {
        return ResponseEntity.ok(gardienService.getStats());
    }

    @GetMapping
    public ResponseEntity<List<Gardien>> getAllGardiens() {
        return ResponseEntity.ok(gardienService.findAll());
    }

    // ===========================
    // dernier taux d'echange
    // ===========================
    @GetMapping("/taux/dernier")
    public ResponseEntity<Double> getDernierTaux() {
        return ResponseEntity.ok(gardienService.getDernierTaux());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Gardien> getGardien(@PathVariable Long id) {
        return ResponseEntity.ok(gardienService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Gardien> updateGardien(@PathVariable Long id, @RequestBody Gardien gardien) {
        return ResponseEntity.ok(gardienService.update(id, gardien));
    }

    @PatchMapping("/bloquer/{id}")
    public ResponseEntity<Gardien> bloquerGardien(@PathVariable Long id) {
        return ResponseEntity.ok(gardienService.bloquerGardien(id));
    }

    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<?> deleteGardien1(@PathVariable Long id) {

        try {
            gardienService.delete(id);
            return ResponseEntity.ok().build();
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Suppression impossible : dépendance existante");
        }
    }

    // ===========================
    // AFFECTATION CONTRAT
    // ===========================
    @PostMapping("/{gardienId}/affectation")
    public ResponseEntity<Affectation> affecterGardien(
            @PathVariable Long gardienId,
            @RequestBody Contrats contrat) {
        return ResponseEntity.ok(gardienService.affecterGardien(gardienId, contrat));
    }

    // ===========================
    // POINTAGE
    // ===========================
    @PostMapping("/{gardienId}/pointage")
    public ResponseEntity<Pointage> pointerGardien(
            @PathVariable Long gardienId,
            @RequestParam LocalDate date,
            @RequestParam StatutPointage statut) {
        return ResponseEntity.ok(gardienService.pointerGardien(gardienId, date, statut));
    }

    @GetMapping("/{gardienId}/pointages")
    public ResponseEntity<List<Pointage>> getPointages(@PathVariable Long gardienId) {
        return ResponseEntity.ok(gardienService.getPointages(gardienId));
    }

    @GetMapping("pointageAll")
    public List<?> getAllPointages() {
        return pointagerepo.getAllPointages();
    }

    // Optionnel : filtrer par gardien
    @GetMapping("/gardien/{id}")
    public List<PresenceDto> getPresencesByGardien(@PathVariable Long id) {
        return pointagerepo.getPointagesByGardien(id);
    }

    @PostMapping("createpointage")
    public ResponseEntity<?> createPointage(@RequestBody PointageDto pointageDto) {
        try {

            pointagerepo.savePointage(pointageDto);

             // renvoyer un objet JSON au lieu d'une simple chaîne
        return ResponseEntity.ok(Map.of("message", "Pointage enregistré"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de l'enregistrement");
        }

    }


    private final PointageRepository pointageRepository;
    private final GardienRepository gardienRepository;


  @PostMapping("/pointage/masse")
public ResponseEntity<List<Pointage>> pointerGardiensMasse(
        @RequestBody List<PointageMasseDTO> dtoList) {

    List<Pointage> pointages = new ArrayList<>();

    for (PointageMasseDTO dto : dtoList) {
        Gardien g = gardienRepository.findById(dto.getGardienId())
                .orElseThrow(() -> new RuntimeException("Gardien non trouvé"));
               
        //deduidre le  moi et l'année a partir de la date 
            Integer mois = dto.getDate().getMonthValue();
            Integer annee = dto.getDate().getYear();

        Pointage p = Pointage.builder()
                .gardien(g)
                .date(dto.getDate())
                .datesortie(dto.getDatesortie())
                .heureEntree(dto.getHeureEntree())
                .heureSortie(dto.getHeureSortie())
                .statut(dto.getStatuses())
                .mois(mois)
                .annee(annee)
                .build();

        pointages.add(p);
    }

    return ResponseEntity.ok(pointageRepository.saveAll(pointages));
}

@PostMapping("/import-excel")
public ResponseEntity<?> importExcel(@RequestBody List<GardienDto> gardiens) {
    gardienService.importerDepuisExcel(gardiens);
    return ResponseEntity.ok(Map.of("message", "Importation réussie"));
}

   
  @GetMapping("/detailsgardien/{id}")
  public GardienDetailsDto getGardienDetails(@PathVariable Long id,
                                             @RequestParam LocalDate dateFrom,
                                             @RequestParam LocalDate dateTo) {
      return gardienService.getGardienDetails(id, dateFrom, dateTo);
  }


     @GetMapping("/actifs")
    public List<Gardien> getActiveGardiens() {
        return gardienRepository.findByActifTrue();
    }

      // ================= GET ALL =================
    @GetMapping("/affectations")
    public ResponseEntity<List<AffectationDto>> getAllAffectations() {
        List<AffectationDto> affectations = affectationService.findAllAffectation();
        return ResponseEntity.ok(affectations);
    }

    // ================= GET BY ID =================
    @GetMapping("/getaffectationbyid/{id}")
    public ResponseEntity<AffectationDto> getAffectation(@PathVariable Long id) {
        AffectationDto affectation = affectationService.findById(id);
        return ResponseEntity.ok(affectation);
    }

    // ================= CREATE / AFFECT =================
    @PostMapping("/createaffectation")
    public ResponseEntity<AffectationDto> createAffectation(@RequestBody AffectationDto dto) {
        AffectationDto created = affectationService.affecterGardien(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ================= UPDATE =================
    @PutMapping("/updateaffectation/{id}")
    public ResponseEntity<AffectationDto> updateAffectation(@PathVariable Long id,
                                                            @RequestBody AffectationDto dto) {
        AffectationDto updated = affectationService.updateAffectation(id, dto);
        return ResponseEntity.ok(updated);
    }

    // ================= DELETE =================
    @DeleteMapping("/deleteaffectation/{id}")
    public ResponseEntity<Void> deleteAffectation(@PathVariable Long id) {
        affectationService.deleteAffectation(id);
        return ResponseEntity.noContent().build();
    }
  
}
