package com.divjazz.recommendic.appointment.controller.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AppointmentCancellationRequest(@NotNull @Size(max = 40) String reason){}
