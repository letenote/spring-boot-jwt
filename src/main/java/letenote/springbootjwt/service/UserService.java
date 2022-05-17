package letenote.springbootjwt.service;

import letenote.springbootjwt.model.Role;
import letenote.springbootjwt.model.User;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface UserService {
	User saveUser(User userRequest) throws Exception;
	Role saveRole(Role roleRequest) throws Exception;
	void addRoleToUser(String email, String roleName);
	User getUserById(String id);
	User getUserByEmail(String email);
	Map<String, String> renewToken(HttpServletRequest request);
}
