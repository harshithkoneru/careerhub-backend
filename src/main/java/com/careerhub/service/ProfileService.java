package com.careerhub.service;
import com.careerhub.dto.ProfileDtos.UpdateRequest;
import com.careerhub.entity.User;
import com.careerhub.exception.ApiException;
import com.careerhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
 private final UserRepository users;
 public ProfileService(UserRepository users){this.users=users;}
 public User get(String id){return users.findById(id).orElseThrow(()->ApiException.notFound("User"));}
 @Transactional public User update(String id,UpdateRequest r){
   User u=get(id);
   if(r.email()!=null&&!r.email().equalsIgnoreCase(u.getEmail())&&users.existsByEmailIgnoreCase(r.email()))throw ApiException.conflict("An account with this email already exists");
   u.setFullName(r.fullName().trim());if(r.email()!=null)u.setEmail(r.email().trim().toLowerCase());
   u.setPhone(r.phone());u.setCollege(r.college());u.setDegree(r.degree());u.setGraduationYear(r.graduationYear());u.setSkills(r.skills());u.setLinkedIn(r.linkedIn());u.setGithub(r.github());u.setPortfolio(r.portfolio());return users.save(u);
 }
}
