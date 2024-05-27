package com.labdesoft.roteiro01.unit.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.labdesoft.roteiro01.controller.TaskController;
import com.labdesoft.roteiro01.entity.Task;
import com.labdesoft.roteiro01.enums.Priority;
import com.labdesoft.roteiro01.enums.TaskType;
import com.labdesoft.roteiro01.repository.TaskRepository;

public class TaskControllerTest {

    private TaskController taskController;
    private TaskRepository taskRepositoryMock;

    @BeforeEach
    void setUp() {
        taskRepositoryMock = mock(TaskRepository.class);
        taskController = new TaskController(taskRepositoryMock);
    }

    @Test
    void testFindTaskById() {
        // Given
        long taskId = 1L;
        Task task = new Task(1L, "Task 1", TaskType.DATA, Priority.ALTA, null, null, false);
        when(taskRepositoryMock.findById(taskId)).thenReturn(Optional.of(task));

        // When
        ResponseEntity<EntityModel<Task>> response = taskController.findTaskById(taskId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @SuppressWarnings("null")
    @Test
    void testListAll() {
        // Given
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(1L, "Task 1", TaskType.DATA, Priority.ALTA, null, null, false));
        tasks.add(new Task(2L, "Task 2", TaskType.DATA, Priority.BAIXA, null, null, false));
        when(taskRepositoryMock.findAll()).thenReturn(tasks);

        // When
        ResponseEntity<List<Task>> response = taskController.listAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks.size(), response.getBody().size());
    }

    @Test
    void testAddTask() {
        // Given
        Task newTask = new Task(1L, "Task 1", TaskType.DATA, Priority.ALTA, null, null, false);
        when(taskRepositoryMock.save(newTask)).thenReturn(newTask);

        // When
        ResponseEntity<EntityModel<Task>> response = taskController.addTask(newTask);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testUpdateTask() {
        // Given
        long taskId = 1L;
        Task updatedTask = new Task(1L, "Updated Task", TaskType.DATA, Priority.MEDIA, null, null, false);
        when(taskRepositoryMock.findById(taskId)).thenReturn(Optional.of(updatedTask));
        when(taskRepositoryMock.save(updatedTask)).thenReturn(updatedTask);

        // When
        ResponseEntity<EntityModel<Task>> response = taskController.updateTask(taskId, updatedTask);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteTask() {
        // Given
        long taskId = 1L;
        when(taskRepositoryMock.existsById(taskId)).thenReturn(true);

        // When
        ResponseEntity<Void> response = taskController.deleteTask(taskId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @SuppressWarnings("null")
    @Test
    void testCompleteTask() {
        // Given
        long taskId = 1L;
        Task existingTask = new Task(1L, "Task 1", TaskType.DATA, Priority.ALTA, null, null, false);
        when(taskRepositoryMock.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepositoryMock.save(existingTask)).thenReturn(existingTask);

        // When
        ResponseEntity<EntityModel<Task>> response = taskController.completeTask(taskId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getContent().getCompleted()); // Verifica se a tarefa foi marcada como concluída
    }
}
