package dujardin.thomas.examTestsM1Dujardin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dujardin.thomas.examTestsM1Dujardin.model.User;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
