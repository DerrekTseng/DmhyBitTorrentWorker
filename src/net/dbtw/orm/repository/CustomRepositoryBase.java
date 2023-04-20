package net.dbtw.orm.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.transaction.annotation.Transactional;

public class CustomRepositoryBase {
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Transactional
	public <T> T getSingleResult(TypedQuery<T> qyery, T defaultVal) {
		if (qyery.getResultList() == null || qyery.getResultList().size() == 0 || qyery.getSingleResult() == null) {
			return defaultVal;
		} else {
			return qyery.getSingleResult();
		}
	}

}	