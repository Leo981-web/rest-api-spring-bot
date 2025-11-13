package br.edu.atitus.api_example.components;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.edu.atitus.api_example.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

	private final UserService userservice;
	
	
	public AuthTokenFilter(UserService userservice) {
		super();
		this.userservice = userservice;
	}


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String jwt = JwtUtils.getJwtFromRequest(request);
		if(jwt != null) {
			String email = JwtUtils.validateToken(jwt);
			if(email != null) {
				var user = userservice.loadUserByUsername(email);
				if(user != null) {
					var auth = new UsernamePasswordAuthenticationToken(user, null, null);
					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}
		}
		
		filterChain.doFilter(request, response);
	}

}
