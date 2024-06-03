package com.siperes.siperes.service;

import com.siperes.siperes.enumeration.EnumTokenAccessType;
import com.siperes.siperes.exception.DataNotFoundException;
import com.siperes.siperes.exception.ForbiddenException;
import com.siperes.siperes.exception.MissingTokenException;
import com.siperes.siperes.exception.ServiceBusinessException;
import com.siperes.siperes.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import static com.siperes.siperes.common.util.Constants.ErrorMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutHandler {
    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String jwt;
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new MissingTokenException("Unauthorized");
            }
            jwt = authHeader.substring(7);
            tokenRepository.findByToken(jwt).ifPresentOrElse(
                    token -> {
                        if (!token.getTokenAccessType().equals(EnumTokenAccessType.ACCESS)) {
                            throw new ForbiddenException(TOKEN_TYPE_INVALID);
                        }
                        token.setExpired(true);
                        token.setRevoked(true);
                        tokenRepository.save(token);
                        SecurityContextHolder.clearContext();
                    },
                    () -> {
                        throw new DataNotFoundException(TOKEN_NOT_FOUND);
                    }
            );
        } catch (DataNotFoundException | MissingTokenException | ForbiddenException e) {
            log.info(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Logout Failed: {}", e.getMessage());
            throw new ServiceBusinessException(LOGOUT_FAILED);
        }
    }
}
