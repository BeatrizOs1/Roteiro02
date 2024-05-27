package com.labdesoft.roteiro01.unit.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.labdesoft.roteiro01.entity.Task;
import com.labdesoft.roteiro01.enums.Priority;
import com.labdesoft.roteiro01.enums.TaskType;
import com.labdesoft.roteiro01.repository.TaskRepository;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void testSaveTask() {
        // Given
        Task task = new Task(1L, "Task Description", TaskType.DATA, Priority.ALTA, null, null, false);

        // When
        Task savedTask = taskRepository.save(task);

        // Then
        assertNotNull(savedTask.getId());
    }

    @Test
    public void testFindById() {
        // Given
        Task task = new Task(1L, "Task Description", TaskType.DATA, Priority.ALTA, null, null, false);
        Task savedTask = taskRepository.save(task);

        // When
        Optional<Task> optionalTask = taskRepository.findById(savedTask.getId());

        // Then
        assertTrue(optionalTask.isPresent());
        assertEquals(task.getDescription(), optionalTask.get().getDescription());
    }

    @Test
    public void testUpdateTask() {
        // Given
        Task task = new Task(1L, "Task Description", TaskType.DATA, Priority.ALTA, null, null, false);
        Task savedTask = taskRepository.save(task);

        // When
        savedTask.setDescription("Updated Description");
        Task updatedTask = taskRepository.save(savedTask);

        // Then
        assertEquals("Updated Description", updatedTask.getDescription());
    }

    @Test
    public void testDeleteTask() {
        // Given
        Task task = new Task(1L,"Task Description", TaskType.DATA, Priority.ALTA, null, null, false);
        Task savedTask = taskRepository.save(task);

        // When
        taskRepository.deleteById(savedTask.getId());

        // Then
        assertFalse(taskRepository.existsById(savedTask.getId()));
    }
}
