package net.dbtw.orm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.dbtw.orm.entity.DownloadSet;

@Repository
@Transactional
public interface DownloadSetRepo extends JpaRepository<DownloadSet, Integer> {

}