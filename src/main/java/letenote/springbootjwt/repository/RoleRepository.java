package letenote.springbootjwt.repository;

import letenote.springbootjwt.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface RoleRepository extends MongoRepository<Role,String> {
	@Query("{'name' : ?0}")
	Role findRoleByName(String name);
}