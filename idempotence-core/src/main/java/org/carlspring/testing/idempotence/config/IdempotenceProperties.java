package org.carlspring.testing.idempotence.config;

/**
 * Defines the configuration properties for the idempotence test resource framework.
 *
 * @author carlspring
 */
public interface IdempotenceProperties
{

    /**
     * Returns the base directory where test resources will be copied to.
     *
     * @return the base directory path
     */
    String getBasedir();

    /**
     * Sets the base directory where test resources will be copied to.
     *
     * @param basedir the base directory path to set
     */
    void setBasedir(String basedir);

    /**
     * Returns whether fully-qualified class name prefixes should be used when determining
     * the test resource directory structure.
     *
     * @return {@code true} if fully-qualified name prefixes should be used, {@code false} otherwise
     */
    boolean useFullyQualifiedNamePrefixes();

    /**
     * Sets whether fully-qualified class name prefixes should be used when determining
     * the test resource directory structure.
     *
     * @param useFullyQualifiedClassNamePrefixes {@code true} to use fully-qualified name prefixes
     */
    void setUseFullyQualifiedClassNamePrefixes(boolean useFullyQualifiedClassNamePrefixes);

    /**
     * Returns the separator used between the test class name and the test method name in the
     * resource directory path. Defaults to {@code -} (hyphen). Use {@code /} (slash) to place
     * the class name and method name as separate path components.
     *
     * @return the separator string
     */
    String getSeparator();

    /**
     * Sets the separator used between the test class name and the test method name in the
     * resource directory path.
     *
     * @param separator the separator string (e.g. {@code -} or {@code /})
     */
    void setSeparator(String separator);

}
