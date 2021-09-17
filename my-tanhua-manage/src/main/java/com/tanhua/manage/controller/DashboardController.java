package com.tanhua.manage.controller;

import com.tanhua.manage.service.DashboardService;
import com.tanhua.manage.vo.Summary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("summary")
    public ResponseEntity<Summary> querySummary(@RequestHeader("Authorization") String token){
        try {
            Summary summary =  dashboardService.querySummary();
            if (summary !=null){
                return ResponseEntity.ok(summary);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
