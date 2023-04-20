package net.dbtw.orm.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.dbtw.orm.entity.DmhyItem;

@Repository
@Transactional
public class DmhyItemRepoCustom extends CustomRepositoryBase {

	@Transactional
	public boolean urlExists(String url) {

		String hql = "from DmhyItem where url=:url";

		List<DmhyItem> list = entityManager.createQuery(hql, DmhyItem.class) //
				.setParameter("url", url) //
				.getResultList();

		return list.size() > 0;
	}

	@Transactional
	public List<DmhyItem> searchCategoryTitleLike(String category, String prefix, String suffix) {

		String hql = "from DmhyItem where category=:category and title like :search order by time desc";

		List<DmhyItem> result = entityManager.createQuery(hql, DmhyItem.class) //
				.setParameter("category", category) //
				.setParameter("search", "%" + prefix.trim() + "%" + suffix.trim() + "%") //
				.getResultList();

		return result;
	}

}
