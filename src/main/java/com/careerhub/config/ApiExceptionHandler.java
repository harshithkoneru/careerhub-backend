package com.careerhub.config;
import com.careerhub.dto.ApiResponse;
import com.careerhub.exception.ApiException;
import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
 private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

 @ExceptionHandler(ApiException.class) ResponseEntity<ApiResponse<Void>> api(ApiException e){return ResponseEntity.status(e.getStatus()).body(new ApiResponse<>(false,e.getMessage(),null));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException e){
  String message=e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+": "+x.getDefaultMessage()).distinct().collect(Collectors.joining("; "));
  return ResponseEntity.badRequest().body(new ApiResponse<>(false,message,null));
 }
 @ExceptionHandler(ConstraintViolationException.class) ResponseEntity<ApiResponse<Void>> constraint(ConstraintViolationException e){return ResponseEntity.badRequest().body(new ApiResponse<>(false,e.getMessage(),null));}
 @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<ApiResponse<Void>> duplicate(DataIntegrityViolationException e){return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(false,"The request conflicts with an existing record",null));}
 @ExceptionHandler(Exception.class) ResponseEntity<ApiResponse<Void>> unexpected(Exception e, HttpServletRequest request){
  log.error("Unhandled API error for {} {}",request.getMethod(),request.getRequestURI(),e);
  return ResponseEntity.status(500).body(new ApiResponse<>(false,"An unexpected server error occurred",null));
 }
}
