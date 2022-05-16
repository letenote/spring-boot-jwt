package letenote.springbootjwt.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;
	public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		log.info("Email is: {}", email);
		log.info("Password is: {}", password);
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(email, password);
		log.info("UsernamePasswordAuthenticationToken: {}", usernamePasswordAuthenticationToken);
		var auth = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
		log.info("::isAuthenticated::: {}", auth.isAuthenticated());
		return auth;
	}
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
		var principal =  authentication.getPrincipal();
		if (principal instanceof UserDetails) {
			String getEmail = ((UserDetails)principal).getUsername();
			Collection<? extends GrantedAuthority> authorities = ((UserDetails)principal).getAuthorities();

			Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
			String access_token = JWT.create()
					.withSubject(getEmail)
					.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
					.withIssuer(request.getRequestURL().toString())
					.withClaim("roles", authorities.stream().map(Object::toString).collect(Collectors.toList()))
					.sign(algorithm);
			String refresh_token = JWT.create()
					.withSubject(getEmail)
					.withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
					.withIssuer(request.getRequestURL().toString())
					.sign(algorithm);

			Map<String, String> tokens = new HashMap<>();
			tokens.put("access_token", access_token);
			tokens.put("refresh_token", refresh_token);
			response.setContentType(APPLICATION_JSON_VALUE);
			new ObjectMapper().writeValue(response.getOutputStream(), tokens);
		} else {
			String principalToString = principal.toString();
			log.error("::SecurityContextHolder not instanceof UserDetails:: = {}", principalToString);
		}
	}
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
		log.error("::Request::, {}", request.toString());
		log.error("::Response::, {}", response.getStatus());
		log.error("::Failed::, {}", failed.getMessage());
		Map<String, String> errResponse = new HashMap<>();
		errResponse.put("status", "200");
		errResponse.put("message", failed.getMessage());
		response.setContentType(APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getOutputStream(), errResponse);
	}
}
