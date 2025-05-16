package com.divjazz.recommendic.fileupload.controller;

import com.divjazz.recommendic.fileupload.service.FileService;
import com.divjazz.recommendic.fileupload.utils.FileResponseFile;
import com.divjazz.recommendic.fileupload.utils.FileResponseMessage;
import com.divjazz.recommendic.user.service.GeneralUserService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * This is a REST Controller that deals with file upload and download as pertains to the users.
 */

@RestController
@RequestMapping("api/v1/file/")
@Profile("test")
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
    public ResponseEntity<FileResponseMessage> uploadProfilePics(@RequestParam(value = "user_id") String userid,
                                                                 @RequestParam(value = "file") MultipartFile multipartFile) {

        return null;
    }

    @PutMapping(value = "consultant/certification",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<FileResponseMessage> uploadCertification(@RequestParam(value = "consultant_id") String id,
                                                                   @RequestParam(value = "certificate_type") String certificateTypeString,
                                                                   @RequestParam(value = "file") MultipartFile multipartFile) {
        return null;
    }

    @GetMapping(value = "consultant/certification",
            params = "article_id")
    public ResponseEntity<List<FileResponseFile>> getCertificationByConsultantId(@RequestParam("consultant_id") String id) {
        //TODO: Implement retrieval of certification from S3 Bucket
        return null;
    }

    /**
     * This method serves as a download link for the certificate which can be used by the admin or Consultant
     *
     * @param certificate_id this represents the certificate id gotten from the previous request
     * @return a Response Entity that contains the byte array data of the file
     */
    @GetMapping(value = "consultant/certification")
    public ResponseEntity<byte[]> getCertificateFile(@RequestParam("certificate_id") String certificate_id) {
        //TODO:
        return null;
    }

    @GetMapping(value = "user/profile_pics_info")
    public ResponseEntity<FileResponseFile> getProfilePicsInfoByUserId(@RequestParam("user_id") String id) {
        return null;
    }

    @GetMapping(value = "user/profile_pics")
    public ResponseEntity<byte[]> getProfilePics(@RequestParam("profile_pics_id") String profile_pics_id) {
        return null;
    }


}
