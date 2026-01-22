package com.hanguyen.registercourses.mapper;
import com.hanguyen.registercourses.dto.request.UserCreationRequest;
import com.hanguyen.registercourses.dto.request.UserUpdateRequest;
import com.hanguyen.registercourses.dto.response.UserResponse;
import com.hanguyen.registercourses.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);
    void updateUser(@MappingTarget User user , UserUpdateRequest userUpdateRequest);
}
