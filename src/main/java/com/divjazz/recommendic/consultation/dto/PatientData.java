package com.divjazz.recommendic.consultation.dto;

import com.divjazz.recommendic.user.enums.Gender;

import java.util.List;
import java.util.Map;

public record PatientData(
        String name,
        int age,
        Gender gender,
        List<String> allergies,
        List<String> conditions,
        String lastVisit,
        String insurance,
        RecordedVitals lastRecordedVitals,
        List<ConnectedDevice> connectedDevices,
        PatientReported patientReported
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

