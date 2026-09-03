package com.divjazz.recommendic.consultation.dto;

import com.divjazz.recommendic.user.enums.BloodType;
import com.divjazz.recommendic.user.enums.Gender;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record PatientData(
        String name,
        String age,
        Gender gender,
        String email,
        String phoneNumber,
        List<String> allergies,
        List<String> conditions,
        String lastVisit,
        String insurance,
        RecordedVitals lastRecordedVitals,
        List<ConnectedDevice> connectedDevices,
        PatientReported patientReported,
        BloodType bloodType
) {
    public record RecordedVitals(
            String bloodPressure,
            String heartRate,
            String temperature,
            String weight,
            String recordedDate,
            String recordedBy
    ) {
    }
    public record PatientReported(
            String painLevel,
            List<String> symptoms,
            String duration,
            String triggers
    ) {
    }

    public record ConnectedDevice(String type, String lastSync, Map<String, String> other){}
}

