package org.carlspring.strongbox.providers.repository;

import org.carlspring.strongbox.artifact.MavenArtifactUtils;
import org.carlspring.strongbox.providers.io.RepositoryPath;
import org.carlspring.strongbox.storage.repository.Repository;
import org.carlspring.strongbox.testing.artifact.MavenArtifactTestUtils;
import org.carlspring.strongbox.testing.repository.MavenRepository;
import org.carlspring.strongbox.testing.storage.repository.RepositoryManagementTestExecutionListener;
import org.carlspring.strongbox.testing.storage.repository.TestRepository.Remote;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.maven.artifact.Artifact;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.Resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Przemyslaw Fusik
 */
public class RetryDownloadArtifactWithSingleFailureAtSomePointTest
        extends MockedRestArtifactResolverTestBase implements ArtifactResolverContext
{

    private static final String REPOSITORY = "rdawsfaspt-repository";

    private static final String PROXY_REPOSITORY_URL = "https://repo.maven.apache.org/maven2/";
    
    private OneTimeBrokenArtifactInputStream brokenArtifactInputStream;

    private boolean exceptionAlreadyThrown;

    public RetryDownloadArtifactWithSingleFailureAtSomePointTest()
    {
        brokenArtifactInputStream = new OneTimeBrokenArtifactInputStream(jarArtifact);
    }

    @Override
    public InputStream getInputStream()
    {
        return brokenArtifactInputStream;
    }

    @Override
    protected ArtifactResolverContext lookupArtifactResolverContext()
    {
        return this;
    }

    @Test
    @ExtendWith(RepositoryManagementTestExecutionListener.class)
    public void resurrectedInputStreamShouldBeSuccessfullyHandledByRetryFeature(@MavenRepository(repositoryId = REPOSITORY) @Remote(url = PROXY_REPOSITORY_URL) Repository proxyRepository)
            throws Exception
    {
        Artifact artifact = MavenArtifactTestUtils.getArtifactFromGAVTC("org.apache.commons:commons-lang3:3.2");
        String path = MavenArtifactUtils.convertArtifactToPath(artifact);
        RepositoryPath artifactPath = repositoryPathResolver.resolve(proxyRepository)
                                                            .resolve(path);
        
        // given
        assertFalse(Files.exists(artifactPath));
        assertFalse(exceptionAlreadyThrown);

        // when
        assertStreamNotNull(STORAGE0, REPOSITORY, path);

        // then
        assertTrue(Files.exists(artifactPath));
        assertThat(Files.size(artifactPath), CoreMatchers.equalTo(Files.size(jarArtifact.getFile().toPath())));
        assertTrue(exceptionAlreadyThrown);
    }

    private class OneTimeBrokenArtifactInputStream
            extends MockedRestArtifactResolverTestBase.BrokenArtifactInputStream
    {

        private int currentReadSize;

        public OneTimeBrokenArtifactInputStream(final Resource jarArtifact)
        {
            super(jarArtifact);
        }

        @Override
        public int read()
                throws IOException
        {
            if (currentReadSize == BUF_SIZE && !exceptionAlreadyThrown)
            {
                exceptionAlreadyThrown = true;
                throw new IOException("Connection lost.");
            }

            currentReadSize++;

            return artifactInputStream.read();
        }
    }

}
