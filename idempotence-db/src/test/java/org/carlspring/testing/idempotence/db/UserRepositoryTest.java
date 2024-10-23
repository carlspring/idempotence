package org.carlspring.testing.idempotence.db;

import org.carlspring.testing.idempotence.annotation.WithDbSchema;
import org.carlspring.testing.idempotence.config.DbConfig;
import org.carlspring.testing.idempotence.entities.User;
import org.carlspring.testing.idempotence.extensions.DbSchemaExtension;
import org.carlspring.testing.idempotence.repository.UserRepository;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author carlspring
 */
@SpringBootTest(classes = { DbConfig.class })
@ExtendWith(DbSchemaExtension.class)
class UserRepositoryTest
{

    @Autowired
    private UserRepository userRepository;


    // The custom @UseSchema annotation we created to switch schema
    @WithDbSchema("test_schema")
    @Test
    void testUserCrudOperations()
    {
        // Create a new user
        User newUser = new User("John Doe", "john.doe@example.com");
        User savedUser = userRepository.save(newUser);

        // Check that the user was saved
        assertNotNull(savedUser.getId());
        assertEquals("John Doe", savedUser.getName());

        // Read user by ID
        Optional<User> fetchedUser = userRepository.findById(savedUser.getId());
        assertTrue(fetchedUser.isPresent());
        assertEquals("john.doe@example.com", fetchedUser.get().getEmail());

        // Update user
        User userToUpdate = fetchedUser.get();
        userToUpdate.setName("John Updated");
        userRepository.save(userToUpdate);

        // Check the updated name
        Optional<User> updatedUser = userRepository.findById(userToUpdate.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals("John Updated", updatedUser.get().getName());

        try
        {
            Thread.sleep(30000);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }

//        // Delete user
//        userRepository.deleteById(userToUpdate.getId());
//
//        // Verify that the user was deleted
//        Optional<User> deletedUser = userRepository.findById(userToUpdate.getId());
//        assertFalse(deletedUser.isPresent());
    }

}
