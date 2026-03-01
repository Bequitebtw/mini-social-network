package com.bequitebtw.socialnetwork.user.mapper;


import com.bequitebtw.socialnetwork.user.dto.UserDto;
import com.bequitebtw.socialnetwork.user.dto.UserShort;
import com.bequitebtw.socialnetwork.user.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

	User toEntity(UserDto userDto);

	UserDto toDto(User user);

	UserShort toShort(User user);

	List<UserDto> toDto(List<User> users);

	List<User> toEntity(List<UserDto> usersDto);


}
