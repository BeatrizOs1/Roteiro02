package com.labdesoft.roteiro01.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.labdesoft.roteiro01.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
