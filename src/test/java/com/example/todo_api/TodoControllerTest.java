package com.example.todo_api;


import com.example.todo_api.controller.TodoController;
import com.example.todo_api.model.dto.TodoRequest;
import com.example.todo_api.model.dto.TodoResponse;
import com.example.todo_api.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    @MockBean
    private TodoService todoService;

    private TodoResponse todoResponse;
    private TodoRequest todoRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        todoResponse = new TodoResponse();
        todoResponse.setId(1L);
        todoResponse.setTitle("Test Todo");
        todoResponse.setDescription("Test Description");
        todoResponse.setCompleted(false);
        todoResponse.setCreatedAt(now);
        todoResponse.setUpdatedAt(now);
        
        todoRequest = new TodoRequest();
        todoRequest.setTitle("Test Todo");
        todoRequest.setDescription("Test Description");
        todoRequest.setCompleted(false);
    }

    @Test
    void shouldGetAllTodos() throws Exception {
        when(todoService.getAllTodos()).thenReturn(List.of(todoResponse));
        
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(todoResponse.getId()))
                .andExpect(jsonPath("$[0].title").value(todoResponse.getTitle()));
        
        verify(todoService, times(1)).getAllTodos();
    }

    @Test
    void shouldGetTodoById() throws Exception {
        when(todoService.getTodoById(1L)).thenReturn(todoResponse);
        
        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(todoResponse.getId()))
                .andExpect(jsonPath("$.title").value(todoResponse.getTitle()));
        
        verify(todoService, times(1)).getTodoById(1L);
    }

    @Test
    void shouldCreateTodo() throws Exception {
        when(todoService.createTodo(any(TodoRequest.class))).thenReturn(todoResponse);
        
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(todoResponse.getId()))
                .andExpect(jsonPath("$.title").value(todoResponse.getTitle()));
        
        verify(todoService, times(1)).createTodo(any(TodoRequest.class));
    }

    @Test
    void shouldUpdateTodo() throws Exception {
        when(todoService.updateTodo(eq(1L), any(TodoRequest.class))).thenReturn(todoResponse);
        
        mockMvc.perform(put("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoResponse.getId()))
                .andExpect(jsonPath("$.title").value(todoResponse.getTitle()));
        
        verify(todoService, times(1)).updateTodo(eq(1L), any(TodoRequest.class));
    }

    @Test
    void shouldToggleTodoStatus() throws Exception {
        when(todoService.toggleTodoStatus(1L)).thenReturn(todoResponse);
        
        mockMvc.perform(patch("/api/todos/1/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoResponse.getId()));
        
        verify(todoService, times(1)).toggleTodoStatus(1L);
    }

    @Test
    void shouldDeleteTodo() throws Exception {
        doNothing().when(todoService).deleteTodo(1L);
        
        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());
        
        verify(todoService, times(1)).deleteTodo(1L);
    }

    @Test
    void shouldReturnBadRequestForInvalidInput() throws Exception {
        TodoRequest invalidRequest = new TodoRequest();
        invalidRequest.setTitle("");
        
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        
        verify(todoService, never()).createTodo(any());
    }
}