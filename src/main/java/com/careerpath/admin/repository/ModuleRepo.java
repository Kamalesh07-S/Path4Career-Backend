package com.careerpath.admin.repository;

import com.careerpath.admin.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ModuleRepo extends JpaRepository<Module, Long> {

    boolean existsByNameIgnoreCase(String name);

    List<Module> findBySkillsId(Long skillId);
}
