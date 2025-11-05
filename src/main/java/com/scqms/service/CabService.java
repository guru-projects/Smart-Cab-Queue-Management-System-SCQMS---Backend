package com.scqms.service;

import com.scqms.entity.Cab;
import com.scqms.enums.CabStatus;
import com.scqms.repository.CabRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CabService {
    private final CabRepository cabRepository;

    public List<Cab> getAll() {
        return cabRepository.findAll();
    }

    public Cab getById(Long id) {
        return cabRepository.findById(id).orElseThrow(() -> new RuntimeException("Cab not found"));
    }

    public Cab assignNextAvailableCab() {
        return cabRepository.findFirstByStatus(CabStatus.AVAILABLE)
                .map(c -> {
                    c.setStatus(CabStatus.BUSY);
                    c.setLastUpdated(LocalDateTime.now());
                    return cabRepository.save(c);
                })
                .orElse(null);
    }

    public void save(Cab c) { cabRepository.save(c); }
}
