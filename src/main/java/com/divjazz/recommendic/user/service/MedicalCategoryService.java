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
                        new MedicalCategory(medicalCategoryEntity.getName(), medicalCategoryEntity.getMedicalCategoryId(), medicalCategoryEntity.getDescription(),medicalCategoryEntity.getIcon()))
                .collect(Collectors.toSet());
    }

    public MedicalCategoryEntity getMedicalCategoryByName(String name) {
        return medicalCategoryRepository
                .findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Medical category not found"));
    }
    public MedicalCategoryEntity getMedicalCategoryById(String medicalCategoryId) {
        return medicalCategoryRepository
                .findByMedicalCategoryId(medicalCategoryId)
                .orElseThrow(() -> new EntityNotFoundException("Medical category not found"));
    }

    public Set<MedicalCategoryEntity> getAllByNames(Set<String> medicalCategoryNames) {
        return medicalCategoryRepository.findAllByMedicalCategoryIdIn(medicalCategoryNames);
    }
    public Set<MedicalCategoryEntity> getAllByIds(Set<String> medicalCategoryIds) {
        return medicalCategoryRepository.findAllByMedicalCategoryIdIn(medicalCategoryIds);
    }
}
