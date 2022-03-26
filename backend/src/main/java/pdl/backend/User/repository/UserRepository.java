package pdl.backend.User.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pdl.backend.User.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {



}
