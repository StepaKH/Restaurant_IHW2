package org.example.controllers

import org.example.data.controllers.functionCheck.HealthCheckResponseData
import org.example.statuses.ApplicationStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController {
    @GetMapping("/health-check - (Состояние системы")
    fun healthCheck(): ResponseEntity<HealthCheckResponseData> {
        return ResponseEntity.ok(HealthCheckResponseData("Ok", ApplicationStatus.UP, "1.0"))
    }
}