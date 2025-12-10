package com.divjazz.recommendic.dashboard.controller;

import com.divjazz.recommendic.dashboard.controller.payload.DashboardResponse;
import com.divjazz.recommendic.dashboard.service.DashboardService;
import com.divjazz.recommendic.global.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController {


    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<Response<DashboardResponse>> getDashboard() {
        var result = dashboardService.getUserDashBoard();

        return ResponseEntity.ok(getResponse(result, HttpStatus.OK));
    }


}
