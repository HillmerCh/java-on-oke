package store.web.rest;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import store.model.MofongoRepository;
import store.model.entity.Mofongo;

@Path("mofongos")
public class MofongoResource {

	private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@Inject
	private MofongoRepository mofongoRepository;

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Mofongo> getAllMofongos() {
		return this.mofongoRepository.getAllMofongos();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response createMofongo(Mofongo mofongo) {
		try {
			mofongo = this.mofongoRepository.persistMofongo(mofongo);
			return Response.created(URI.create("/" + mofongo.getId())).build();
		} catch (PersistenceException e) {
			logger.log(Level.SEVERE, "Error creating mofongo {0}: {1}.", new Object[] { mofongo, e });
			throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Mofongo getMofongoById(@PathParam("id") Long mofongoId) {
		return this.mofongoRepository.findMofongoById(mofongoId);
	}

	@DELETE
	@Path("{id}")
	public void deleteMofongo(@PathParam("id") Long mofongoId) {
		try {
			this.mofongoRepository.removeMofongoById(mofongoId);
		} catch (IllegalArgumentException ex) {
			logger.log(Level.SEVERE, "Error calling deleteMofongo() for mofongoId {0}: {1}.",
					new Object[] { mofongoId, ex });
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}
}