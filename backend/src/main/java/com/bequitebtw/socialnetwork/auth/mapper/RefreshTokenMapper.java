package com.bequitebtw.socialnetwork.auth.mapper;

import com.bequitebtw.socialnetwork.auth.dto.RefreshTokenResponse;
import com.bequitebtw.socialnetwork.auth.model.RefreshToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
	RefreshTokenResponse toResponse(RefreshToken refreshToken);
}
