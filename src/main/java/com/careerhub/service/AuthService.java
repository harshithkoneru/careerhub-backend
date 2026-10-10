package com.careerhub.service;
import com.careerhub.dto.AuthDtos.*;
import com.careerhub.entity.*;
import com.careerhub.exception.ApiException;
import com.careerhub.repository.UserRepository;
import com.careerhub.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
 private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
 public AuthService(UserRepository users,PasswordEncoder encoder,JwtService jwt){this.users=users;this.encoder=encoder;this.jwt=jwt;}
 @Transactional
 public AuthResponse register(RegisterRequest request) {
   if(users.existsByEmailIgnoreCase(request.email())) throw ApiException.conflict("An account with this email already exists");
   User user=new User(request.fullName().trim(),request.email().trim().toLowerCase(),encoder.encode(request.password()),Role.USER);
   user.setPhone(request.phone()); user.setCollege(request.college()); user.setDegree(request.degree()); user.setGraduationYear(request.graduationYear());
   users.save(user); return response(user);
 }
 public AuthResponse login(LoginRequest request) {
   User user=users.findByEmailIgnoreCase(request.email()).orElseThrow(()->new ApiException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));
   if(!encoder.matches(request.password(),user.getPassword())) throw new ApiException(HttpStatus.UNAUTHORIZED,"Invalid email or password");
   return response(user);
 }
 public static UserView view(User u){return new UserView(u.getId(),u.getFullName(),u.getEmail(),u.getRole(),u.getPhone(),u.getCollege(),u.getDegree(),u.getGraduationYear(),u.getSkills(),u.getLinkedIn(),u.getGithub(),u.getPortfolio());}
 private AuthResponse response(User u){return new AuthResponse(jwt.generate(u.getId(),u.getRole().name()),"Bearer",view(u));}
}
