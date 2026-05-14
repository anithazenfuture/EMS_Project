package com.example.EMS.EmployeeSecurity;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.EMS.EmployeeRepository.UserRepository;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final Jwtutil jwtUtil;
    private final UserRepository userRepository;

    public JwtFilter(Jwtutil jwtUtil,
                     UserRepository userRepository) {

        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header =
                request.getHeader("Authorization");

        if (header == null ||
                !header.startsWith("Bearer ")) {

            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        if (!jwtUtil.isValid(token)) {

            chain.doFilter(request, response);
            return;
        }

        String email =
                jwtUtil.getEmailFromToken(token);

        userRepository.findByEmail(email)
                .ifPresent(user -> {

                    List<SimpleGrantedAuthority> authorities =user.getRoles()
                                    .stream()
                                    .map(role ->
                                            new SimpleGrantedAuthority("ROLE_" + role.name()))
                                    .toList();

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(user,null,authorities);

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(auth);
                });

        chain.doFilter(request, response);
    }
}