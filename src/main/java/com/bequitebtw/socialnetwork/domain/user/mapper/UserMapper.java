package com.bequitebtw.socialnetwork.domain.user.mapper;


import com.bequitebtw.socialnetwork.domain.user.dto.UserDto;
import com.bequitebtw.socialnetwork.domain.user.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

	User toEntity(UserDto userDto);

	UserDto toDto(User user);

	List<UserDto> toDto(List<User> users);

	List<User> toEntity(List<UserDto> usersDto);
}
