package com.careerhub.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
 private final JwtService jwt;
 public JwtAuthenticationFilter(JwtService jwt){this.jwt=jwt;}
 @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException{
   String header=request.getHeader("Authorization");
   if(header!=null&&header.startsWith("Bearer ")&&SecurityContextHolder.getContext().getAuthentication()==null){
     try{String token=header.substring(7);String id=jwt.subject(token);String role=jwt.role(token);
       var auth=new UsernamePasswordAuthenticationToken(id,null,List.of(new SimpleGrantedAuthority("ROLE_"+role)));
       SecurityContextHolder.getContext().setAuthentication(auth);
     }catch(RuntimeException ignored){SecurityContextHolder.clearContext();}
   }
   chain.doFilter(request,response);
 }
}
