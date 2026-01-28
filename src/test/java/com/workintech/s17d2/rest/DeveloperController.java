package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    public Map<Integer, Developer> developers;

    private final Taxable taxable;

    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAll() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Developer> getById(@PathVariable Integer id) {
        Developer dev = developers.get(id);
        if (dev == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dev);
    }

    @PostMapping
    public ResponseEntity<Developer> add(@RequestBody Developer request) {
        if (request == null) return ResponseEntity.badRequest().build();

        int id = request.getId();
        String name = request.getName();
        double salary = request.getSalary();
        Experience exp = request.getExperience();

        if (name == null || exp == null) return ResponseEntity.badRequest().build();

        Developer created;

        if (exp == Experience.JUNIOR) {
            double netSalary = salary - (salary * taxable.getSimpleTaxRate() / 100.0);
            created = new JuniorDeveloper(id, name, netSalary);
        } else if (exp == Experience.MID) {
            double netSalary = salary - (salary * taxable.getMiddleTaxRate() / 100.0);
            created = new MidDeveloper(id, name, netSalary);
        } else {
            double netSalary = salary - (salary * taxable.getUpperTaxRate() / 100.0);
            created = new SeniorDeveloper(id, name, netSalary);
        }

        developers.put(created.getId(), created);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Developer> update(@PathVariable Integer id, @RequestBody Developer request) {
        if (request == null) return ResponseEntity.badRequest().build();

        developers.put(id, request);
        return ResponseEntity.ok(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        developers.remove(id);
        return ResponseEntity.ok("deleted");
    }
}