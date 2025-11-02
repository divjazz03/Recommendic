package com.divjazz.recommendic.security.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.security.controller.payload.UserSecuritySettingUpdateRequest;
import com.divjazz.recommendic.security.dto.UserSecuritySettingDTO;
import com.divjazz.recommendic.security.model.ConsultantUserSecuritySetting;
import com.divjazz.recommendic.security.model.PatientUserSecuritySetting;
import com.divjazz.recommendic.security.repository.ConsultantUserSecuritySettingRepository;
import com.divjazz.recommendic.security.repository.PatientUserSecuritySettingRepository;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final PatientUserSecuritySettingRepository patientUserSecuritySettingRepository;
    private final ConsultantUserSecuritySettingRepository consultantUserSecuritySettingRepository;
    private final AuthUtils authUtils;
    @Value("${spring.session.timeout}")
    private Duration sessionTimeout;

    public UserSecuritySettingDTO createUserSetting(User user) {
        return switch (user.getUserType()) {
            case PATIENT -> {
                var userSecurity = new PatientUserSecuritySetting(
                        false,
                        (int) sessionTimeout.toMinutes(),
                        false,
                        (Patient) user
                );
                var savedUserSecurity = patientUserSecuritySettingRepository.save(userSecurity);

                yield new UserSecuritySettingDTO(
                        savedUserSecurity.isMultiFactorAuthEnabled(),
                        savedUserSecurity.getSessionTimeoutMin(),
                        savedUserSecurity.isLoginAlertsEnabled()
                );
            }
            case CONSULTANT -> {
                var userSetting = new ConsultantUserSecuritySetting(
                        true,
                        (int) sessionTimeout.toMinutes(),
                        false,
                        (Consultant) user
                );
                var savedUserSecuritySetting = consultantUserSecuritySettingRepository.save(userSetting);
                yield new UserSecuritySettingDTO(
                        savedUserSecuritySetting.isMultiFactorAuthEnabled(),
                        savedUserSecuritySetting.getSessionTimeoutMin(),
                        savedUserSecuritySetting.isLoginAlertsEnabled()
                );
            }
            case ADMIN -> null;
        };

    }

    @Transactional
    public UserSecuritySettingDTO updateUserSecuritySetting(UserSecuritySettingUpdateRequest updateRequest, HttpServletRequest httpServletRequest) {
        var user = authUtils.getCurrentUser();
        return switch (user.userType()) {
            case PATIENT -> {
                var patientSetting = patientUserSecuritySettingRepository.findByPatient_UserId(user.userId())
                        .orElseThrow(() -> new EntityNotFoundException("User Setting not found"));
                if (Objects.nonNull(updateRequest.multifactorAuthEnabled()) && Boolean.compare(patientSetting.isMultiFactorAuthEnabled(), updateRequest.multifactorAuthEnabled()) != 0) {
                    patientSetting.setMultiFactorAuthEnabled(updateRequest.multifactorAuthEnabled());
                }
                if (Objects.nonNull(updateRequest.loginAlertsEnabled()) && Boolean.compare(patientSetting.isLoginAlertsEnabled(), updateRequest.loginAlertsEnabled()) != 0) {
                    patientSetting.setMultiFactorAuthEnabled(updateRequest.multifactorAuthEnabled());
                }
                if (Objects.nonNull(updateRequest.sessionTimeoutMin()) && patientSetting.getSessionTimeoutMin() != updateRequest.sessionTimeoutMin()) {
                    patientSetting.setSessionTimeoutMin(updateRequest.sessionTimeoutMin());
                }
                patientSetting = patientUserSecuritySettingRepository.save(patientSetting);

                httpServletRequest.getSession().setMaxInactiveInterval(patientSetting.getSessionTimeoutMin() * 60);
                yield new UserSecuritySettingDTO(
                        patientSetting.isMultiFactorAuthEnabled(),
                        patientSetting.getSessionTimeoutMin(),
                        patientSetting.isLoginAlertsEnabled()
                );
            }
            case CONSULTANT -> {
                var consultantSetting = consultantUserSecuritySettingRepository.findByConsultant_UserId(user.userId())
                        .orElseThrow(() -> new EntityNotFoundException("User setting not found"));
                if (Objects.nonNull(updateRequest.multifactorAuthEnabled()) && Boolean.compare(consultantSetting.isMultiFactorAuthEnabled(), updateRequest.multifactorAuthEnabled()) != 0) {
                    consultantSetting.setMultiFactorAuthEnabled(updateRequest.multifactorAuthEnabled());
                }
                if (Objects.nonNull(updateRequest.loginAlertsEnabled()) && Boolean.compare(consultantSetting.isLoginAlertsEnabled(), updateRequest.loginAlertsEnabled()) != 0) {
                    consultantSetting.setMultiFactorAuthEnabled(updateRequest.multifactorAuthEnabled());
                }
                if (Objects.nonNull(updateRequest.sessionTimeoutMin()) && consultantSetting.getSessionTimeoutMin() != updateRequest.sessionTimeoutMin()) {
                    consultantSetting.setSessionTimeoutMin(updateRequest.sessionTimeoutMin());
                }
                consultantSetting = consultantUserSecuritySettingRepository.save(consultantSetting);
                httpServletRequest.getSession().setMaxInactiveInterval(consultantSetting.getSessionTimeoutMin() * 60);

                yield new UserSecuritySettingDTO(
                        consultantSetting.isMultiFactorAuthEnabled(),
                        consultantSetting.getSessionTimeoutMin(),
                        consultantSetting.isLoginAlertsEnabled()
                );
            }
            case ADMIN -> null;
        };
    }

    public UserSecuritySettingDTO getUserSecuritySetting() {
        var user = authUtils.getCurrentUser();
        return switch (user.userType()) {
            case PATIENT -> patientUserSecuritySettingRepository.findSecuritySettingByUserId(user.userId())
                    .orElseThrow(() -> new EntityNotFoundException("User Setting not found"));
            case CONSULTANT -> consultantUserSecuritySettingRepository.findSettingByUserId(user.userId())
                    .orElseThrow(() -> new EntityNotFoundException("User Setting not found"));
            case ADMIN -> null;
        };
    }
    public Integer getUserSessionExpiryDurationInMinutes(UserType userType, String userId) {
        return switch (userType) {
            case PATIENT -> patientUserSecuritySettingRepository.findByPatient_UserIdReturningSessionDurationINMinutes(userId);
            case CONSULTANT -> consultantUserSecuritySettingRepository.findByConsultant_UserIdReturningSessionDurationINMinutes(userId);
            case ADMIN -> 0;
        };
    }


}
