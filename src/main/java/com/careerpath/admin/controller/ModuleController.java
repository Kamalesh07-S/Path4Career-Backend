package com.careerpath.admin.controller;

import com.careerpath.admin.dto.ModuleRequest;
import com.careerpath.admin.dto.ModuleResponse;
import com.careerpath.admin.entity.Module;
import com.careerpath.admin.service.ModuleService;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;



@RestController
@RequestMapping("/api/modules")
@CrossOrigin
public class ModuleController {

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN','CONTENT_ADMIN')")
    @PostMapping
    public ModuleResponse create(@RequestBody ModuleRequest req) {
        return toResponse(moduleService.create(req));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','CONTENT_ADMIN')")
    @PutMapping("/{id}")
    public ModuleResponse update(@PathVariable Long id,
                                 @RequestBody ModuleRequest req) {
        return toResponse(moduleService.update(id, req));
    }
    
    @GetMapping("/{id}")
    public ModuleResponse get(@PathVariable Long id) {
        return toResponse(moduleService.get(id));
    }

    @GetMapping
    public List<ModuleResponse> getAll() {
        return moduleService.getAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','CONTENT_ADMIN')")
    @PutMapping("/{id}/toggle")
    public ModuleResponse toggle(@PathVariable Long id) {
        Module m = moduleService.get(id);
        m.setActive(!m.isActive());

        // reuse update path so roadmap recalculation is respected
        return toResponse(moduleService.update(
                id,
                ModuleRequest.fromEntity(m)
        ));
    }


    
    // mapper
    private ModuleResponse toResponse(Module m) {
        ModuleResponse r = new ModuleResponse();
        r.id = m.getId();
        r.name = m.getName();
        r.description = m.getDescription();
        r.durationValue = m.getDurationValue();
        r.durationUnit = m.getDurationUnit().name();
        r.difficulty = m.getDifficulty().name();
        r.active = m.isActive();        // ✅
        r.createdAt = m.getCreatedAt(); 
        r.lastModifiedAt = m.getLastModifiedAt();
        r.skills = m.getSkills().stream()
                .map(s -> new ModuleResponse.SkillMini(s.getId(), s.getName(),s.isActive()))
                .collect(Collectors.toList());
        return r;
    }
    
}
