package com.king.peace.Web;

import java.util.List;

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

import com.king.peace.Dto.AvanceSalaireDTO;
import com.king.peace.ImplementServices.AvanceSalaireServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/avance-salaire")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AvanceSalaireController {

    private final AvanceSalaireServiceImpl service;

    @PostMapping
    public ResponseEntity<AvanceSalaireDTO> create(@RequestBody AvanceSalaireDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<AvanceSalaireDTO>> findAll() {
        return ResponseEntity.ok(service.findAllGardien());
    }

    @PutMapping("/{id}/valider")
    public ResponseEntity<AvanceSalaireDTO> valider(@PathVariable Long id) {
        return ResponseEntity.ok(service.valider(id));
    }

    @PutMapping("/{id}/deduire")
    public ResponseEntity<?> deduire(@PathVariable Long id) {
        return ResponseEntity.ok(service.deduire(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
public ResponseEntity<AvanceSalaireDTO> update(@PathVariable Long id, @RequestBody AvanceSalaireDTO dto) {
    return ResponseEntity.ok(service.update(id, dto));
}
}