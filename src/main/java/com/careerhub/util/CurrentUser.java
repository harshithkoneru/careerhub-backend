package com.careerhub.util;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
public final class CurrentUser {
 private CurrentUser() {}
 public static String id() {
   Authentication auth=SecurityContextHolder.getContext().getAuthentication();
   if(auth==null || auth.getPrincipal()==null) throw new IllegalStateException("Authenticated user is missing");
   return (String)auth.getPrincipal();
 }
}
