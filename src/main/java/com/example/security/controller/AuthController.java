package com.example.security.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.example.security.enums.ClientType;
import com.example.security.enums.UserRole;
import com.example.security.exception.TokenRefreshException;
import com.example.security.model.*;
import com.example.security.payload.request.LoginRequest;
import com.example.security.payload.request.SignupRequest;
import com.example.security.payload.response.CaptchaResponse;
import com.example.security.payload.response.MessageResponse;
import com.example.security.payload.response.UserInfoResponse;
import com.example.security.repository.*;
import com.example.security.security.jwt.JwtUtils;
import com.example.security.security.services.RefreshTokenService;
import com.example.security.security.services.UserDetailsImpl;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final String SECRET_KEY = "6LdqIfcqAAAAAL1yr2icvV6ys8t3VNpr9p3USb2s";

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        
        if(UserRole.ROLE_MODERATOR.equals(loginRequest.getRole())) {
            String captchaResponse = loginRequest.getCaptchaResponse();

            if (captchaResponse == null || captchaResponse.isEmpty()) {
                return ResponseEntity.badRequest().body("reCAPTCHA verification failed!");
            }
            
            CaptchaResponse captchaVerificationResult = verifyCaptcha(captchaResponse);
            if (captchaVerificationResult == null || !captchaVerificationResult.isSuccess()) {
                return ResponseEntity.badRequest().body("reCAPTCHA verification failed! Errors: " + Arrays.toString(captchaVerificationResult.getErrorCodes()));
            }
        }

        try{
            Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), 
                    loginRequest.getPassword()
                ));
        
            SecurityContextHolder.getContext().setAuthentication(authentication);
        
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
            ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());
        

            List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
            

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                    .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                                    .body(new UserInfoResponse(userDetails.getId(),
                                        userDetails.getUsername(),
                                        userDetails.getEmail(),
                                        userDetails.getClientType(),
                                        userDetails.getName(),
                                        userDetails.getLastName(),
                                        userDetails.getCompanyName(),
                                        userDetails.getPib(),
                                        userDetails.getAddress(),
                                        userDetails.getCity(),
                                        userDetails.getCountry(),
                                        userDetails.getPhoneNumber(),
                                        userDetails.getServicesPackage(),
                                        roles
                                        ));

        } catch(BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.TEXT_PLAIN)
                .body("Incorrect username or password");
        }
    }

    private CaptchaResponse verifyCaptcha(String captchaResponse) {
        String verifyUrl = String.format("%s?secret=%s&response=%s", RECAPTCHA_VERIFY_URL, SECRET_KEY, captchaResponse);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(verifyUrl, null, CaptchaResponse.class);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use!"));
        }

        Set<String> strRoles = signUpRequest.getRole();
        boolean isAdminOrModerator = strRoles != null && !strRoles.isEmpty() && (strRoles.contains("ADMIN") || strRoles.contains("MODERATOR"));

        if (!isAdminOrModerator) {
            if (signUpRequest.getClientType() == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Client type is required!"));
            }
            if (signUpRequest.getClientType() != ClientType.INDIVIDUAL && signUpRequest.getClientType() != ClientType.LEGAL) {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid client type!"));
            }
            if (signUpRequest.getClientType() == ClientType.INDIVIDUAL) {
                if (signUpRequest.getName() == null || signUpRequest.getLastName() == null) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Name and Last Name are required for individuals!"));
                }
            } else if (signUpRequest.getClientType() == ClientType.LEGAL) {
                if (signUpRequest.getCompanyName() == null || signUpRequest.getPib() == null) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Company Name and PIB are required for legal clients!"));
                }
            }
        }

        User user = new User(
            isAdminOrModerator ? null : signUpRequest.getName(),
            isAdminOrModerator ? null : signUpRequest.getLastName(),
            signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()),
            isAdminOrModerator ? null : signUpRequest.getAddress(),
            isAdminOrModerator ? null : signUpRequest.getCity(),
            isAdminOrModerator ? null : signUpRequest.getCountry(),
            isAdminOrModerator ? null : signUpRequest.getPhoneNumber(),
            isAdminOrModerator ? null : signUpRequest.getCompanyName(),
            isAdminOrModerator ? null : signUpRequest.getPib(),
            isAdminOrModerator ? null : signUpRequest.getClientType(),
            isAdminOrModerator ? null : signUpRequest.getServicesPackage()
        );

        Set<Role> roles = new HashSet<>();
        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(UserRole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            for (String role:strRoles) {
                switch (role) {
                    case "ADMIN":
                        Role adminRole = roleRepository.findByName(UserRole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "MODERATOR":
                        Role modRole = roleRepository.findByName(UserRole.ROLE_MODERATOR)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(UserRole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            }
        }
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);
        
        if ((refreshToken != null) && (refreshToken.length() > 0)) {
        return refreshTokenService.findByToken(refreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(user -> {
                ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
                
                return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(new MessageResponse("Token is refreshed successfully!"));
            })
            .orElseThrow(() -> new TokenRefreshException(refreshToken,
                "Refresh token is not in database!"));
        }
        
        return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principle.toString() != "anonymousUser") {      
        Long userId = ((UserDetailsImpl) principle).getId();
        refreshTokenService.deleteByUserId(userId);
        }
        
        ResponseCookie cleanJwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie cleanJwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cleanJwtCookie.toString())
            .header(HttpHeaders.SET_COOKIE, cleanJwtRefreshCookie.toString())
            .body(new MessageResponse("You've been signed out!"));
    }

}
