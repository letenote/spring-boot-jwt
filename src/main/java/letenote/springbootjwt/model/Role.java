package letenote.springbootjwt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "role")
public class Role {
	@Id
	private String id;
	@NotNull(message = "name cannot be null")
	@NotEmpty(message = "name cannot be empty")
	@NotBlank(message = "name may not be blank")
	@Indexed(unique = true)
	private String name;
}
