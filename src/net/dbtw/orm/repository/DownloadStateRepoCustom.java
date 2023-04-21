package net.dbtw.orm.repository;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.dbtw.orm.entity.DownloadState;
import net.dbtw.orm.entity.DownloadState.State;

@Repository
@Transactional
public class DownloadStateRepoCustom extends CustomRepositoryBase {

	@Autowired
	DownloadStateRepo downloadStateRepo;

	public List<DownloadState> findByStateIn(State... states) {
		String hql = "from DownloadState where state in (:states)";
		return entityManager.createQuery(hql, DownloadState.class) //
				.setParameter("states", Arrays.asList(states)) //
				.getResultList();
	}

	@Transactional
	public void resetStateIn(State... states) {
		String hql = "from DownloadState where state in (:states)";
		entityManager.createQuery(hql, DownloadState.class) //
				.setParameter("states", Arrays.asList(states)) //
				.getResultList().forEach(entity -> {
					entity.setState(State.Init);
					downloadStateRepo.save(entity);
				});
	}

}
