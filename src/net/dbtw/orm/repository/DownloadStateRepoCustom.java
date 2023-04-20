package net.dbtw.orm.repository;

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

	@Transactional
	public List<DownloadState> findByState(State state) {
		String hql = "from DownloadState where state = :state";
		return entityManager.createQuery(hql, DownloadState.class).setParameter("state", state).getResultList();
	}

	@Transactional
	public void resetState() {
		String hql = "from DownloadState where state = :state";
		entityManager.createQuery(hql, DownloadState.class).setParameter("state", State.Downloading).getResultList().forEach(entity -> {
			entity.setState(State.Wait);
			downloadStateRepo.save(entity);
		});

	}

}
