package store.model;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import store.model.entity.Mofongo;

@Stateless
public class MofongoRepository {

	private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@PersistenceContext
	private EntityManager entityManager;

	public List<Mofongo> getAllMofongos() {
		logger.log(Level.INFO, "Finding all mofongos.");

		return this.entityManager.createNamedQuery("findAllMofongos", Mofongo.class).getResultList();
	}

	public Mofongo persistMofongo(Mofongo mofongo) {
		logger.log(Level.INFO, "Persisting the new mofongo {0}.", mofongo);
		this.entityManager.persist(mofongo);
		return mofongo;
	}

	public void removeMofongoById(Long mofongoId) {
		logger.log(Level.INFO, "Removing a mofongo {0}.", mofongoId);
		Mofongo mofongo = entityManager.find(Mofongo.class, mofongoId);
		this.entityManager.remove(mofongo);
	}

	public Mofongo findMofongoById(Long mofongoId) {
		logger.log(Level.INFO, "Finding the mofongo with id {0}.", mofongoId);
		return this.entityManager.find(Mofongo.class, mofongoId);
	}
}
