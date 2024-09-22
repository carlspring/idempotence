package org.carlspring.testing.idempotence.config;

/**
 * @author carlspring
 */
public interface IdempotenceProperties
{

    String getBasedir();

    void setBasedir(String basedir);

    boolean useFullyQualifiedNamePrefixes();

    void setUseFullyQualifiedClassNamePrefixes(boolean useFullyQualifiedClassNamePrefixes);

}
