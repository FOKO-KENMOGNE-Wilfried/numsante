# Biometric Authentication API Documentation

## Overview

The Carnet de Santé Numérique backend provides comprehensive biometric authentication support for both **patients** and **medical personnel**. This document details the implementation, endpoints, and usage of the biometric authentication system.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Data Model](#data-model)
3. [API Endpoints](#api-endpoints)
4. [Request/Response Examples](#requestresponse-examples)
5. [Error Handling](#error-handling)
6. [Security Considerations](#security-considerations)
7. [Frontend Integration](#frontend-integration)
8. [Testing Guide](#testing-guide)

---

## Architecture Overview

### Biometric Flow

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│  Mobile App │────────▶│  Backend API │────────▶│  PostgreSQL │
│ (React Native)        │ (Spring Boot)│         │  Database   │
└─────────────┘         └──────────────┘         └─────────────┘
      │                        │
      │  1. Register           │
      │     Public Key         │
      ├───────────────────────▶│
      │                        │
      │  2. Login with         │
      │     Biometric          │
      ├───────────────────────▶│
      │                        │
      │  3. JWT Token          │
      │◀───────────────────────┤
```

### Key Components

| Component | Location | Purpose |
|-----------|----------|---------|
| **AuthController** | `controller/AuthController.java` | REST endpoints for biometric operations |
| **AuthService** | `service/AuthService.java` | Business logic for biometric registration/login |
| **BiometricRegistrationRequest** | `dto/BiometricRegistrationRequest.java` | DTO for registration |
| **BiometricLoginRequest** | `dto/BiometricLoginRequest.java` | DTO for login |
| **Patient** | `entity/Patient.java` | Patient entity with `clePubliqueBiometrique` field |
| **PersonnelMedical** | `entity/PersonnelMedical.java` | Personnel entity with `clePubliqueAppareil` field |

---

## Data Model

### Database Schema

**Patient Table**
```sql
CREATE TABLE patients (
    id_patient UUID PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    cle_publique_biometrique TEXT,  -- Stores biometric public key
    -- other fields...
);
```

**Personnel Medical Table**
```sql
CREATE TABLE personnel_medical (
    id_personnel BIGSERIAL PRIMARY KEY,
    identifiant_pro VARCHAR(100) UNIQUE NOT NULL,
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    cle_publique_appareil TEXT,  -- Stores biometric public key
    -- other fields...
);
```

### Entity Fields

**Patient.java**
- Field: `clePubliqueBiometrique` (String, TEXT)
- Location: Line 44-45
- Purpose: Stores the device's public key for biometric authentication

**PersonnelMedical.java**
- Field: `clePubliqueAppareil` (String, TEXT)
- Purpose: Stores the device's public key for biometric authentication

---

## API Endpoints

### 1. Register Biometric Key

**Endpoint:** `POST /auth/enregistrer-biometrie`

**Location:** `AuthController.java:44-49`

**Description:** Registers a device's public key for biometric authentication. This endpoint must be called after initial user registration to enable biometric login.

**Authentication:** Not required (public endpoint)

**Request Body:**
```json
{
  "idUtilisateur": "string",
  "typeUtilisateur": "patient" | "personnel",
  "clePubliqueAppareil": "string"
}
```

**Request Fields:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `idUtilisateur` | String | Yes | Patient UUID or Personnel ID |
| `typeUtilisateur` | String | Yes | Either "patient" or "personnel" |
| `clePubliqueAppareil` | String | Yes | Device's cryptographic public key |

**Response:**
```json
{
  "statut": "success",
  "message": "Authentification biométrique configurée"
}
```

**HTTP Status Codes:**
- `200 OK` - Biometric key registered successfully
- `400 Bad Request` - Invalid request body or validation error
- `404 Not Found` - User not found with given ID
- `500 Internal Server Error` - Server error

---

### 2. Biometric Login

**Endpoint:** `POST /auth/login-biometrique`

**Location:** `AuthController.java:51-56`

**Description:** Authenticates a user using biometric credentials. Returns a JWT token upon successful authentication.

**Authentication:** Not required (public endpoint)

**Request Body:**
```json
{
  "idUtilisateur": "string",
  "signatureDefi": "string"
}
```

**Request Fields:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `idUtilisateur` | String | Yes | Patient UUID or Personnel ID |
| `signatureDefi` | String | Yes | Cryptographic signature (currently simulated) |

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**HTTP Status Codes:**
- `200 OK` - Authentication successful
- `400 Bad Request` - Invalid request body
- `401 Unauthorized` - Authentication failed
- `404 Not Found` - User not found
- `500 Internal Server Error` - Server error

---

## Request/Response Examples

### Example 1: Register Biometric for Patient

**Request:**
```bash
curl -X POST http://localhost:8082/api/v1/auth/enregistrer-biometrie \
  -H "Content-Type: application/json" \
  -d '{
    "idUtilisateur": "550e8400-e29b-41d4-a716-446655440000",
    "typeUtilisateur": "patient",
    "clePubliqueAppareil": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
  }'
```

**Response:**
```json
{
  "statut": "success",
  "message": "Authentification biométrique configurée"
}
```

---

### Example 2: Register Biometric for Personnel

**Request:**
```bash
curl -X POST http://localhost:8082/api/v1/auth/enregistrer-biometrie \
  -H "Content-Type: application/json" \
  -d '{
    "idUtilisateur": "1",
    "typeUtilisateur": "personnel",
    "clePubliqueAppareil": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
  }'
```

**Response:**
```json
{
  "statut": "success",
  "message": "Authentification biométrique configurée"
}
```

---

### Example 3: Biometric Login for Patient

**Request:**
```bash
curl -X POST http://localhost:8082/api/v1/auth/login-biometrique \
  -H "Content-Type: application/json" \
  -d '{
    "idUtilisateur": "550e8400-e29b-41d4-a716-446655440000",
    "signatureDefi": "base64EncodedSignature..."
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDAiLCJyb2xlIjoiUEFUSUVOVCIsImlhdCI6MTYxOTUyMDAwMCwiZXhwIjoxNjE5NjA2NDAwfQ.abc123"
}
```

---

### Example 4: Biometric Login for Personnel

**Request:**
```bash
curl -X POST http://localhost:8082/api/v1/auth/login-biometrique \
  -H "Content-Type: application/json" \
  -d '{
    "idUtilisateur": "1",
    "signatureDefi": "base64EncodedSignature..."
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJkcl9tYmFsbGEiLCJyb2xlIjoiTUVERUNJTiIsImlhdCI6MTYxOTUyMDAwMCwiZXhwIjoxNjE5NjA2NDAwfQ.xyz789"
}
```

---

## Error Handling

### Common Error Responses

**Patient Not Found:**
```json
{
  "timestamp": "2026-06-11T22:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Patient introuvable",
  "path": "/auth/enregistrer-biometrie"
}
```

**Personnel Not Found:**
```json
{
  "timestamp": "2026-06-11T22:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Personnel introuvable",
  "path": "/auth/enregistrer-biometrie"
}
```

**Invalid User Type:**
```json
{
  "timestamp": "2026-06-11T22:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Type utilisateur invalide",
  "path": "/auth/enregistrer-biometrie"
}
```

**Authentication Failed:**
```json
{
  "timestamp": "2026-06-11T22:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Authentification biométrique échouée",
  "path": "/auth/login-biometrique"
}
```

**Validation Error (Missing Required Field):**
```json
{
  "timestamp": "2026-06-11T22:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "idUtilisateur",
      "message": "must not be blank"
    }
  ],
  "path": "/auth/enregistrer-biometrie"
}
```

---

## Security Considerations

### Current Implementation (Simulated)

**⚠️ IMPORTANT:** The biometric login is currently **simulated** for development purposes.

**AuthService.java:61-81** - Biometric Login Logic:
```java
// Simulation de connexion biométrique : on vérifie que la clé publique existe,
// puis on génère un token.
public String loginBiometrique(BiometricLoginRequest request) {
    // Dans un cas réel, on vérifierait la signature avec la clé publique.
    // Ici on simule en acceptant toute signature non vide.
    if (!request.getSignatureDefi().isBlank()) {
        return jwtTokenProvider.generateToken(...);
    }
    // ...
}
```

### Production Implementation Recommendations

For a production-ready biometric authentication system:

#### 1. **Challenge-Response Protocol**

Replace simulated signature verification with real cryptographic verification:

```java
public String loginBiometrique(BiometricLoginRequest request) {
    // 1. Retrieve stored public key
    String publicKeyPem = getPublicKeyForUser(request.getIdUtilisateur());

    // 2. Verify signature using stored public key
    boolean isValid = CryptoUtils.verifySignature(
        publicKeyPem,
        request.getChallenge(),
        request.getSignatureDefi()
    );

    if (!isValid) {
        throw new UnauthorizedException("Invalid biometric signature");
    }

    // 3. Generate JWT token only if signature is valid
    return jwtTokenProvider.generateToken(...);
}
```

#### 2. **Challenge Generation**

Add an endpoint to generate random challenges:

```java
@PostMapping("/auth/biometric-challenge")
public ResponseEntity<Map<String, String>> generateChallenge() {
    String challenge = CryptoUtils.generateRandomChallenge();
    // Store challenge in Redis with expiration (5 minutes)
    redisTemplate.opsForValue().set("challenge:" + challenge, "", 5, TimeUnit.MINUTES);
    return ResponseEntity.ok(Map.of("challenge", challenge));
}
```

#### 3. **Public Key Validation**

Validate public key format during registration:

```java
public void enregistrerBiometrie(BiometricRegistrationRequest request) {
    // Validate public key format
    if (!CryptoUtils.isValidPublicKey(request.getClePubliqueAppareil())) {
        throw new BadRequestException("Invalid public key format");
    }

    // Continue with registration...
}
```

#### 4. **Rate Limiting**

Implement rate limiting to prevent brute-force attacks:

```java
@RateLimiter(name = "biometric-login", fallbackMethod = "rateLimitFallback")
@PostMapping("/login-biometrique")
public ResponseEntity<Map<String, String>> loginBiometrique(...) {
    // ...
}
```

#### 5. **Audit Logging**

Log all biometric operations for security auditing:

```java
public void enregistrerBiometrie(BiometricRegistrationRequest request) {
    // Register biometric...

    // Log the action
    logService.logAction(
        userId,
        patientId,
        "ENREGISTREMENT_BIOMETRIE",
        null
    );
}
```

#### 6. **HTTPS Only**

Ensure biometric endpoints are only accessible via HTTPS in production.

#### 7. **Public Key Rotation**

Allow users to update/rotate their biometric keys:

```java
@PostMapping("/auth/rotate-biometric-key")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> rotateBiometricKey(...) {
    // Verify current key, then update to new key
}
```

---

## Frontend Integration

### Expected Frontend Behavior

Based on the integration report, the frontend (React Native) expects the following flow:

#### 1. **Initial Setup (After User Registration)**

```typescript
// Frontend Service: authService.registerBiometric()
const registerBiometric = async (
  userId: string,
  userType: 'patient' | 'personnel',
  publicKey: string
) => {
  const response = await api.post('/auth/enregistrer-biometrie', {
    idUtilisateur: userId,
    typeUtilisateur: userType,
    clePubliqueAppareil: publicKey
  });
  return response.data;
};
```

#### 2. **Biometric Login**

```typescript
// Frontend Service: authService.loginBiometric()
const loginBiometric = async (
  userId: string,
  signature: string
) => {
  const response = await api.post('/auth/login-biometrique', {
    idUtilisateur: userId,
    signatureDefi: signature
  });
  return response.data.token;
};
```

### Frontend-Backend Contract

**Request DTOs:**
- `BiometricRegistrationRequest` matches frontend `BiometricRegistrationRequest` type
- `BiometricLoginRequest` matches frontend `BiometricLoginRequest` type

**Response Format:**
- Registration: `{ statut: string, message: string }`
- Login: `{ token: string }`

**API Base URL:**
- Backend: `http://localhost:8082/api/v1`
- Frontend expects: `/auth/enregistrer-biometrie` and `/auth/login-biometrique`

✅ **Compatibility:** Frontend and backend are fully compatible.

---

## Testing Guide

### Manual Testing via Swagger

1. **Access Swagger UI:**
   ```
   http://localhost:8082/api/v1/swagger-ui.html
   ```

2. **Test Biometric Registration:**
   - Navigate to "Auth" section
   - Expand "POST /auth/enregistrer-biometrie"
   - Click "Try it out"
   - Enter test data:
     ```json
     {
       "idUtilisateur": "550e8400-e29b-41d4-a716-446655440000",
       "typeUtilisateur": "patient",
       "clePubliqueAppareil": "test-public-key-123"
     }
     ```
   - Click "Execute"
   - Verify `200 OK` response

3. **Test Biometric Login:**
   - Expand "POST /auth/login-biometrique"
   - Click "Try it out"
   - Enter test data:
     ```json
     {
       "idUtilisateur": "550e8400-e29b-41d4-a716-446655440000",
       "signatureDefi": "test-signature"
     }
     ```
   - Click "Execute"
   - Verify `200 OK` response with JWT token

### Testing via cURL

**Register Biometric:**
```bash
curl -X POST http://localhost:8082/api/v1/auth/enregistrer-biometrie \
  -H "Content-Type: application/json" \
  -d '{
    "idUtilisateur": "550e8400-e29b-41d4-a716-446655440000",
    "typeUtilisateur": "patient",
    "clePubliqueAppareil": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A..."
  }'
```

**Login with Biometric:**
```bash
curl -X POST http://localhost:8082/api/v1/auth/login-biometrique \
  -H "Content-Type: application/json" \
  -d '{
    "idUtilisateur": "550e8400-e29b-41d4-a716-446655440000",
    "signatureDefi": "base64EncodedSignature"
  }'
```

### Automated Testing (Recommended)

Create integration tests:

```java
@SpringBootTest
@AutoConfigureMockMvc
class BiometricAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testEnregistrerBiometrie_Patient_Success() throws Exception {
        mockMvc.perform(post("/auth/enregistrer-biometrie")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "idUtilisateur": "550e8400-e29b-41d4-a716-446655440000",
                        "typeUtilisateur": "patient",
                        "clePubliqueAppareil": "test-key"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("success"));
    }

    @Test
    void testLoginBiometrique_Success() throws Exception {
        // First register biometric...

        // Then test login
        mockMvc.perform(post("/auth/login-biometrique")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "idUtilisateur": "550e8400-e29b-41d4-a716-446655440000",
                        "signatureDefi": "test-signature"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}
```

---

## Implementation Details

### Service Layer Logic

**Location:** `AuthService.java:45-81`

**Registration Logic (Lines 45-59):**
```java
public void enregistrerBiometrie(BiometricRegistrationRequest request) {
    if ("patient".equals(request.getTypeUtilisateur())) {
        Patient patient = patientRepo.findById(UUID.fromString(request.getIdUtilisateur()))
                .orElseThrow(() -> new RuntimeException("Patient introuvable"));
        patient.setClePubliqueBiometrique(request.getClePubliqueAppareil());
        patientRepo.save(patient);
    } else if ("personnel".equals(request.getTypeUtilisateur())) {
        PersonnelMedical personnel = personnelRepo.findById(Long.parseLong(request.getIdUtilisateur()))
                .orElseThrow(() -> new RuntimeException("Personnel introuvable"));
        personnel.setClePubliqueAppareil(request.getClePubliqueAppareil());
        personnelRepo.save(personnel);
    } else {
        throw new RuntimeException("Type utilisateur invalide");
    }
}
```

**Login Logic (Lines 62-81):**
```java
public String loginBiometrique(BiometricLoginRequest request) {
    // Try patient first
    try {
        Patient patient = patientRepo.findById(UUID.fromString(request.getIdUtilisateur())).orElse(null);
        if (patient != null && patient.getClePubliqueBiometrique() != null) {
            if (!request.getSignatureDefi().isBlank()) {
                return jwtTokenProvider.generateToken(patient.getIdPatient().toString(), "PATIENT");
            }
        }
    } catch (IllegalArgumentException ignored) {}

    // Then try personnel
    PersonnelMedical personnel = personnelRepo.findById(Long.parseLong(request.getIdUtilisateur()))
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    if (personnel.getClePubliqueAppareil() != null && !request.getSignatureDefi().isBlank()) {
        return jwtTokenProvider.generateToken(personnel.getIdentifiantPro(), personnel.getRole());
    }

    throw new RuntimeException("Authentification biométrique échouée");
}
```

---

## Changelog

| Date | Version | Changes |
|------|---------|---------|
| 2026-06-11 | 1.0 | Initial documentation of existing biometric endpoints |

---

## Support

For questions or issues:
- Review this documentation
- Check Swagger UI: `http://localhost:8082/api/v1/swagger-ui.html`
- Consult project README: `README.md`
- Review source code:
  - `AuthController.java:44-56`
  - `AuthService.java:45-81`

---

## References

- **Main Documentation:** `README.md`
- **User Guide:** `userGuide`
- **Integration Report:** Provided rapport document
- **Swagger UI:** `http://localhost:8082/api/v1/swagger-ui.html`
- **OpenAPI Spec:** `http://localhost:8082/api/v1/api-docs`

---

**Document Version:** 1.0
**Last Updated:** 2026-06-11
**Maintained By:** Carnet de Santé Numérique Development Team
**Project:** UCAC-ICAM Mobile Development Project
