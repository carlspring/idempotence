package org.carlspring.testing.idempotence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author carlspring
 */
@SpringBootApplication(scanBasePackages = "org.carlspring.testing.idempotence")
public class IdempotenceApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(IdempotenceApplication.class, args);
    }

}
