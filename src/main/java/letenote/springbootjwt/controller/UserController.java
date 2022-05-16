package letenote.springbootjwt.controller;

import letenote.springbootjwt.model.User;
import letenote.springbootjwt.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
	@Autowired
	UserServiceImpl userService;
	@GetMapping("/users")
	public ResponseEntity<?> getUser(@RequestBody String email) {
		try{
			var getUser = userService.getUser(email);
			return ResponseEntity.status(HttpStatus.OK).body(getUser);
		} catch (Exception err){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
		}
	}
	@PostMapping("/users")
	public ResponseEntity<?> createUser(@RequestBody User user) {
		try{
			User saveUser = userService.saveUser(user);
			return ResponseEntity.status(HttpStatus.OK).body(saveUser);
		} catch (Exception err){
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User Conflict !!!");
		}
	}
}
