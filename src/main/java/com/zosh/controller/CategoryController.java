package com.zosh.controller;

import com.zosh.exception.UserException;
import com.zosh.modal.Category;
import com.zosh.payload.dto.SalonDTO;
import com.zosh.payload.dto.UserDTO;
import com.zosh.service.CategoryService;
import com.zosh.service.clients.SalonFeignClient;
import com.zosh.service.clients.UserFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final UserFeignClient userService;
    private final SalonFeignClient salonService;

    // Get all Categories
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Get all Categories by Salon ID
    @GetMapping("/salon/{id}")
    public ResponseEntity<Set<Category>> getCategoriesBySalon(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt) {

        System.out.println("📂 CATEGORY - getCategoriesBySalon");

        try {
            // 🚀 OBTENER USUARIO CON MANEJO DE ERRORES
            UserDTO user = null;
            try {
                user = userService.getUserFromJwtToken(jwt).getBody();
            } catch (Exception e) {
                System.err.println("❌ Error obteniendo usuario: " + e.getMessage());
                return ResponseEntity.ok(Collections.emptySet());
            }

            if (user == null) {
                System.out.println("❌ Usuario no encontrado");
                return ResponseEntity.ok(Collections.emptySet());
            }

            // 🚀 OBTENER SALÓN CON MANEJO DE ERRORES
            SalonDTO salon = null;
            try {
                salon = salonService.getSalonById(id).getBody();
            } catch (Exception e) {
                System.err.println("❌ Error obteniendo salón: " + e.getMessage());
                return ResponseEntity.ok(Collections.emptySet());
            }

            if (salon == null) {
                System.out.println("❌ Salón no encontrado");
                return ResponseEntity.ok(Collections.emptySet());
            }

            // 🚀 OBTENER CATEGORÍAS
            Set<Category> categories = categoryService.getAllCategoriesBySalon(salon.getId());

            return new ResponseEntity<>(categories, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("❌ Error general: " + e.getMessage());
            return ResponseEntity.ok(Collections.emptySet());
        }
    }

    // Obtener todas las categorías del salón del propietario autenticado
    @GetMapping("/salon-owner")
    public ResponseEntity<Set<Category>> getCategoriesBySalonOwner(
            @RequestHeader("Authorization") String jwt,
            @RequestHeader(value = "X-Cognito-Sub", required = false) String cognitoSub,
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @RequestHeader(value = "X-User-Username", required = false) String username,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-Auth-Source", required = false) String authSource) {
        try {
            SalonDTO salon = salonService.getSalonByOwner(jwt, cognitoSub, userEmail, username, userRole, authSource)
                    .getBody();
            if (salon == null) {
                System.out.println("❌ No se encontró el salón");
                return ResponseEntity.ok(Collections.emptySet());
            }
            Set<Category> categories = categoryService.getAllCategoriesBySalon(salon.getId());
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener categorías por dueño: " + e.getMessage());
            return ResponseEntity.ok(Collections.emptySet());
        }
    }

    // Get a Category by ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt // 🔐 ¡Agrega esto!
    ) {
        try {
            Category category = categoryService.getCategoryById(id);
            return new ResponseEntity<>(category, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @RequestBody Category category) throws Exception {
        Category updatedCategory = categoryService.updateCategory(id, category);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    // Delete a Category by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
