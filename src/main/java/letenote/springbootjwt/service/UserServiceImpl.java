package letenote.springbootjwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import letenote.springbootjwt.model.Role;
import letenote.springbootjwt.model.User;
import letenote.springbootjwt.repository.RoleRepository;
import letenote.springbootjwt.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	private MappingJacksonValue mappingUserPropsFiltering(MappingJacksonValue mapper, Set<String> includeProperties){
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(includeProperties);
		FilterProvider filters = new SimpleFilterProvider().addFilter("UserPropsFiltering", filter);
		mapper.setFilters(filters);
		return mapper;
	}
	@Override
	public MappingJacksonValue findUsers() {
		var users = userRepository.findAll();
		return this.mappingUserPropsFiltering(
				new MappingJacksonValue(users),
				Set.of("id","email","roles")
		);
	}
	@Override
	public MappingJacksonValue saveUser(User userRequest) throws Exception {
		Optional<User> userOptional = userRepository.findUserByEmail(userRequest.getEmail());
		if(userOptional.isPresent()){
			throw new Exception("User Exist");
		}else{
			User newUser = new User()
					.setId(String.format("USER::%S", UUID.randomUUID()))
					.setEmail(userRequest.getEmail())
					.setPassword(new BCryptPasswordEncoder().encode(userRequest.getPassword()));
			log.info("::Service:: Saving new User {} in to Database..", userRequest.getEmail());
			var userSaved = userRepository.save(newUser);
			return this.mappingUserPropsFiltering(
					new MappingJacksonValue(userSaved),
					Set.of("id","email","roles")
			);
		}
	}
	@Override
	public Role saveRole(Role roleRequest) throws Exception {
		Optional<Role> roleOptional = roleRepository.findRoleByName(roleRequest.getName());
		if(roleOptional.isPresent()){
			throw new Exception("Role Exist");
		}else{
			Role newRole = new Role()
					.setId(String.format("ROLE::%S", UUID.randomUUID()))
					.setName(roleRequest.getName());
			log.info("::Service:: Saving new Role {} in to Database..", roleRequest.getName());
			return roleRepository.save(newRole);
		}
	}
	@Override
	public void addRoleToUser(String email, String roleName) {
		User user = userRepository.findUserByEmail(email).get();
		Role role = roleRepository.findRoleByName(roleName).get();
		log.info("::Service:: Add new Role {} to User {}", roleName, email);
		user.getRoles().add(role);
		userRepository.save(user);
	}
	@Override
	public MappingJacksonValue getUserById(String id) {
		log.info("::Service:: Fetching User {}", id);
		var getUser = userRepository.findById(id).get();
		return this.mappingUserPropsFiltering(
				new MappingJacksonValue(getUser),
				Set.of("id","email","roles")
		);
	}
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findUserByEmail(email).get();
		if(user == null){
			String message = "::UserDetails:: User not found in database";
			log.error(message);
			throw new UsernameNotFoundException(message);
		} else {
			log.info("::UserDetails:: User {} found in database", email);
			Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
			user.getRoles().forEach(role -> {
				authorities.add(new SimpleGrantedAuthority(role.getName()));
			});
			return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
		}
	}
	@Override
	public Map<String, String> renewToken(HttpServletRequest request) {
		String getRefreshToken = request.getHeader("Refresh-Token");
		if( getRefreshToken != null ) {
			try {
				Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(getRefreshToken);
				String email = decodedJWT.getSubject();

				User user = userRepository.findUserByEmail(email).get();

				String newAccessToken = JWT.create()
						.withSubject(user.getEmail())
						.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
						.withIssuer(request.getRequestURL().toString())
						.withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
						.sign(algorithm);

				Map<String, String> tokens = new HashMap<>();
				tokens.put("access_token", newAccessToken);
				tokens.put("refresh_token", getRefreshToken);
				return tokens;
			}catch (Exception err) {
				throw new RuntimeException(err.getMessage());
			}
		}else{
			throw new RuntimeException("Refresh token is missing !!!");
		}
	}
}
