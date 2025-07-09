package com.zosh.controller;

import com.zosh.modal.Category;
import com.zosh.payload.dto.SalonDTO;
import com.zosh.payload.dto.UserDTO;
import com.zosh.service.CategoryService;
import com.zosh.service.clients.SalonFeignClient;
import com.zosh.service.clients.UserFeignClient;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories/salon-owner")
@RequiredArgsConstructor
public class SalonCategoryController {

    private final CategoryService categoryService;
    private final SalonFeignClient salonService;

    @PostMapping
    public ResponseEntity<?> createCategory(
            @RequestBody Category category,
            @RequestHeader("Authorization") String jwt,
            @RequestHeader(value = "X-Cognito-Sub", required = false) String cognitoSub,
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @RequestHeader(value = "X-User-Username", required = false) String username,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-Auth-Source", required = false) String authSource) {

        try {
            ResponseEntity<SalonDTO> response = salonService.getSalonByOwner(jwt, cognitoSub, userEmail, username, userRole, authSource);
            SalonDTO salon = response.getBody();


            if (salon == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No se pudo obtener el salón del propietario");
            }

            category.setSalonId(salon.getId());
            Category savedCategory = categoryService.saveCategory(category, salon);

            return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);

        } catch (FeignException e) {
            System.err.println("❌ Error Feign al obtener salón: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al obtener salón del propietario: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error inesperado al crear categoría: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }
}
