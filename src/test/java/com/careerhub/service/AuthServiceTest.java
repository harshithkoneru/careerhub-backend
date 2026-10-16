package com.careerhub.service;
import com.careerhub.dto.AuthDtos.*;
import com.careerhub.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest(properties = "app.cors.allowed-origins=http://localhost:5173,https://careerhub-frontend-zveu.vercel.app")
@AutoConfigureMockMvc @Transactional
class AuthServiceTest {
 @Autowired AuthService auth;@Autowired UserRepository users;@Autowired PasswordEncoder encoder;
 @Autowired MockMvc mvc;
 @Test void registrationHashesPasswordAndLoginReturnsToken(){
  var registered=auth.register(new RegisterRequest("New User","new@example.test","very-secure-passphrase",null,null,null,2027));
  var user=users.findByEmailIgnoreCase("new@example.test").orElseThrow();
  assertNotEquals("very-secure-passphrase",user.getPassword());assertTrue(encoder.matches("very-secure-passphrase",user.getPassword()));
  assertEquals(user.getId(),registered.user().id());
  assertEquals(user.getEmail(),auth.login(new LoginRequest("new@example.test","very-secure-passphrase")).user().email());
  assertFalse(registered.token().isBlank());
 }
 @Test void registrationEndpointReturnsCreatedAndConflictStatuses()throws Exception{
  String payload="{\"fullName\":\"New User\",\"email\":\"new-endpoint@example.test\",\"password\":\"very-secure-passphrase\"}";
  mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(payload))
    .andExpect(status().isCreated())
    .andExpect(jsonPath("$.success").value(true))
    .andExpect(jsonPath("$.data.token").isNotEmpty());
  mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(payload))
    .andExpect(status().isConflict())
    .andExpect(jsonPath("$.success").value(false));
  mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
      .content("{\"fullName\":\"\",\"email\":\"invalid\",\"password\":\"short\"}"))
    .andExpect(status().isBadRequest())
    .andExpect(jsonPath("$.success").value(false))
    .andExpect(jsonPath("$.message").isNotEmpty());
 }
 @Test void deployedFrontendCanSendRegistrationPreflight()throws Exception{
  mvc.perform(options("/api/auth/register")
      .header("Origin","https://careerhub-frontend-zveu.vercel.app")
      .header("Access-Control-Request-Method","POST")
      .header("Access-Control-Request-Headers","content-type"))
    .andExpect(status().isOk())
    .andExpect(header().string("Access-Control-Allow-Origin","https://careerhub-frontend-zveu.vercel.app"));
 }
 @Test void jwtProtectsRoutesAndRestrictsAdminEndpoints()throws Exception{
  var registration=auth.register(new RegisterRequest("Standard User","standard@example.test","another-secure-passphrase",null,null,null,null));
  mvc.perform(get("/api/profile").header("Authorization","Bearer "+registration.token())).andExpect(status().isOk());
  mvc.perform(get("/api/users/profile").header("Authorization","Bearer "+registration.token())).andExpect(status().isOk());
  mvc.perform(get("/api/dashboard/statistics").header("Authorization","Bearer "+registration.token())).andExpect(status().isOk());
  mvc.perform(get("/api/interviews/upcoming").header("Authorization","Bearer "+registration.token())).andExpect(status().isOk());
  mvc.perform(get("/api/career/health").header("Authorization","Bearer "+registration.token())).andExpect(status().isOk());
  mvc.perform(get("/api/admin/stats").header("Authorization","Bearer "+registration.token())).andExpect(status().isForbidden());
  mvc.perform(get("/api/admin/statistics").header("Authorization","Bearer "+registration.token())).andExpect(status().isForbidden());
  mvc.perform(get("/api/profile")).andExpect(status().isUnauthorized());
 }
}
