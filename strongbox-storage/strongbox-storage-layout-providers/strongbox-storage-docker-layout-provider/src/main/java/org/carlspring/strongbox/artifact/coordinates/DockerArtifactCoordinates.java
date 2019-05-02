package org.carlspring.strongbox.artifact.coordinates;

import org.carlspring.strongbox.artifact.coordinates.versioning.SemanticVersion;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author carlspring
 */
@Entity
@SuppressWarnings("serial")
@XmlRootElement(name = "DockerArtifactCoordinates")
@XmlAccessorType(XmlAccessType.NONE)
@ArtifactCoordinatesLayout(name = DockerArtifactCoordinates.LAYOUT_NAME, alias = DockerArtifactCoordinates.LAYOUT_ALIAS)
public class DockerArtifactCoordinates
    extends AbstractArtifactCoordinates<DockerArtifactCoordinates, String>
{

    public static final String LAYOUT_NAME = "Docker";
    
    public static final String LAYOUT_ALIAS = "Docker";

    public static final String REPOSITORY = "repository";

    public static final String TAG = "tag";

    //
    // TODO: We will have to think about something like this:
    //
    // public static final String LAYERS = "layers";


    public DockerArtifactCoordinates(String repository,
                                     String reference)
    {
        // if any of the required arguments are empty, throw an error
        if (StringUtils.isBlank(repository))
        {
            throw new IllegalArgumentException("The repository field is mandatory.");
        }

        if (StringUtils.isBlank(reference))
        {
            throw new IllegalArgumentException("The reference field is mandatory.");
        }

        setId(repository);
        setVersion(reference);
        
        // TODO:
        // setLayers(layers);
    }

    public static DockerArtifactCoordinates parse(String path)
    {
        // TODO:
        return null;
    }

    @Override
    public String getId()
    {
        return getCoordinate(REPOSITORY);
    }

    @Override
    public void setId(String id)
    {
        setCoordinate(REPOSITORY, id);
    }

    @Override
    public String getVersion()
    {
        return getCoordinate(TAG);
    }

    @Override
    public void setVersion(String version)
    {
        setCoordinate(TAG, version);
    }

    //
    // TODO: We will have to think about something like this:
    //
    // @ArtifactLayoutCoordinate
    // public List<String> getLayers()
    // {
    //     return (List<String>) getCoordinate(LAYERS);
    // }
    //
    // public void setLayers(List<String> layers)
    // {
    //     setCoordinate(LAYERS, layers);
    // }

    /**
     * @return Returns the reconstructed path from the stored coordinate values
     */
    @Override
    public String toPath()
    {
        // TODO:
        return null;
    }

    /**
     * @return Returns the native version of the package
     */
    @Override
    public String getNativeVersion()
    {
        return getVersion();
    }

    /**
     * @return Returns a map data structure of the coordinates without the TAG coordinate
     */
    @Override
    public Map<String, String> dropVersion()
    {
        Map<String, String> result = getCoordinates();
        result.remove(TAG);

        return result;
    }

}
