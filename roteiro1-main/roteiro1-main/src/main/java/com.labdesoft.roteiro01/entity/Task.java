package com.labdesoft.roteiro01.entity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.labdesoft.roteiro01.enums.Priority;
import com.labdesoft.roteiro01.enums.TaskType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents the details of a task.")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 10, message = "Description must be at least 10 characters long")
    @Schema(description = "Description must be at least 10 characters long")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TaskType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Priority priority;

    private LocalDate dueDate;
    private Integer dueDays;
    private Boolean completed;

    public Task(String description) {
        this.description = description;
    }

    public String getCurrentStatus() {
        if (type == TaskType.DATA) {
            if (completed) return "Completed";
            if (dueDate == null) return "Scheduled";
            if (dueDate.isAfter(LocalDate.now()) || dueDate.isEqual(LocalDate.now())) return "Scheduled";
            long daysOverdue = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
            return daysOverdue + " day" + (daysOverdue != 1 ? "s" : "") + " overdue";
        } else if (type == TaskType.PRAZO) {
            if (completed) return "Completed";
            if (dueDays == null) return "Scheduled";
            LocalDate deadline = LocalDate.now().plusDays(dueDays);
            if (deadline.isAfter(LocalDate.now()) || deadline.isEqual(LocalDate.now())) return "Scheduled";
            long daysOverdue = ChronoUnit.DAYS.between(deadline, LocalDate.now());
            return daysOverdue + " day" + (daysOverdue != 1 ? "s" : "") + " overdue";
        } else {
            return completed ? "Completed" : "Scheduled";
        }
    }

    @Override
    public String toString() {
        return "Task[id=" + id + ", description=" + description + ", status=" + getCurrentStatus() + "]";
    }
}
