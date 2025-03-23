package com.example.todo_api;


import com.example.todo_api.model.dto.TodoRequest;
import com.example.todo_api.model.dto.TodoResponse;
import com.example.todo_api.exception.ResourceNotFoundException;
import com.example.todo_api.model.entity.Todo;
import com.example.todo_api.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo todo;
    private TodoRequest todoRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Test Todo");
        todo.setDescription("Test Description");
        todo.setCompleted(false);
        todo.setCreatedAt(now);
        todo.setUpdatedAt(now);
        
        todoRequest = new TodoRequest();
        todoRequest.setTitle("Test Todo");
        todoRequest.setDescription("Test Description");
        todoRequest.setCompleted(false);
    }

    @Test
    void shouldGetAllTodos() {
        when(todoRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(todo));
        
        List<TodoResponse> todos = todoService.getAllTodos();
        
        assertThat(todos).hasSize(1);
        assertThat(todos.get(0).getId()).isEqualTo(todo.getId());
        verify(todoRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void shouldGetTodoById() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        
        TodoResponse todoResponse = todoService.getTodoById(1L);
        
        assertThat(todoResponse.getId()).isEqualTo(todo.getId());
        assertThat(todoResponse.getTitle()).isEqualTo(todo.getTitle());
        verify(todoRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenTodoNotFound() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> todoService.getTodoById(99L));
        
        verify(todoRepository, times(1)).findById(99L);
    }

    @Test
    void shouldCreateTodo() {
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);
        
        TodoResponse createdTodo = todoService.createTodo(todoRequest);
        
        assertThat(createdTodo.getId()).isEqualTo(todo.getId());
        assertThat(createdTodo.getTitle()).isEqualTo(todoRequest.getTitle());
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void shouldUpdateTodo() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);
        
        TodoResponse updatedTodo = todoService.updateTodo(1L, todoRequest);
        
        assertThat(updatedTodo.getId()).isEqualTo(todo.getId());
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void shouldToggleTodoStatus() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);
        
        TodoResponse updatedTodo = todoService.toggleTodoStatus(1L);
        
        assertThat(updatedTodo.isCompleted()).isNotEqualTo(todo.isCompleted());
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void shouldDeleteTodo() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        doNothing().when(todoRepository).delete(todo);
        
        todoService.deleteTodo(1L);
        
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).delete(todo);
    }
}