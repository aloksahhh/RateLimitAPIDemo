package com.example.RateLimitDemoApplication.filter;

import com.example.RateLimitDemoApplication.service.RateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;

    public RateLimitFilter(RateLimiterService rateLimiterService){
        this.rateLimiterService=rateLimiterService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String userId = request.getHeader("X-User-Id");

        if(userId==null|| userId.isBlank()){
            userId="anonymous";
        }

        String ipAddress= request.getRemoteAddr();

        boolean allowed =rateLimiterService.isAllowed(userId,ipAddress);

        if(!allowed){
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many request.please try again later.");
            return;
        }

        filterChain.doFilter(request,response);

    }
}
