package org.example.data.controllers.functionCheck

import org.example.statuses.ApplicationStatus
import java.util.*

class HealthCheckResponseData (
    val message: String,
    val status: ApplicationStatus,
    val version: String,
    val date: Date = Date()
)