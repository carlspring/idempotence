package org.carlspring.testing.idempotence.extensions;

import org.carlspring.testing.idempotence.annotation.WithDbSchema;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;


/**
 * @author carlspring
 */
public class DbSchemaExtension
        implements BeforeEachCallback
{

    private DataSource dataSource;

    private EntityManagerFactory entityManagerFactory;


    @Override
    public void beforeEach(ExtensionContext context)
            throws Exception
    {
        // Get Spring's ApplicationContext from the test context
        ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);

        // Manually get the DataSource and EntityManagerFactory beans from the context
        dataSource = applicationContext.getBean(DataSource.class);
        entityManagerFactory = applicationContext.getBean(EntityManagerFactory.class);

        // Now process the @WithDbSchema annotation and handle schema creation
        Method testMethod = context.getRequiredTestMethod();
        WithDbSchema annotation = testMethod.getAnnotation(WithDbSchema.class);

        if (annotation != null)
        {
            String schema = annotation.value();

            System.setProperty("org.postgresql.Driver", "ALL");

            createSchemaIfNotExists(schema);
            switchToSchema(schema);
            createTablesInSchema(schema);
        }
    }

    private void createSchemaIfNotExists(String schema)
            throws SQLException
    {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement())
        {
            String createSchemaSQL = String.format("CREATE SCHEMA IF NOT EXISTS %s", schema);
            stmt.execute(createSchemaSQL);
        }
    }

    private void switchToSchema(String schema)
            throws SQLException
    {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement())
        {
            String setSchemaSQL = String.format("SET SCHEMA '%s'", schema);
            stmt.execute(setSchemaSQL);
        }
    }

    private void createTablesInSchema(String schema)
    {
        // Get the underlying Hibernate SessionFactory
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        // Set the schema for the Hibernate connection
        try (Session session = sessionFactory.openSession())
        {
            session.doWork(connection -> {
                connection.setSchema(schema);
                try (Statement stmt = connection.createStatement())
                {
                    stmt.execute(String.format("SET search_path TO %s", schema));
                }
            });

            // Modify Hibernate settings to set the schema explicitly
            Map<String, Object> settings = new HashMap<>(sessionFactory.getProperties());
            settings.put("hibernate.default_schema", schema);  // Ensure the schema is set for Hibernate

            // Create a new ServiceRegistry with the updated settings
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(settings).build();

            // Build MetadataSources using the ServiceRegistry with updated schema settings
            MetadataSources metadataSources = new MetadataSources(serviceRegistry);
            Metadata metadata = metadataSources.buildMetadata();

            // Generate the tables using Hibernate's SchemaExport
            SchemaExport schemaExport = new SchemaExport();
            schemaExport.create(EnumSet.of(TargetType.DATABASE), metadata);
        }
    }

}
