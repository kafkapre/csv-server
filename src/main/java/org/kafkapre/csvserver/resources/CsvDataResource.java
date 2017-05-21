package org.kafkapre.csvserver.resources;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.kafkapre.csvserver.configuration.ServerConfiguration;
import org.kafkapre.csvserver.model.CsvDocument;
import org.kafkapre.csvserver.model.CsvDocumentFactory;
import org.kafkapre.csvserver.model.IdCollection;
import org.kafkapre.csvserver.model.CsvWrapper;
import org.kafkapre.csvserver.model.CsvWrapperFactory;
import org.kafkapre.csvserver.model.InfoMessage;
import org.kafkapre.csvserver.model.LineCollection;
import org.kafkapre.csvserver.persistence.api.ConnectionPersistenceException;
import org.kafkapre.csvserver.persistence.api.PersistenceClient;
import org.kafkapre.csvserver.persistence.api.PersistenceException;
import org.kafkapre.csvserver.persistence.api.StaleDataPersistenceException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_ACCEPTABLE;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.PRECONDITION_FAILED;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static org.kafkapre.csvserver.resources.ResourcesPaths.CSV_RESOURCE_PATH;

@Path(CSV_RESOURCE_PATH)
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CsvDataResource {

    private static final Logger logger = LogManager.getLogger(RootResource.class);

    private final ServerConfiguration configuration;
    private final PersistenceClient persistenceClient;

    public CsvDataResource(ServerConfiguration configuration, PersistenceClient persistenceClient) {
        this.configuration = configuration;
        this.persistenceClient = persistenceClient;
    }

    @GET
    public Response getAllIds() {
        logger.debug("GET: getAllIds endpoint requested");

        Response.Status status = null;
        Object response = null;
        try {
            Set<String> ids = persistenceClient.getAllIds();
            status = OK;
            response = new IdCollection(ids);
        } catch (PersistenceException e) {
            status = INTERNAL_SERVER_ERROR;
            response = new InfoMessage(e.getMessage());
        }
        return Response.status(status).entity(response).build();
    }

    @GET
    @Path("/{id}")
    public Response getCsvDocument(@PathParam("id") int id) {
        logger.debug("GET: getCsvDocument endpoint requested");

        Response.Status status = null;
        Object response = null;
        try {
            Optional<CsvDocument> csvDocument = persistenceClient.getCsvDocument(id);
            if (csvDocument.isPresent()) {
                status = OK;
                response = csvDocument.get();
            } else {
                status = NOT_FOUND;
                response = new InfoMessage(String.format("Data with id [%d] not found.", id));
            }
        } catch (PersistenceException e) {
            status = INTERNAL_SERVER_ERROR;
            response = new InfoMessage(e.getMessage());
        }

        return Response.status(status).entity(response).build();
    }

    @GET
    @Path("/{id}/lines")
    public Response getLines(@PathParam("id") int id,
                             @QueryParam("from") int from,
                             @QueryParam("to") int to
    ) {
        logger.debug("GET: getLines endpoint requested");

        Response.Status status = null;
        Object response = null;
        try {
            List<String> lines = persistenceClient.getLines(id, from, to);
            status = OK;
            response = new LineCollection(lines);
        } catch (PersistenceException e) {
            status = INTERNAL_SERVER_ERROR;
            response = new InfoMessage(e.getMessage());
        }
        return Response.status(status).entity(response).build();
    }

    @POST
    public Response postCsv(String csvData) {
        logger.debug("POST: postCsv endpoint requested");

        return store(null, csvData);
    }

    @PUT
    @Path("/{id}")
    public Response putCsv(@PathParam("id") int id, String csvData) {
        logger.debug("PUT: putCsv endpoint requested");

        if (id <= 0) {
            InfoMessage response = new InfoMessage(String.format("Id must be greater than 0. Your id is [%d]", id));
            return Response.status(PRECONDITION_FAILED).entity(response).build();
        }
        return store(id, csvData);
    }

    private Response store(Integer id, String csvData) {
        Response.Status status = null;
        Object response = null;

        Optional<CsvWrapper> csvWrapper = parseCsv(csvData, id);
        if (csvWrapper.isPresent()) {
            try {
                persistenceClient.storeCsvWrapper(csvWrapper.get());
                status = CREATED;
                response = csvWrapper.get().getCsvDocument();
            } catch (StaleDataPersistenceException e) {
                status = PRECONDITION_FAILED;
                response = new InfoMessage(e.getMessage());
            } catch (ConnectionPersistenceException e) {
                status = SERVICE_UNAVAILABLE;
                response = new InfoMessage(e.getMessage());
            } catch (PersistenceException e) {
                status = INTERNAL_SERVER_ERROR;
                response = new InfoMessage(e.getMessage());
            }
        } else {
            status = NOT_ACCEPTABLE;
            response = new InfoMessage("Cannot parse content.");
        }
        return Response.status(status).entity(response).build();
    }

    private Optional<CsvWrapper> parseCsv(String csvData, Integer id) {
        String[] linesArr = csvData.split("\n");

        if (linesArr.length > 0) {
            String[] headers = linesArr[0].split(configuration.getCsvSeparator());
            CsvDocument csvDocument = (id == null) ? CsvDocumentFactory.build(headers) : CsvDocumentFactory.build(id, headers);

            List<String> trimmedLines = new ArrayList<>(linesArr.length - 1);
            for (int i = 1; i < linesArr.length; i++) {
                String line = linesArr[i];
                if (!line.matches("\\w*")) {
                    trimmedLines.add(line);
                }
            }
            CsvWrapper csvWrapper = CsvWrapperFactory.build(csvDocument, trimmedLines);
            return Optional.of(csvWrapper);
        }
        return Optional.empty();
    }


}
