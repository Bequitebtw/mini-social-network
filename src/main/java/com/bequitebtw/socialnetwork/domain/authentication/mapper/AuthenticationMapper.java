package com.bequitebtw.socialnetwork.domain.authentication.mapper;

import com.bequitebtw.socialnetwork.domain.authentication.dto.AuthenticationRequest;
import com.bequitebtw.socialnetwork.domain.authentication.dto.AuthenticationResponse;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AuthenticationMapper {
	AuthenticationResponse toResponse(AuthenticationRequest authenticationRequest);
}
