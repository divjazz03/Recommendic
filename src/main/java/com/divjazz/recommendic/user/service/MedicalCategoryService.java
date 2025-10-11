package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.repository.MedicalCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalCategoryService {
    private final MedicalCategoryRepository medicalCategoryRepository;

    public Set<MedicalCategory> getAllMedicalCategories() {
        return medicalCategoryRepository
                .findAll()
                .stream()
                .map(medicalCategoryEntity ->
                        new MedicalCategory(medicalCategoryEntity.getName(), medicalCategoryEntity.getDescription()))
                .collect(Collectors.toSet());
    }

    public MedicalCategoryEntity getMedicalCategoryByName(String name) {
        return medicalCategoryRepository
                .findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Medical category not found"));
    }

    public Set<MedicalCategoryEntity> getAllByNames(List<String> medicalCategoryNames) {
        return medicalCategoryRepository.findAllByNameIn(medicalCategoryNames);
    }
}
