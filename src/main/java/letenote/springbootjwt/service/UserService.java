package letenote.springbootjwt.service;

import letenote.springbootjwt.model.Role;
import letenote.springbootjwt.model.User;
import org.springframework.http.converter.json.MappingJacksonValue;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface UserService {
	MappingJacksonValue saveUser(User userRequest) throws Exception;
	Role saveRole(Role roleRequest) throws Exception;
	void addRoleToUser(String email, String roleName);
	MappingJacksonValue getUserById(String id);
	Map<String, String> renewToken(HttpServletRequest request);
	MappingJacksonValue findUsers();
}
