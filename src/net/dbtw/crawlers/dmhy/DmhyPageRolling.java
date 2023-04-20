package net.dbtw.crawlers.dmhy;

import java.util.List;

@FunctionalInterface
public interface DmhyPageRolling {
	boolean apply(int pageNum, List<DmhyItemBean> itemBean);
}
