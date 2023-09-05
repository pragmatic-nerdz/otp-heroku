package com.pragmaticnerdz.otp.resource.persistence

import org.springframework.data.repository.CrudRepository

interface OtpRepository : CrudRepository<OtpEntity, String>
