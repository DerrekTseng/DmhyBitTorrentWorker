package net.dbtw.orm.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
public class DmhyItem {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Integer rowid;

	@Type(type = "text")
	String category;

	@Type(type = "text")
	String title;

	@Type(type = "text")
	String url;

	@Type(type = "text")
	String magnet;

	@Type(type = "text")
	String time;

	@Type(type = "text")
	String size;

	@Type(type = "text")
	String downloadState;

}
