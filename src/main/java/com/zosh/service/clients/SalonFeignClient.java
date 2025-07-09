package com.zosh.service.clients;

import com.zosh.payload.dto.SalonDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("SALON")
public interface SalonFeignClient {

        @GetMapping("/api/salons/owner")
        ResponseEntity<SalonDTO> getSalonByOwner(
                        @RequestHeader("Authorization") String jwt,
                        @RequestHeader(value = "X-Cognito-Sub", required = false) String cognitoSub,
                        @RequestHeader(value = "X-User-Email", required = false) String userEmail,
                        @RequestHeader(value = "X-User-Username", required = false) String username, // ESTE FALTABA
                        @RequestHeader(value = "X-User-Role", required = false) String userRole,
                        @RequestHeader(value = "X-Auth-Source", required = false) String authSource);

        @GetMapping("/api/salons/{salonId}")
        public ResponseEntity<SalonDTO> getSalonById(@PathVariable Long salonId)
                        throws Exception;
}
