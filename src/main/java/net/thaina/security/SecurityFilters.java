package net.thaina.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.thaina.exceptions.ErrorDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class SecurityFilters extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filter) throws ServletException, IOException {
        if (req.getHeader("Authorization") != null) {
            Authentication auth = TokenUtil.decodeToken(req);
            if (auth != null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }else{
                ErrorDTO error = new ErrorDTO(401, "Usuario sem a permicao adquada!");
                res.setStatus(error.status());
                res.setContentType("application/json");
                ObjectMapper mapper = new ObjectMapper();
                res.getWriter().println(mapper.writeValueAsString(error));
                res.getWriter().flush();
                return;
            }
        }
        filter.doFilter(req, res);
    }
}
