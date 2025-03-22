package com.example.todo_api.model.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TodoResponse {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
