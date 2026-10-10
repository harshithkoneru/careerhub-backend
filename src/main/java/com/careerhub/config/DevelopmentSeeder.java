package com.careerhub.config;
import com.careerhub.entity.*;
import com.careerhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DevelopmentSeeder {
 @Bean CommandLineRunner seedConfiguredAccounts(UserRepository users,PasswordEncoder encoder,
   @Value("${app.seed.admin.email:}")String adminEmail,@Value("${app.seed.admin.password:}")String adminPassword,
   @Value("${app.seed.admin.name:CareerHub Admin}")String adminName,
   @Value("${app.seed.user.email:}")String userEmail,@Value("${app.seed.user.password:}")String userPassword,
   @Value("${app.seed.user.name:CareerHub Test User}")String userName) {
  return args->{
   seed(users,encoder,adminEmail,adminPassword,adminName,Role.ADMIN);
   seed(users,encoder,userEmail,userPassword,userName,Role.USER);
  };
 }
 private static void seed(UserRepository users,PasswordEncoder encoder,String email,String password,String name,Role role){
  if(email==null||email.isBlank()||password==null||password.isBlank())return;
  if(password.length()<12)throw new IllegalStateException("Configured seed account passwords must be at least 12 characters");
  if(!users.existsByEmailIgnoreCase(email)){users.save(new User(name,email.trim().toLowerCase(),encoder.encode(password),role));}
 }
}
