package letenote.springbootjwt.service;

import letenote.springbootjwt.model.Role;
import letenote.springbootjwt.model.User;

public interface UserService {
	User saveUser(User userRequest) throws Exception;
	Role saveRole(Role roleRequest) throws Exception;
	void addRoleToUser(String email, String roleName);
	User getUser(String email);
}
