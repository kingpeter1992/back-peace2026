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

import com.king.peace.Dto.PrimeDTO;
import com.king.peace.ImplementServices.PrimeServiceImpl;
import com.king.peace.Interfaces.PrimeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/primes")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PrimeController {

    private final PrimeServiceImpl service;

    @PostMapping
    public ResponseEntity<PrimeDTO> create(@RequestBody PrimeDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrimeDTO> update(@PathVariable Long id, @RequestBody PrimeDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<PrimeDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/{id}/valider")
    public ResponseEntity<PrimeDTO> valider(@PathVariable Long id) {
        return ResponseEntity.ok(service.valider(id));
    }

    @PutMapping("/{id}/payer")
    public ResponseEntity<PrimeDTO> payer(@PathVariable Long id) {
        return ResponseEntity.ok(service.payer(id));
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<PrimeDTO> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(service.annuler(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}