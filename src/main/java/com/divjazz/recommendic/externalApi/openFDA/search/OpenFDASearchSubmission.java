package com.divjazz.recommendic.externalApi.openFDA.search;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record OpenFDASearchSubmission (
        String submission_type,
        String submission_number,
        String submission_status,
        String submission_status_date,
        String submission_class_code,
        String submission_class_description,
        List<OpenFDASearchSubmissionApplicationDocs> application_docs
){}
