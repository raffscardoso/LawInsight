package com.raffs.LawInsight.mapper;

import com.raffs.LawInsight.domain.User;
import com.raffs.LawInsight.dto.UserRequest;
import com.raffs.LawInsight.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        var user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBarNumber(request.getBarNumber());
        user.setRole(request.getRole());
        return user;
    }

    public UserResponse toResponse(User user) {
        var response = new UserResponse();
        response.setId(user.getId());
        response.setVersion(user.getVersion());
        response.setCreatedAt(user.getCreatedAt());
        response.setLastModifiedAt(user.getLastModifiedAt());
        response.setCreatedBy(user.getCreatedBy());
        response.setLastModifiedBy(user.getLastModifiedBy());

        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setBarNumber(user.getBarNumber());
        response.setRole(user.getRole());
        response.setActive(user.isActive());
        return response;
    }
}
