package dujardin.thomas.examTestsM1Dujardin.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dujardin.thomas.examTestsM1Dujardin.dto.UserDto;
import dujardin.thomas.examTestsM1Dujardin.exception.DataIntegrityViolationException;
import dujardin.thomas.examTestsM1Dujardin.exception.ObjectNotFoundException;
import dujardin.thomas.examTestsM1Dujardin.model.User;
import dujardin.thomas.examTestsM1Dujardin.repository.UserRepository;
import dujardin.thomas.examTestsM1Dujardin.tools.CrudService;

@Service
public class UserService implements CrudService<User, UserDto, Long> {

    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDto create(UserDto dto) {
        User user = convertDtoToDAO(dto);
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("defaultPassword123");
        }
        return convertDaoToDTO(userRepository.save(user));
    }

    public UserDto createWithPassword(User user) {
        if(userRepository.findByEmail(user.getEmail()) != null) {
            throw new DataIntegrityViolationException("Email already exists: " + user.getEmail());
        }
        return convertDaoToDTO(userRepository.save(user));
    }

    @Override
    public UserDto get(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User not found with id: " + id));
        return convertDaoToDTO(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                    .map(this::convertDaoToDTO)
                    .toList();
    }

    @Override
    public UserDto update(UserDto dto, Long id) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) throw new ObjectNotFoundException("User not update because not found with id: " + id);
        existingUser.setName(dto.getName());
        existingUser.setEmail(dto.getEmail());
        User updatedUser = userRepository.save(existingUser);
        return convertDaoToDTO(updatedUser);
    }

    @Override
    public boolean delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ObjectNotFoundException("User not delete because not found with id: " + id);
        }
        userRepository.deleteById(id);
        return !userRepository.existsById(id);
    }

    @Override
    public UserDto convertDaoToDTO(User dao) {
        if (dao == null) return null;
        UserDto dto = new UserDto();
        dto.setId(dao.getId());
        dto.setName(dao.getName());
        dto.setEmail(dao.getEmail());
        return dto;
    }

    @Override
    public User convertDtoToDAO(UserDto dto) {
        if (dto == null) return null;
        User dao = new User();
        dao.setId(dto.getId());
        dao.setName(dto.getName());
        dao.setEmail(dto.getEmail());
        return dao;
    }


}
