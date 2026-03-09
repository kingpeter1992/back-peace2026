package com.king.peace.Web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.king.peace.Dto.PretDTO;
import com.king.peace.ImplementServices.PretServiceImplement;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/prets")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PretsController {
    
   @Autowired PretServiceImplement servicePret;

    


    @PostMapping("/savePret")
    public PretDTO savePret(@RequestBody PretDTO dto) {
        return servicePret.save(dto);
    }

    @PutMapping("/{id}/udpatePret")
    public ResponseEntity<PretDTO> updatePrets(@PathVariable Long id,
                                               @RequestBody PretDTO updaDto){
        return ResponseEntity.ok(servicePret.saveUpdate(id, updaDto));
    }


 @PutMapping("/{id}/cloturer")
    public ResponseEntity<PretDTO> cloturePrets(@PathVariable Long id){
        return ResponseEntity.ok(servicePret.cloturerPret(id));
    }


    @GetMapping("/findAllPrets")
    public List<PretDTO> findAllPrets() {
        return servicePret.findAll();
    }

    @DeleteMapping("/deletePretById/{id}")
    public void delete(@PathVariable Long id) {
        servicePret.delete(id);
    }

    @GetMapping("/getById/{id}")
    public PretDTO getById(@PathVariable Long id) {
    
        return servicePret.getById(id);
    }
    
}
