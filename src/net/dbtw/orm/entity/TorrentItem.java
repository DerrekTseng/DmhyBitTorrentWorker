package net.dbtw.orm.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
public class TorrentItem {

	@Id
	String torrentId;

	@Type(type = "text")
	String category;

	@Type(type = "text")
	String name;

}
