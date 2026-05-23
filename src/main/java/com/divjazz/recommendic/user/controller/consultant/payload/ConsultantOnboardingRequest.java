package com.divjazz.recommendic.user.controller.consultant.payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ProfileInformationRequest.class, name = "profile"),
        @JsonSubTypes.Type(value = QualificationsRequest.class, name = "qualifications"),
        @JsonSubTypes.Type(value = PracticeDetails.class, name = "practice"),
        @JsonSubTypes.Type(value = ProfessionalInfoRequest.class, name = "professional")
})
public sealed interface ConsultantOnboardingRequest permits
        ProfileInformationRequest,
        QualificationsRequest,
        PracticeDetails, ProfessionalInfoRequest{
}
