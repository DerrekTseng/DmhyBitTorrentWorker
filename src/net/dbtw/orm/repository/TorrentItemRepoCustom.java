package net.dbtw.orm.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.dbtw.orm.entity.TorrentItem;

@Repository
@Transactional
public class TorrentItemRepoCustom extends CustomRepositoryBase {

	@Transactional
	public List<TorrentItem> searchLike(String category, String prefix, String suffix) {

		String hql = "from TorrentItem where category=:category and name like :search";

		List<TorrentItem> result = entityManager.createQuery(hql, TorrentItem.class) //
				.setParameter("category", category.trim()) //
				.setParameter("search", "%" + prefix.trim() + "%" + suffix.trim() + "%") //
				.getResultList();

		return result;
	}

}
