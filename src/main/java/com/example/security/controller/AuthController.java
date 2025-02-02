package com.example.security.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.security.enums.ClientType;
import com.example.security.enums.UserRole;
import com.example.security.exception.TokenRefreshException;
import com.example.security.model.*;
import com.example.security.payload.request.LoginRequest;
import com.example.security.payload.request.SignupRequest;
import com.example.security.payload.request.TokenRefreshRequest;
import com.example.security.payload.response.JwtResponse;
import com.example.security.payload.response.MessageResponse;
import com.example.security.payload.response.TokenRefreshResponse;
import com.example.security.repository.*;
import com.example.security.security.jwt.JwtUtils;
import com.example.security.security.services.RefreshTokenService;
import com.example.security.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
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

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    String jwt = jwtUtils.generateJwtToken(userDetails);
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
    List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
      userDetails.getUsername(), userDetails.getEmail(), roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

     if (signUpRequest.getClientType() == ClientType.INDIVIDUAL) {
       if (signUpRequest.getName() == null || signUpRequest.getLastName() == null) {
           return ResponseEntity.badRequest().body(new MessageResponse("Error: Name and Last Name are required for individuals!"));
       }
     } else if (signUpRequest.getClientType() == ClientType.LEGAL) {
         if (signUpRequest.getCompanyName() == null || signUpRequest.getPib() == null) {
             return ResponseEntity.badRequest().body(new MessageResponse("Error: Company Name and PIB are required for legal clients!"));
         }
     } else {
         return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid client type!"));
     }

    User user = new User(signUpRequest.getName(), signUpRequest.getLastName(), signUpRequest.getUsername(), signUpRequest.getEmail(), 
                    encoder.encode(signUpRequest.getPassword()),signUpRequest.getAddress(), signUpRequest.getCity(), 
                    signUpRequest.getCountry(), signUpRequest.getPhoneNumber(), signUpRequest.getCompanyName(), signUpRequest.getPib(), signUpRequest.getClientType(),
                    signUpRequest.getServicesPackage());

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(UserRole.ROLE_CLIENT)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(UserRole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
        case "employee":
          Role modRole = roleRepository.findByName(UserRole.ROLE_EMPLOYEE)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(modRole);

          break;
        default:
          Role userRole = roleRepository.findByName(UserRole.ROLE_CLIENT)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }
    user.setRoles(roles);
    
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

  @PostMapping("/refreshtoken")
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();

    return refreshTokenService.findByToken(requestRefreshToken)
        .map(refreshTokenService::verifyExpiration)
        .map(RefreshToken::getUser)
        .map(user -> {
          String token = jwtUtils.generateTokenFromUsername(user.getUsername());
          return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
        })
        .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
  }

}
