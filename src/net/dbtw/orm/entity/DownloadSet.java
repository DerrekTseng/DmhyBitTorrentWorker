package net.dbtw.orm.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
public class DownloadSet {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	int rowid;

	@Type(type = "text")
	String name;
	
	@Type(type = "text")
	String category;

	@Type(type = "text")
	String prefix;

	@Type(type = "text")
	String suffix;

	@Type(type = "text")
	String downloadingFolder;

	@Type(type = "text")
	String completedFolder;

}
