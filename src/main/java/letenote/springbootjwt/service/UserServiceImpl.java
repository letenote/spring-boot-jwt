package letenote.springbootjwt.service;

import letenote.springbootjwt.model.Role;
import letenote.springbootjwt.model.User;
import letenote.springbootjwt.repository.RoleRepository;
import letenote.springbootjwt.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;

	@Override
	public User saveUser(User userRequest) throws Exception {
		Optional<User> userOptional = userRepository.findUserByEmail(userRequest.getEmail());
		if(userOptional.isPresent()){
			throw new Exception("User Exist");
		}else{
			User newUser = new User()
					.setEmail(userRequest.getEmail())
					.setPassword(userRequest.getPassword());
		log.info(":X: Saving new User {} in to Database..", userRequest.getEmail());
			return userRepository.save(newUser);
		}
	}

	@Override
	public Role saveRole(Role roleRequest) throws Exception {
		Optional<Role> roleOptional = roleRepository.findRoleByName(roleRequest.getName());
		if(roleOptional.isPresent()){
			throw new Exception("Role Exist");
		}else{
			Role newRole = new Role()
					.setName(roleRequest.getName());
			log.info(":X: Saving new Role {} in to Database..", roleRequest.getName());
			return roleRepository.save(newRole);
		}
	}

	@Override
	public void addRoleToUser(String email, String roleName) {
		User user = userRepository.findUserByEmail(email).get();
		Role role = roleRepository.findRoleByName(roleName).get();
		log.info(":X: Add new Role {} to User {}", roleName, email);
		user.getRoles().add(role);
		userRepository.save(user);
	}

	@Override
	public User getUser(String id) {
		log.info(":X: Fetching User {}", id);
		return userRepository.findById(id).get();
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findUserByEmail(email);
		if(!user.isPresent()){
			String message = "User not found in database";
			log.error(message);
			throw new UsernameNotFoundException(message);
		}else{
			log.info("User {} found in database", email);
		}
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		user.get().getRoles().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		});
		return new org.springframework.security.core.userdetails.User(user.get().getEmail(), user.get().getPassword(), authorities);
	}
}
