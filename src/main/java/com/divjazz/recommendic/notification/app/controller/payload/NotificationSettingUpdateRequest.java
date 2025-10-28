package com.divjazz.recommendic.notification.app.controller.payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PatientNotificationSettingUpdateRequest.class, name = "PATIENT"),
        @JsonSubTypes.Type(value = ConsultantNotificationSettingUpdateRequest.class, name = "CONSULTANT")
})
public sealed interface NotificationSettingUpdateRequest permits PatientNotificationSettingUpdateRequest, ConsultantNotificationSettingUpdateRequest{
}
