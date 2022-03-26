package pdl.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pdl.backend.model.User;
import pdl.backend.repository.UserRepository;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext configurableApplicationContext =
		SpringApplication.run(BackendApplication.class, args);

		UserRepository userRepository = configurableApplicationContext.getBean(UserRepository.class);

		User myuser = new User("bro", "Doe");

		userRepository.save(myuser);

	}

}
