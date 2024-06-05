package com.divjazz.recommendic.user.controller;

import com.divjazz.recommendic.user.exceptions.NoSuchCertificateException;
import com.divjazz.recommendic.user.enums.CertificateType;
import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import com.divjazz.recommendic.user.service.FileService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.utils.fileUpload.ResponseFile;
import com.divjazz.recommendic.utils.fileUpload.ResponseMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * This is a REST Controller that deals with file upload and download as pertains to the users.
 */

@RestController
@RequestMapping("api/v1/file/")
public class FileController {
    private final FileService fileService;
    private final GeneralUserService userService;

    public FileController(FileService fileService, GeneralUserService userService) {
        this.fileService = fileService;

        this.userService = userService;
    }

    @PutMapping(value = "user/profile_pics",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<ResponseMessage> uploadProfilePics(@RequestParam(value = "user_id") String userid, @RequestParam(value = "file") MultipartFile multipartFile){
        StringBuilder message = new StringBuilder();
        try{
            fileService.storeProfilePicture(multipartFile, new UserId(UUID.fromString(userid)));
        } catch (IOException e){
            message.append("Could not upload the file: ")
                    .append(multipartFile.getOriginalFilename())
                    .append("!");
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseMessage(message.toString()));
        }
        message.append("Uploaded the file successfully: ")
                .append(multipartFile.getOriginalFilename());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ResponseMessage(message.toString()));
    }
    @PutMapping(value = "consultant/certification",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ResponseMessage> uploadCertification(@RequestParam(value = "consultant_id")String id,
                                                               @RequestParam(value = "certificate_type") String certificateTypeString,
                                                               @RequestParam(value = "file") MultipartFile multipartFile){
        String message = "";
        CertificateType certificateType = switch (certificateTypeString.toUpperCase()){
            case "RESUME" -> CertificateType.RESUME;
            case "UNI_CERTIFICATE" -> CertificateType.UNI_CERTIFICATE;
            case null, default -> throw new NoSuchCertificateException();
        };
        try{
            fileService.storeCertificate(multipartFile, new UserId(UUID.fromString(id)), certificateType);
        } catch (Exception e){
            message = "Could not upload the file: " + multipartFile.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));

        }
        message = "Uploaded the file successfully: " + multipartFile.getOriginalFilename();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseMessage(message));
    }

    @GetMapping(value = "consultant/certification",
        params = "consultant_id")
    public ResponseEntity<List<ResponseFile>> getCertificationByConsultantId(@RequestParam("consultant_id") String id){
        List<ResponseFile> files = fileService
                .getAllCertificationsByConsultantId(new UserId(UUID.fromString(id)))
                .stream().map(file -> {
                    String fileDownloadUri = ServletUriComponentsBuilder
                            .fromCurrentContextPath()
                            .path("consultant/certification/")
                            .path(file.getId().asString())
                            .toUriString();

                    return new ResponseFile(file.getFileName(),
                            fileDownloadUri,
                            MediaType.APPLICATION_PDF_VALUE,
                            file.getFileContent().length);
                }).toList();
        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    /**
     * This method serves as a download link for the certificate which can be used by the admin or Consultant
     * @param certificate_id this represents the certificate id gotten from the previous request
     * @return a Response Entity that contains the byte array data of the file
     */
    @GetMapping(value = "consultant/certification")
    public ResponseEntity<byte[]> getCertificateFile(@RequestParam("certificate_id") String certificate_id){
        Certification certification = fileService.getCertificationById(certificate_id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + certification.getFileName() + "\"" )
                .body(certification.getFileContent());
    }
    @GetMapping(value = "user/profile_pics_info")
    public ResponseEntity<ResponseFile> getProfilePicsInfoByUserId(@RequestParam("user_id") String id){
        ProfilePicture profilePicture= fileService.getProfilePictureByUserId(id);

        String fileDownloadUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("api/v1/file/user/profile_pics/")
                .path(profilePicture.getId().asString())
                .toUriString();
        ResponseFile responseFile = new ResponseFile(profilePicture.getName(),
                fileDownloadUrl,
                MediaType.IMAGE_JPEG_VALUE,
                profilePicture.getPictureData().length);
        return ResponseEntity.status(HttpStatus.OK).body(responseFile);
    }

    @GetMapping(value = "user/profile_pics/{profile_pics_id}")
    public ResponseEntity<byte[]> getProfilePics(@PathVariable("profile_pics_id") String profile_pics_id){
        ProfilePicture profilePicture = fileService.getProfilePictureByProfilePictureId(profile_pics_id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + profilePicture.getName() + "\"")
                .body(profilePicture.getPictureData());
    }



}
