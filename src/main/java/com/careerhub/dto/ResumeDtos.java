package com.careerhub.dto;
public final class ResumeDtos {
 private ResumeDtos() {}
 public record View(String id,String name,String uploadedAt,boolean isDefault,long size) {}
}
