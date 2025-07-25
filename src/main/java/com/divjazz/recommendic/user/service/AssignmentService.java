package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.model.Assignment;
import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;


    public Set<Assignment> retrieveAllAssignmentByAdminId(String adminId) {
        return assignmentRepository.getAllByAdminAssigned_UserId(adminId);
    }

    public Set<Certification> retrieveAllCertificationByAssignmentId(Long id) {
        return assignmentRepository.getAllCertificationByAssignmentId(id);
    }
}
