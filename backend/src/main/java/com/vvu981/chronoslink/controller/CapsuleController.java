package com.vvu981.chronoslink.controller;

import com.vvu981.chronoslink.dto.CapsuleDTO;
import com.vvu981.chronoslink.dto.CapsuleFilter;
import com.vvu981.chronoslink.model.Capsule;
import com.vvu981.chronoslink.service.CapsuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/capsules") // Ruta base para todos los métodos de esta clase
public class CapsuleController {

    private final CapsuleService capsuleService;

    public CapsuleController(CapsuleService capsuleService) {
        this.capsuleService = capsuleService;
    }

    @GetMapping
    public ResponseEntity<List<Capsule>> getCapsules(
            @RequestHeader("X-User-Id") UUID ownerId, CapsuleFilter filter) {

        List<Capsule> results = capsuleService.findCapsules(ownerId, filter);
        return ResponseEntity.ok(results); // Devuelve 200 OK con la lista
    }

    @PostMapping
    public ResponseEntity<Capsule> createCapsule(
            @RequestHeader("X-User-Id") UUID ownerId, @RequestBody Capsule capsule) { // Transforma el JSON del cuerpo en un objeto Java

        Capsule created = capsuleService.createCapsule(capsule, ownerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Capsule> deleteCapsule(@RequestHeader("X-User-Id") UUID ownerId, @PathVariable UUID capsuleId) {
        Capsule capsuleDeleted = capsuleService.deleteCapsule(capsuleId, ownerId);

        return ResponseEntity.ok(capsuleDeleted);
    }

    @PutMapping("/{capsuleId}")
    public ResponseEntity<Capsule> editCapsule(@RequestHeader("X-User-Id") UUID ownerId, @PathVariable UUID capsuleId, @RequestBody Capsule newCapsule) {
        Capsule capsuleEdited = capsuleService.editCapsule(newCapsule, ownerId);

        newCapsule.setId(capsuleId);

        return ResponseEntity.ok(capsuleEdited);
    }

    @PostMapping("/{capsuleId}/open")
    public ResponseEntity<CapsuleDTO> openCapsule(@RequestHeader("X-User-Id") UUID ownerId, @PathVariable UUID capsuleId) {
        Capsule openedCapsule = capsuleService.openCapsule(capsuleId, ownerId);

        CapsuleDTO response = new CapsuleDTO(openedCapsule);

        return ResponseEntity.ok(response);
    }
}