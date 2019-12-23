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

import store.model.StoreRepository;
import store.model.entity.Taco;

@Path("tacos")
public class StoreResource {

	private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@Inject
	private StoreRepository storeRepository;

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Taco> getAllTacos() {
		return this.storeRepository.getAllTacos();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response createTaco(Taco taco) {
		try {
			taco = this.storeRepository.persistTaco(taco);
			return Response.created(URI.create("/" + taco.getId())).build();
		} catch (PersistenceException e) {
			logger.log(Level.SEVERE, "Error creating taco {0}: {1}.", new Object[] { taco, e });
			throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Taco getTacoById(@PathParam("id") Long tacoId) {
		return this.storeRepository.findTacoById(tacoId);
	}

	@DELETE
	@Path("{id}")
	public void deleteTaco(@PathParam("id") Long tacoId) {
		try {
			this.storeRepository.removeTacoById(tacoId);
		} catch (IllegalArgumentException ex) {
			logger.log(Level.SEVERE, "Error calling deleteTaco() for tacoId {0}: {1}.",
					new Object[] { tacoId, ex });
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}
}