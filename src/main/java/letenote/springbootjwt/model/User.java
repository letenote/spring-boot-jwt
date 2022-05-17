package letenote.springbootjwt.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.*;
// https://javaee.github.io/tutorial/bean-validation002.html

import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "user")
@JsonFilter("UserFilterProps")
public class User {
	@Id
	private String id;
	@NotNull(message = "email cannot be null")
	@NotEmpty(message = "email cannot be empty")
	@NotBlank(message = "email may not be blank")
	@Email
	@Indexed(unique = true)
	private String email;
	@NotNull(message = "password cannot be null")
	@NotEmpty(message = "password cannot be empty")
	@NotBlank(message = "password may not be blank")
	private String password;
	private Collection<Role> roles = new ArrayList<>();
}
