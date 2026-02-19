package com.king.peace.Web;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.king.peace.Dto.PlainteDto;
import com.king.peace.Dto.ReponseDto;
import com.king.peace.Entitys.ReponsePlainte;
import com.king.peace.ImplementServices.PlainteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/plaintes")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PlainteController {

    private final PlainteService plainteService;

    @PostMapping
    public ResponseEntity<PlainteDto> create(@RequestBody PlainteDto dto){
        return ResponseEntity.ok(plainteService.create(dto));
    }


    @PutMapping("/{id}")
    public ResponseEntity<PlainteDto> update(
            @PathVariable Long id,
            @RequestBody PlainteDto dto) {

        PlainteDto updated = plainteService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public List<PlainteDto> getAll(){
        return plainteService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        plainteService.delete(id);
    }

    @PutMapping("/{id}/reponse")
    public ResponseEntity<ReponseDto> repondre(@PathVariable Long id,
                                               @RequestBody ReponseDto reponse){
        return ResponseEntity.ok(plainteService.ajouterReponse(id, reponse));
    }

@GetMapping("/{id}/reponses")
public ResponseEntity<List<ReponsePlainte>> getReponses(@PathVariable Long id) {
    return ResponseEntity.ok(plainteService.listerReponses(id));
}

// ================= GET ALL =================
    @GetMapping("all-plainte-response")
    public ResponseEntity<List<PlainteDto>> getAllPlaiteWithRepose() {
        return ResponseEntity.ok(plainteService.getAllPlainteWithResponse());
    }

@GetMapping("byid/{id}")
public ResponseEntity<PlainteDto> getOne(@PathVariable Long id){
    return ResponseEntity.ok(plainteService.getRespnseById(id));
}



}