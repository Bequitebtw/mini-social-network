package com.bequitebtw.socialnetwork.domain.registration.mapper;


import com.bequitebtw.socialnetwork.domain.registration.dto.RegistrationRequest;
import com.bequitebtw.socialnetwork.domain.registration.dto.RegistrationResponse;
import com.bequitebtw.socialnetwork.domain.registration.entity.Registration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {
	Registration toEntity(RegistrationRequest registrationRequest);

	List<Registration> toEntity(List<RegistrationRequest> registrationRequestsDto);

	RegistrationRequest toDto(Registration registration);

	List<RegistrationRequest> toDto(List<Registration> registrations);

	RegistrationResponse toResponse(Registration registration);
}
