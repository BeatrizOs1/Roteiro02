package com.labdesoft.roteiro01.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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
    
    private final TaskRepository taskRepository;

    // Construtor que aceita um parâmetro do tipo TaskRepository
    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @SuppressWarnings("null")
    @GetMapping("/task")
    @Operation(summary = "Lista todas as tarefas da lista")
    public ResponseEntity<List<Task>> listAll() {
        try {
            List<Task> taskList = new ArrayList<Task>();
            taskRepository.findAll().forEach(taskList::add);
            if (taskList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(taskList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/task/{id}")
    @Operation(summary = "Busca uma tarefa pelo ID")
    public ResponseEntity<EntityModel<Task>> findTaskById(@PathVariable("id") long id) {
        Optional<Task> taskData = taskRepository.findById(id);

        if (taskData.isPresent()) {
            Task task = taskData.get();
            EntityModel<Task> model = EntityModel.of(task,
                    linkTo(methodOn(TaskController.class).findTaskById(id)).withSelfRel(),
                    linkTo(methodOn(TaskController.class).updateTask(id, task)).withRel("update"),
                    linkTo(methodOn(TaskController.class).deleteTask(id)).withRel("delete"));
            return new ResponseEntity<>(model, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/task")
    @Operation(summary = "Adiciona uma tarefa na lista")
    public ResponseEntity<EntityModel<Task>> addTask(@RequestBody Task task) {
        try {
            // Verifica se a tarefa é do tipo "Data" e a data prevista é igual ou superior à data atual
            if (task.getType() == TaskType.DATA && task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Retorna erro se a data prevista for anterior à data atual
            }
    
            Task newTask = taskRepository.save(task);
            EntityModel<Task> model = EntityModel.of(newTask,
                    linkTo(methodOn(TaskController.class).findTaskById(newTask.getId())).withSelfRel(),
                    linkTo(methodOn(TaskController.class).updateTask(newTask.getId(), newTask)).withRel("update"),
                    linkTo(methodOn(TaskController.class).deleteTask(newTask.getId())).withRel("delete"));
            return new ResponseEntity<>(model, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

    @PutMapping("/task/{id}")
    @Operation(summary = "Atualiza uma tarefa na lista pelo ID")
    public ResponseEntity<EntityModel<Task>> updateTask(@PathVariable("id") long id, @RequestBody Task task) {
        Optional<Task> taskData = taskRepository.findById(id);

        if (taskData.isPresent()) {
            Task updatedTask = taskRepository.save(task);
            EntityModel<Task> model = EntityModel.of(updatedTask,
                    linkTo(methodOn(TaskController.class).findTaskById(id)).withSelfRel(),
                    linkTo(methodOn(TaskController.class).updateTask(id, updatedTask)).withRel("update"),
                    linkTo(methodOn(TaskController.class).deleteTask(id)).withRel("delete"));
            return new ResponseEntity<>(model, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/task/{id}")
    @Operation(summary = "Deleta uma tarefa pelo ID")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") long id) {
        try {
            if (taskRepository.existsById(id)) {
                taskRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/task/{id}/complete")
    @Operation(summary = "Atualiza a tarefa como concluída")
    public ResponseEntity<EntityModel<Task>> completeTask(@PathVariable("id") Long id) {
        try {
            Optional<Task> taskData = taskRepository.findById(id);
    
            if (taskData.isPresent()) {
                Task existingTask = taskData.get();
                existingTask.setCompleted(true);
                Task updatedTask = taskRepository.save(existingTask);
    
                EntityModel<Task> model = EntityModel.of(updatedTask,
                        linkTo(methodOn(TaskController.class).findTaskById(id)).withSelfRel(),
                        linkTo(methodOn(TaskController.class).updateTask(id, updatedTask)).withRel("update"),
                        linkTo(methodOn(TaskController.class).deleteTask(id)).withRel("delete"));
    
                return new ResponseEntity<>(model, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
