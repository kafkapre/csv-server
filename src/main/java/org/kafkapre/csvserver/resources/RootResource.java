package org.kafkapre.csvserver.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.kafkapre.csvserver.AppServer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

import static javax.ws.rs.core.Response.Status.OK;
import static org.kafkapre.csvserver.resources.ResourcesPaths.CSV_RESOURCE_PATH;
import static org.kafkapre.csvserver.resources.ResourcesPaths.HEALTHCHECK_RESOURCE_PATH;

@Path("/")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class RootResource {

    private static final Logger logger = LogManager.getLogger(RootResource.class);

    @XmlRootElement
    private static class RootApi{

        @JsonProperty
        @XmlElement
        private final String healtcheckPath = HEALTHCHECK_RESOURCE_PATH;

        @JsonProperty
        @XmlElement
        private final String csvPath = CSV_RESOURCE_PATH;
    }

    private static final RootApi rootApi = new RootApi();

    @GET
    public Response getRootApi() {
        logger.debug("GET: root api endpoint requested");
        return Response.status(OK).entity(rootApi).build();
    }

}
