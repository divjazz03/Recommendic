package com.divjazz.recommendic.user.controller;

import com.divjazz.recommendic.user.exceptions.NoSuchCertificateException;
import com.divjazz.recommendic.user.model.certification.CertificateType;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import com.divjazz.recommendic.user.service.CertificationService;
import com.divjazz.recommendic.user.utils.fileUpload.ResponseFile;
import com.divjazz.recommendic.user.utils.fileUpload.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/file/")
public class FileController {
    private final CertificationService certificationService;

    public FileController(CertificationService certificationService) {
        this.certificationService = certificationService;

    }

    @PostMapping(value = "consultant/certification",
            params = {"consultant_id", "certificate_type"},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ResponseMessage> uploadCertification(@RequestParam(value = "consultant_id")String id, @RequestParam(value = "certificate_type") String certificateTypeString, @RequestBody MultipartFile multipartFile){
        String message = "";
        CertificateType certificateType = switch (certificateTypeString.toUpperCase()){
            case "RESUME" -> CertificateType.RESUME;
            case "UNI_CERTIFICATE" -> CertificateType.UNI_CERTIFICATE;
            case null, default -> throw new NoSuchCertificateException();
        };
        try{
            certificationService.storeCertificate(multipartFile, new UserId(UUID.fromString(id)), certificateType);
        } catch (Exception e){
            message = "Could not upload the file: " + multipartFile.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));

        }
        message = "Uploaded the file successfully: " + multipartFile.getOriginalFilename();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseMessage(message));
    }
    @GetMapping(value = "consultant/certification", params = {"certificate_id"})
    public ResponseEntity<List<ResponseFile>> getCertificationByConsultantId(@RequestParam("certificate_id") String id){
        List<ResponseFile> files = certificationService
                .getAllCertificationsByConsultantId(new UserId(UUID.fromString(id)))
                .stream().map(file -> {
                    String fileDownloadUri = ServletUriComponentsBuilder
                            .fromCurrentContextPath()
                            .path("/consultant/certification/")
                            .path(file.getId().asString())
                            .toUriString();

                    return new ResponseFile(file.getFileName(),
                            fileDownloadUri,
                            file.getCertificateType().name(),
                            file.getFileContent().length);
                }).toList();
        return ResponseEntity.status(HttpStatus.OK).body(files);


    }

}
