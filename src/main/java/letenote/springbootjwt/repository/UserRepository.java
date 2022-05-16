package letenote.springbootjwt.repository;

import letenote.springbootjwt.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface UserRepository extends MongoRepository<User,String> {
	@Query("{'email' : ?0}")
	Optional<User> findUserByEmail(String email);
}
