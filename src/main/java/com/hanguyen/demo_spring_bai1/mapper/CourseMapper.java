package com.hanguyen.demo_spring_bai1.mapper;

import com.hanguyen.demo_spring_bai1.dto.response.CourseResponse;
import com.hanguyen.demo_spring_bai1.entity.Course;
import com.hanguyen.demo_spring_bai1.entity.Lecturer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "lecturer", source = "lecturer")
    @Mapping(target = "subject.subjectCode", source = "subject.subjectCode")
    @Mapping(target = "subject.name", source = "subject.name")
    @Mapping(target = "subject.credits", source = "subject.credits")
    @Mapping(target = "semester.name", source = "semester.name")
    CourseResponse toCourseResponse(Course course);
    @Mapping(target = "fullName", expression = "java(lecturer.getUser().getFirstname() + \" \" + lecturer.getUser().getLastname())")
    @Mapping(target = "degree", source = "degree")
    CourseResponse.LecturerResponse toLecturerResponse(Lecturer lecturer);
}