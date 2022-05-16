package letenote.springbootjwt.repository;

import letenote.springbootjwt.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role,String> {
	@Query("{'name' : ?0}")
	Optional<Role> findRoleByName(String name);
}