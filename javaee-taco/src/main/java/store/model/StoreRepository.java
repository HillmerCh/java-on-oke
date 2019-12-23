package store.model;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import store.model.entity.Taco;

@Stateless
public class StoreRepository {

	private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@PersistenceContext
	private EntityManager entityManager;

	public List<Taco> getAllTacos() {
		logger.log(Level.INFO, "Finding all tacoes.");

		return this.entityManager.createNamedQuery("findAllTacos", Taco.class).getResultList();
	}

	public Taco persistTaco(Taco taco) {
		logger.log(Level.INFO, "Persisting the new taco {0}.", taco);
		this.entityManager.persist(taco);
		return taco;
	}

	public void removeTacoById(Long tacoId) {
		logger.log(Level.INFO, "Removing a taco {0}.", tacoId);
		Taco taco = entityManager.find(Taco.class, tacoId);
		this.entityManager.remove(taco);
	}

	public Taco findTacoById(Long tacoId) {
		logger.log(Level.INFO, "Finding the taco with id {0}.", tacoId);
		return this.entityManager.find(Taco.class, tacoId);
	}
}
