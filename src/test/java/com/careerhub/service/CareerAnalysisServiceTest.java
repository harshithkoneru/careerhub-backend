package com.careerhub.service;
import com.careerhub.repository.*;
import com.careerhub.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest @Transactional
class CareerAnalysisServiceTest {
 @Autowired CareerAnalysisService service;@Autowired UserRepository users;@Autowired ApplicationRepository apps;
 @Test void returnsWellFormedEmptyAccountAnalysis(){
  User user=users.save(new User("Analyst","analyst@example.test","encoded",Role.USER));
  var result=service.analysis(user.getId());
  assertEquals(0,result.get("totalApplications"));assertNotNull(result.get("careerHealth"));assertNotNull(result.get("recommendations"));
 }
}
