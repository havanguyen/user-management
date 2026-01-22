package com.hanguyen.registercourses.mapper;
import com.hanguyen.registercourses.dto.response.CourseResponse;
import com.hanguyen.registercourses.entity.Course;
import com.hanguyen.registercourses.entity.Lecturer;
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