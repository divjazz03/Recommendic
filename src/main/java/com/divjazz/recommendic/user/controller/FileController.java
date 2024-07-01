package com.divjazz.recommendic.user.controller;

import com.divjazz.recommendic.user.enums.CertificateType;
import com.divjazz.recommendic.user.exceptions.NoSuchCertificateException;
import com.divjazz.recommendic.user.service.FileService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.utils.fileUpload.ResponseFile;
import com.divjazz.recommendic.utils.fileUpload.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        //fileService.storeProfilePicture(UUID.fromString(userid));
        message.append("Uploaded the file successfully: ")
                .append(multipartFile.getOriginalFilename());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ResponseMessage(message.toString()));
    }
    /*@PutMapping(value = "consultant/certification",
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
            fileService.storeCertificate(multipartFile, UUID.fromString(id), certificateType);
        } catch (Exception e){
            message = "Could not upload the file: " + multipartFile.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));

        }
        message = "Uploaded the file successfully: " + multipartFile.getOriginalFilename();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseMessage(message));
    }*/

  /*  @GetMapping(value = "consultant/certification",
        params = "consultant_id")
    public ResponseEntity<List<ResponseFile>> getCertificationByConsultantId(@RequestParam("consultant_id") String id){
        //TODO: Implement retrieval of certification from S3 Bucket

    }*/

    /**
     * This method serves as a download link for the certificate which can be used by the admin or Consultant
     * @param certificate_id this represents the certificate id gotten from the previous request
     * @return a Response Entity that contains the byte array data of the file
     */
    /*@GetMapping(value = "consultant/certification")
    public ResponseEntity<byte[]> getCertificateFile(@RequestParam("certificate_id") String certificate_id) {
        //TODO:
    }*/
   /*
    @GetMapping(value = "user/profile_pics_info")
    public ResponseEntity<ResponseFile> getProfilePicsInfoByUserId(@RequestParam("user_id") String id){

    }*/
/*

    @GetMapping(value = "user/profile_pics/{profile_pics_id}")
    public ResponseEntity<byte[]> getProfilePics(@PathVariable("profile_pics_id") String profile_pics_id){

    }
*/



}
