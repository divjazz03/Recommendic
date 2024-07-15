package com.divjazz.recommendic.utils.fileUpload;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class FileUploadExceptionAdvice extends ResponseEntityExceptionHandler {
    
    public ResponseEntity<FileResponseMessage> handleMaxSizeException(MaxUploadSizeExceededException exceededException){
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new FileResponseMessage("File too large!"));
    }
}
