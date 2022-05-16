package letenote.springbootjwt.repository;

import letenote.springbootjwt.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User,String> {
	@Query("{'email' : ?0}")
	User findUserByEmail(String email);
}
