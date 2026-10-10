package com.careerhub.service;
import com.careerhub.dto.ApplicationDtos.SaveRequest;
import com.careerhub.entity.*;
import com.careerhub.exception.ApiException;
import com.careerhub.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ApplicationServiceTest {
 @Autowired ApplicationService service;@Autowired UserRepository users;
 @BeforeEach void user(){User u=new User("Test User","test@example.test","encoded",Role.USER);users.save(u);}
 private SaveRequest request(String status){return new SaveRequest("Acme","Engineer","Full Time","Remote",LocalDate.now(),null,null,java.util.List.of("Java","SQL"),java.util.List.of("Java"),"Resume.pdf",status,"Unknown","");}
 @Test void createsAndRejectsDuplicateApplication(){
  String uid=users.findByEmailIgnoreCase("test@example.test").orElseThrow().getId();
  assertEquals("Applied",service.create(uid,request("Applied")).status());
  assertThrows(ApiException.class,()->service.create(uid,request("APPLIED")));
 }
 @Test void enforcesProgressionAndOwnership(){
  String uid=users.findByEmailIgnoreCase("test@example.test").orElseThrow().getId();
  var app=service.create(uid,request("Applied"));
  assertThrows(ApiException.class,()->service.update(uid,app.id(),request("Selected")));
  User other=users.save(new User("Other","other@example.test","encoded",Role.USER));
  assertThrows(ApiException.class,()->service.get(other.getId(),app.id()));
 }
}
