package com.careerpath.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.careerpath.admin.entity.Skill;

import java.util.Optional;

public interface SkillRepo extends JpaRepository<Skill, Long> {
	boolean existsByNameAndCategoryAndLevel(String name, String category, String level);

	Optional<Skill> findFirstByTutorialId(String tutorialId);
}
