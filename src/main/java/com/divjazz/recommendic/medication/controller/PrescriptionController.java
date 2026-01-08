package com.divjazz.recommendic.medication.controller;

import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.medication.controller.payload.PrescriptionRequest;
import com.divjazz.recommendic.medication.controller.payload.PatientPrescriptionResponse;
import com.divjazz.recommendic.medication.controller.payload.PrescriptionResponse;
import com.divjazz.recommendic.medication.service.PrescriptionService;
import com.divjazz.recommendic.user.dto.PatientMedicalData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/prescription")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;

    @GetMapping
    ResponseEntity<Response<Set<PrescriptionResponse>>> getPrescriptions() {
        var response = getResponse(prescriptionService.getPrescriptions(), HttpStatus.OK);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    ResponseEntity<Response<PatientPrescriptionResponse>> getPrescriptionById(@PathVariable String id) {
        var response = getResponse(prescriptionService.getPrescriptionById(id), HttpStatus.OK);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    ResponseEntity<Response<PrescriptionResponse>> createPrescription(@RequestBody @Valid PrescriptionRequest request) {
        var response = getResponse(prescriptionService.createPrescription(request), HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/today")
    ResponseEntity<Response<Set<PatientPrescriptionResponse>>> getTodayPrescriptions() {
        var response = getResponse(prescriptionService.getTodayPrescription(), HttpStatus.OK);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/patient/{id}")
    ResponseEntity<Response<PatientMedicalData>> getPatientMedicalData(@PathVariable String id) {
        var response = getResponse(prescriptionService.getPatientMedicalData(id), HttpStatus.OK);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient")
    public ResponseEntity<Response<Set<PatientMedicalData>>> getPatientMedicalDataFromOngoingConsultations() {
        var response = getResponse(prescriptionService.getPatientMedicalDataFromOngoingConsultations(), HttpStatus.OK);
        return ResponseEntity.ok(response);
    }

}
