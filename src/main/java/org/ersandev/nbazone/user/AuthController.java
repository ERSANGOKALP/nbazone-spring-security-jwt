package org.ersandev.nbazone.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ersandev.nbazone.security.jwt.JwtUtils;
import org.ersandev.nbazone.security.request.LoginRequest;
import org.ersandev.nbazone.security.request.SignupRequest;
import org.ersandev.nbazone.security.response.MessageResponse;
import org.ersandev.nbazone.security.response.UserJwtInfoResponse;
import org.ersandev.nbazone.security.userdetail.CustomUserDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        //burada username ve password alarak Authentication türünde bir obje yaratarak içine varsa geçerli user'ı ekliyoruz
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
                    );
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }
        // SecurityContextHolder içine geçerli user'ı set et
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // authentication.getPrincipal() metodu ile userDetails al
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        // aldığımız userDetails aracılığı ile jwtCookie yaratıyoruz
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        //rolleri al ve List ile tut
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        // bir response yarat ve içine gerekli olan id,username,roles ve jwtCookie ekle
        UserJwtInfoResponse response = new UserJwtInfoResponse(userDetails.getId(),
                userDetails.getUsername(), roles, jwtCookie.toString());
        //geriye ResponseEntity içinde, header olarak jwtCookie'yi string yaparak set et ve response'u dön
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                        jwtCookie.toString())
                .body(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser(){
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if(userRepository.existsByUsername(signUpRequest.getUsername())){
            ResponseEntity.badRequest().body(new MessageResponse("Username is already taken!"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())){
            ResponseEntity.badRequest().body(new MessageResponse("Email is already in use!"));
        }

        //yeni kullanıcı oluştur / create new user
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        //rolleri belirle. request'ten role gelmediyse yani null ise default olarak USER, gelmişse gelen rolleri set et
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        // request'ten role gelmediyse yani null ise USER olarak default set et
        if(strRoles == null){
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow( () -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }else {// request'ten role geldiyse admin ise admin olarak ve default user olarak kaydet ve admin değilse USER olarak kaydet
            strRoles.forEach( role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow( () -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

    }

    @GetMapping("/username")
    public String currentUsername(Authentication authentication){
        if (authentication != null){
            return authentication.getName();
        }else {
            return "";
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        //authentication'dan getPrincipal() methodu ile ıserDetails al
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        //userDetails içinden getAuthorities() methodu ve stream ile herbir rolü al ve list haline getir
        List<String> roles = userDetails.getAuthorities().stream()
                .map(role -> role.getAuthority())
                .toList();

        //userDetails içinden ıd ve username'i al ve oluşturduğun roles List'ini içine ekle
        UserJwtInfoResponse response = new UserJwtInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                roles
        );
        //ResponseEntity ok ile 200 kodunu ve body olarak response'u dön
        return ResponseEntity.ok(response);
    }

}
