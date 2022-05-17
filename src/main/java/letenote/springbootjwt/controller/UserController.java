package letenote.springbootjwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import letenote.springbootjwt.model.Role;
import letenote.springbootjwt.model.User;
import letenote.springbootjwt.service.UserServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {
	@Autowired
	UserServiceImpl userService;
	@GetMapping("/users/{id}")
	public ResponseEntity<?> getUser(@PathVariable String id) {
		try{
			var getUser = userService.getUserById(id);
			return ResponseEntity.status(HttpStatus.OK).body(getUser);
		} catch (Exception err){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
		}
	}
	@PostMapping("/users/register")
	public ResponseEntity<?> createUser(@RequestBody User user) {
		try{
			User saveUser = userService.saveUser(user);
			return ResponseEntity.status(HttpStatus.OK).body(saveUser);
		} catch (Exception err){
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User Conflict !!!");
		}
	}
	@PostMapping("/roles")
	public ResponseEntity<?> createRole(@RequestBody Role role) {
		try{
			Role saveRole = userService.saveRole(role);
			return ResponseEntity.status(HttpStatus.OK).body(saveRole);
		} catch (Exception err){
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Role Conflict !!!");
		}
	}
	@PostMapping("users/add-role")
	public String userLogin(@RequestBody RoleToUserForm roleToUserForm){
		userService.addRoleToUser(roleToUserForm.getEmail(), roleToUserForm.getRoleName());
		return "success";
	}
	@PostMapping("users/refresh/token")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		var newToken = userService.renewToken(request);
		response.setContentType(APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getOutputStream(), newToken);
	}
}
@Data
class RoleToUserForm{
	private String email;
	private String roleName;
}
