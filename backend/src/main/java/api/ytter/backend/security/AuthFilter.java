package api.ytter.backend.security;

import api.ytter.backend.exception.exception_types.AuthorizationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final SecretKey jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getHeader("Authorization") != null) {

            String JWT = request.getHeader("Authorization").replaceFirst("Bearer ", "");
            Claims claims;
            try {
                claims = Jwts.parser().verifyWith(jwtSecret).build().parseSignedClaims(JWT).getPayload();
            } catch (RuntimeException e) {
                throw new AuthorizationException("Invalid token");
            }

            if (claims.getExpiration().before(new Date())) {
                throw new AuthorizationException("Expired token");
            }

            request.setAttribute("username", claims.getSubject());
            request.setAttribute("isAdmin", claims.get("admin"));

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), null, null);
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}
