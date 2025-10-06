package com.example.PipReviewSystem.dto;


import lombok.Data;

@Data
public class ProgressUpdateDTO {
    private String status;//"ASSIGNED", "STARTED", "WORKING", "ABOUT TO COMPLETE", "COMPLETED"
    private String progressNote;
}