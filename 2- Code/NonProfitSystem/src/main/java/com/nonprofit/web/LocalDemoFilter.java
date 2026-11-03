package com.nonprofit.web;

import com.nonprofit.ApplicationServices;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Protects local forms and serializes access to the existing in-memory DAOs. */
@Component
@Order(1)
public class LocalDemoFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        var session = request.getSession();
        String csrf;
        synchronized (session) {
            csrf = (String) session.getAttribute("csrf");
            if (csrf == null) {
                csrf = UUID.randomUUID().toString();
                session.setAttribute("csrf", csrf);
            }
        }
        request.setAttribute("csrf", csrf);
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Cache-Control", "no-store");
        if ("POST".equals(request.getMethod()) && !csrf.equals(request.getParameter("_csrf"))) {
            response.sendError(403, "Reload the page before submitting the form.");
            return;
        }
        // The Phase 1 model uses mutable ArrayLists. Keep rendering and writes together.
        synchronized (ApplicationServices.getInstance()) {
            chain.doFilter(request, response);
        }
    }
}
