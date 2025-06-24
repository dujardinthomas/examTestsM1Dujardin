package dujardin.thomas.examTestsM1Dujardin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dujardin.thomas.examTestsM1Dujardin.dto.UserDto;
import dujardin.thomas.examTestsM1Dujardin.exception.DataIntegrityViolationException;
import dujardin.thomas.examTestsM1Dujardin.exception.ObjectNotFoundException;
import dujardin.thomas.examTestsM1Dujardin.model.User;
import dujardin.thomas.examTestsM1Dujardin.repository.UserRepository;
import dujardin.thomas.examTestsM1Dujardin.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User defaultUser;
    private User secondaryUser;

    @BeforeEach
    void setUp() {
        defaultUser = new User();
        defaultUser.setId(1L);
        defaultUser.setName("Alice Smith");
        defaultUser.setEmail("alice.smith@test.com");
        defaultUser.setPassword("securePass");

        secondaryUser = new User();
        secondaryUser.setId(2L);
        secondaryUser.setName("Bob Wilson");
        secondaryUser.setEmail("bob.wilson@test.com");
        secondaryUser.setPassword("myPassword");
    }

    @Test
    void shouldFetchAllUsersSuccessfully() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(defaultUser, secondaryUser));

        List<UserDto> userList = userService.getAll();

        assertEquals(2, userList.size());
        assertEquals("Alice Smith", userList.get(0).getName());
        assertEquals("alice.smith@test.com", userList.get(0).getEmail());
        assertEquals("Bob Wilson", userList.get(1).getName());
        assertEquals("bob.wilson@test.com", userList.get(1).getEmail());
    }

    @Test
    void shouldRetrieveUserByValidId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(defaultUser));

        UserDto retrievedUser = userService.get(1L);

        assertEquals("Alice Smith", retrievedUser.getName());
        assertEquals("alice.smith@test.com", retrievedUser.getEmail());
        assertEquals(1L, retrievedUser.getId());
    }

    @Test
    void shouldThrowExceptionWhenUserIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> userService.get(999L));
    }

    @Test
    void shouldCreateNewUserSuccessfully() {
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Charlie Brown");
        newUserDto.setEmail("charlie.brown@test.com");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        UserDto createdUser = userService.create(newUserDto);

        assertEquals("Charlie Brown", createdUser.getName());
        assertEquals("charlie.brown@test.com", createdUser.getEmail());
        assertNotNull(createdUser.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailExistsOnUserCreation() {
        User newUserWithConflict = new User();
        newUserWithConflict.setName("Diana Prince");
        newUserWithConflict.setEmail("bob.wilson@test.com");
        newUserWithConflict.setPassword("wonderPass");

        when(userRepository.findByEmail("bob.wilson@test.com")).thenReturn(secondaryUser);

        assertThrows(DataIntegrityViolationException.class, () -> userService.createWithPassword(newUserWithConflict));
        verify(userRepository).findByEmail("bob.wilson@test.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldUpdateExistingUserData() {
        UserDto updateData = new UserDto();
        updateData.setName("New Name");
        updateData.setEmail("new.email@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(defaultUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto updatedUser = userService.update(updateData, 1L);

        assertEquals("New Name", updatedUser.getName());
        assertEquals("new.email@test.com", updatedUser.getEmail());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldPreservePasswordWhenUpdatingOtherFields() {
        UserDto updateData = new UserDto();
        updateData.setName("Alice Updated");
        updateData.setEmail("alice.updated@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(defaultUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User updatedUser = invocation.getArgument(0);
            assertEquals("securePass", updatedUser.getPassword());
            return updatedUser;
        });

        UserDto result = userService.update(updateData, 1L);

        assertEquals("Alice Updated", result.getName());
        assertEquals("alice.updated@test.com", result.getEmail());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        UserDto updateData = new UserDto();
        updateData.setName("Test Name");
        updateData.setEmail("test@test.com");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> userService.update(updateData, 999L));
    }

    @Test
    void shouldDeleteUserWhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true, false);

        boolean deletionResult = userService.delete(1L);

        assertTrue(deletionResult);
        verify(userRepository).deleteById(1L);
        verify(userRepository, times(2)).existsById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> userService.delete(999L));
        verify(userRepository, never()).deleteById(999L);
    }

    @Test
    void shouldHandleNullReturnFromFindByEmail() {
        User newValidUser = new User();
        newValidUser.setName("Frank Miller");
        newValidUser.setEmail("frank.miller@test.com");
        newValidUser.setPassword("frankPass");

        when(userRepository.findByEmail("frank.miller@test.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(4L);
            return savedUser;
        });

        UserDto createdUser = userService.createWithPassword(newValidUser);

        assertEquals("Frank Miller", createdUser.getName());
        assertEquals("frank.miller@test.com", createdUser.getEmail());
        assertEquals(4L, createdUser.getId());
        verify(userRepository).findByEmail("frank.miller@test.com");
        verify(userRepository).save(any(User.class));
    }

}
