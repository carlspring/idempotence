package org.carlspring.testing.idempotence.repository;

import org.carlspring.testing.idempotence.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author carlspring
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>
{

}
