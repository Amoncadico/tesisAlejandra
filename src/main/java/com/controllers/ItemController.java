package com.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import com.models.Item;
import com.repository.EjercicioRepository;
import com.repository.ItemRepository;
import com.repository.RutinaRepository;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/item")
public class ItemController {

    private final ItemRepository itemRepository;
    private final EjercicioRepository ejercicioRepository;
    private final RutinaRepository rutinaRepository;

    public ItemController(ItemRepository itemRepository, EjercicioRepository ejercicioRepository, RutinaRepository rutinaRepository) {
        this.itemRepository = itemRepository;
        this.ejercicioRepository = ejercicioRepository;
        this.rutinaRepository = rutinaRepository;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Item>> listarItems() {
        try {
            List<Item> items = itemRepository.findAll();
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> itemPorId(@PathVariable Long id) {
        Item item = itemRepository.findById(id).orElse(null);
        return item != null ?
                        ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    @PostMapping("/crear")
    public ResponseEntity<Item> crearItem(@RequestBody Item item) {
        // Resolver referencias a Ejercicio y Rutina por id si vienen en el payload
        if (item.getEjercicio() != null && item.getEjercicio().getId() != null) {
            ejercicioRepository.findById(item.getEjercicio().getId()).ifPresent(item::setEjercicio);
        }
        if (item.getRutina() != null && item.getRutina().getId() != null) {
            rutinaRepository.findById(item.getRutina().getId()).ifPresent(item::setRutina);
        }

        Item nuevoItem = itemRepository.save(item);
        return new ResponseEntity<>(nuevoItem, HttpStatus.CREATED);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Item> actualizarItem(@PathVariable Long id, @RequestBody Item itemActualizado) {
        return itemRepository.findById(id)
                .map(item -> {

                    if (itemActualizado.getSeries() > 0) {
                        item.setSeries(itemActualizado.getSeries());
                    }
                    if (itemActualizado.getRepeticiones() > 0) {
                        item.setRepeticiones(itemActualizado.getRepeticiones());
                    }
                    if (itemActualizado.getDuracion() != null) {
                        item.setDuracion(itemActualizado.getDuracion());
                    }
                    if (itemActualizado.getObservaciones() != null && !itemActualizado.getObservaciones().isEmpty()) {
                        item.setObservaciones(itemActualizado.getObservaciones());
                    }
                    if (itemActualizado.getEjercicio() != null && itemActualizado.getEjercicio().getId() != null) {
                        ejercicioRepository.findById(itemActualizado.getEjercicio().getId()).ifPresent(item::setEjercicio);
                    }
                    if (itemActualizado.getRutina() != null && itemActualizado.getRutina().getId() != null) {
                        rutinaRepository.findById(itemActualizado.getRutina().getId()).ifPresent(item::setRutina);
                    }

                    Item actualizado = itemRepository.save(item);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<HttpStatus> eliminarItem(@PathVariable Long id) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        itemRepository.delete(item);
        return ResponseEntity.noContent().build();
    }


    
}
