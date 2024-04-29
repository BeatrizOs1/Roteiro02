package com.labdesoft.roteiro01.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.labdesoft.roteiro01.entity.Task;
import com.labdesoft.roteiro01.enums.TaskType;
import com.labdesoft.roteiro01.repository.TaskRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/task")
    @Operation(summary = "Retrieves all tasks")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        taskRepository.findAll().forEach(tasks::add);
        if (tasks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @PostMapping("/task")
    @Operation(summary = "Creates a new task")
    public ResponseEntity<EntityModel<Task>> createTask(@RequestBody Task task) {
        if (task.getType() == TaskType.DATA && task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Task savedTask = taskRepository.save(task);
        EntityModel<Task> resource = EntityModel.of(savedTask,
                linkTo(methodOn(TaskController.class).getTaskById(savedTask.getId())).withSelfRel(),
                linkTo(methodOn(TaskController.class).updateTask(savedTask.getId(), savedTask)).withRel("update"),
                linkTo(methodOn(TaskController.class).deleteTask(savedTask.getId())).withRel("delete"));
        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    @PutMapping("/task/{id}")
    @Operation(summary = "Updates a task by ID")
    public ResponseEntity<EntityModel<Task>> updateTask(@PathVariable("id") long id, @RequestBody Task task) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent()) {
            Task updatedTask = taskRepository.save(task);
            EntityModel<Task> resource = EntityModel.of(updatedTask,
                    linkTo(methodOn(TaskController.class).getTaskById(id)).withSelfRel(),
                    linkTo(methodOn(TaskController.class).updateTask(id, updatedTask)).withRel("update"),
                    linkTo(methodOn(TaskController.class).deleteTask(id)).withRel("delete"));
            return new ResponseEntity<>(resource, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/task/{id}")
    @Operation(summary = "Deletes a task by ID")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/task/{id}/complete")
    @Operation(summary = "Marks a task as completed")
    public ResponseEntity<EntityModel<Task>> completeTask(@PathVariable("id") Long id) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent()) {
            Task task = existingTask.get();
            task.setCompleted(true);
            Task completedTask = taskRepository.save(task);

            EntityModel<Task> resource = EntityModel.of(completedTask,
                    linkTo(methodOn(TaskController.class).getTaskById(id)).withSelfRel(),
                    linkTo(methodOn(TaskController.class).updateTask(id, completedTask)).withRel("update"),
                    linkTo(methodOn(TaskController.class).deleteTask(id)).withRel("delete"));

            return new ResponseEntity<>(resource, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
