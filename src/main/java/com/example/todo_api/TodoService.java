package com.example.todo_api;


import com.example.todo_api.model.dto.TodoRequest;
import com.example.todo_api.model.dto.TodoResponse;
import com.example.todo_api.exception.ResourceNotFoundException;
import com.example.todo_api.model.entity.Todo;
import com.example.todo_api.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<TodoResponse> getAllTodos() {
        return todoRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TodoResponse> getTodosByStatus(boolean completed) {
        return todoRepository.findByCompletedOrderByCreatedAtDesc(completed)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TodoResponse getTodoById(Long id) {
        Todo todo =  todoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Todo", "id", id));
        return mapToResponse(todo);
        }

    public TodoResponse createTodo(TodoRequest todoRequest){
        Todo todo = new Todo();
        todo.setTitle(todoRequest.getTitle());
        todo.setDescription(todoRequest.getDescription());
        todo.setCompleted(todoRequest.isCompleted());
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());

        Todo savedTodo = todoRepository.save(todo);
        return mapToResponse(savedTodo);
    }

    public TodoResponse updateTodo(Long id, TodoRequest todoRequest) {
        Todo existingTodo = todoRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Todo", "id", id));

        existingTodo.setTitle(todoRequest.getTitle());
        existingTodo.setDescription(todoRequest.getDescription());
        existingTodo.setCompleted(todoRequest.isCompleted());
        existingTodo.setUpdatedAt(LocalDateTime.now());
        
        return mapToResponse(existingTodo);
    }

    public TodoResponse toggleTodoStatus(Long id) {
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo", "id", id));
        
        existingTodo.setCompleted(!existingTodo.isCompleted());
        existingTodo.setUpdatedAt(LocalDateTime.now());
        
        Todo updatedTodo = todoRepository.save(existingTodo);
        
        return mapToResponse(updatedTodo);
    }

    public void deleteTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo", "id", id));
        
        todoRepository.delete(todo);
    }
    
    private TodoResponse mapToResponse(Todo todo) {
        TodoResponse response = new TodoResponse();
        response.setId(todo.getId());
        response.setTitle(todo.getTitle());
        response.setDescription(todo.getDescription());
        response.setCompleted(todo.isCompleted());
        response.setCreatedAt(todo.getCreatedAt());
        response.setUpdatedAt(todo.getUpdatedAt());
        return response;
    }
}