package com.changlu.langchain4jtools.demo03;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class Demo03ApiController {


    // GET /users/{userId}?page=1&size=10
    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUserInfo(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authToken,
            @RequestHeader(value = "Content-Type", required = false) String contentType) {

        Map<String, Object> response = new HashMap<>();
        response.put("method", "GET");
        response.put("userId", userId);
        response.put("page", page);
        response.put("size", size);
        response.put("authToken", authToken);
        response.put("contentType", contentType);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    // POST /users
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(
            @RequestBody Map<String, Object> userData,
            @RequestHeader(value = "Content-Type", defaultValue = "application/json") String contentType,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId,
            @RequestHeader("X-API-Version") String apiVersion) {

        Map<String, Object> response = new HashMap<>();
        response.put("method", "POST");
        response.put("userData", userData);
        response.put("contentType", contentType);
        response.put("traceId", traceId);
        response.put("apiVersion", apiVersion);
        response.put("id", UUID.randomUUID().toString());
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT /users/{userId}
    @PutMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable String userId,
            @RequestBody Map<String, Object> userData,
            @RequestHeader(value = "If-Match", required = false) String ifMatch,
            @RequestHeader("Content-Type") String contentType) {

        Map<String, Object> response = new HashMap<>();
        response.put("method", "PUT");
        response.put("userId", userId);
        response.put("userData", userData);
        response.put("ifMatch", ifMatch);
        response.put("contentType", contentType);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    // DELETE /users/{userId}?force=false
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "false") boolean force,
            @RequestHeader("Authorization") String authToken,
            @RequestHeader("X-API-Version") String apiVersion) {

        Map<String, Object> response = new HashMap<>();
        response.put("method", "DELETE");
        response.put("userId", userId);
        response.put("force", force);
        response.put("authToken", authToken);
        response.put("apiVersion", apiVersion);
        response.put("deleted", true);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    // PATCH /users/{userId}
    @PatchMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> patchUser(
            @PathVariable String userId,
            @RequestBody Map<String, Object> patchData,
            @RequestHeader(value = "Content-Type", defaultValue = "application/json-patch+json") String contentType) {

        Map<String, Object> response = new HashMap<>();
        response.put("method", "PATCH");
        response.put("userId", userId);
        response.put("patchData", patchData);
        response.put("contentType", contentType);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

}
