package com.careerpath.admin.user.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.careerpath.admin.user.dto.FeaturedCourseDTO;
import com.careerpath.admin.user.service.CourseService;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final CourseService courseService;

    public PublicController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/featured-courses")
    public List<FeaturedCourseDTO> getFeaturedCourses() {
        return courseService.getFeaturedCourses();
    }

    @GetMapping("/search")
    public List<FeaturedCourseDTO> searchCourses(@RequestParam String keyword) {
        return courseService.searchCourses(keyword);
    }
}
