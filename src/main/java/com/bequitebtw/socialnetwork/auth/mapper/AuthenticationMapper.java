package com.bequitebtw.socialnetwork.auth.mapper;

import com.bequitebtw.socialnetwork.auth.dto.AuthenticationRequest;
import com.bequitebtw.socialnetwork.auth.dto.AuthenticationResponse;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AuthenticationMapper {
	AuthenticationResponse toResponse(AuthenticationRequest authenticationRequest);
}
