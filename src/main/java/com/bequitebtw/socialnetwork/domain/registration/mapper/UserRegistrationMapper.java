package com.bequitebtw.socialnetwork.domain.registration.mapper;


import com.bequitebtw.socialnetwork.domain.registration.dto.UserRegistrationDto;
import com.bequitebtw.socialnetwork.domain.registration.dto.UserRegistrationResponse;
import com.bequitebtw.socialnetwork.domain.registration.entity.UserRegistration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserRegistrationMapper {
	UserRegistration toEntity(UserRegistrationDto userRegistrationDto);

	List<UserRegistration> toEntity(List<UserRegistrationDto> registrationRequestsDto);

	UserRegistrationDto toDto(UserRegistration userRegistration);

	List<UserRegistrationDto> toDto(List<UserRegistration> userRegistrations);

	UserRegistrationResponse toResponse(UserRegistration userRegistration);
}
