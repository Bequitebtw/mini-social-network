package com.bequitebtw.socialnetwork.registration.mapper;


import com.bequitebtw.socialnetwork.registration.dto.RegistrationRequest;
import com.bequitebtw.socialnetwork.registration.dto.RegistrationResponse;
import com.bequitebtw.socialnetwork.registration.model.Registration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {
	@Mapping(target = "email", expression = "java(registrationRequest.email().toLowerCase())")
	@Mapping(target = "username", expression = "java(registrationRequest.username().toLowerCase())")
	Registration toEntity(RegistrationRequest registrationRequest);

	List<Registration> toEntity(List<RegistrationRequest> registrationRequestsDto);

	RegistrationRequest toDto(Registration registration);

	List<RegistrationRequest> toDto(List<Registration> registrations);

	RegistrationResponse toResponse(Registration registration);
}
