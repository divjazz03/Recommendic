package com.divjazz.recommendic.appointment.controller.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConsultationFee(@JsonProperty("in_person") int inPerson, @JsonProperty("online") int online){}
