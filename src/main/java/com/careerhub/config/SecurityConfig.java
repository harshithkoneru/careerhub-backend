package com.careerhub.config;
import com.careerhub.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;

@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean UserDetailsService noPasswordAuthentication(){return username->{throw new UsernameNotFoundException("Bearer token authentication is required");};}
 @Bean SecurityFilterChain filterChain(HttpSecurity http,JwtAuthenticationFilter jwtFilter)throws Exception{
   return http.csrf(csrf->csrf.disable()).cors(Customizer.withDefaults()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
     .authorizeHttpRequests(a->a.requestMatchers("/api/auth/**","/actuator/health","/swagger-ui/**","/v3/api-docs/**").permitAll()
       .requestMatchers("/api/admin/**").hasRole("ADMIN")
       .requestMatchers("/api/**").hasAnyRole("USER","ADMIN").anyRequest().authenticated())
     .exceptionHandling(e->e.authenticationEntryPoint((request,response,exception)->{
       response.setStatus(401);response.setContentType("application/json");response.getWriter().write("{\"success\":false,\"message\":\"Authentication required or token invalid\",\"data\":null}");
     }).accessDeniedHandler((request,response,exception)->{
       response.setStatus(403);response.setContentType("application/json");response.getWriter().write("{\"success\":false,\"message\":\"You are not authorized to perform this action\",\"data\":null}");
     }))
     .addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class).build();
 }
 @Bean CorsConfigurationSource corsConfigurationSource(){
   CorsConfiguration c=new CorsConfiguration();c.setAllowedOrigins(List.of("http://localhost:5173"));c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));c.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));c.setExposedHeaders(List.of("Authorization"));c.setAllowCredentials(true);
   UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();source.registerCorsConfiguration("/**",c);return source;
 }
}
