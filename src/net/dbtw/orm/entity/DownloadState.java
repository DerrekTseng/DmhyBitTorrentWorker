package net.dbtw.orm.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class DownloadState {

	public static enum State {
		Wait, Downloading, Finish, DmhyItemNotFound, Error;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Integer rowid;

	Integer refDmhyItem;

	Integer refDownloadSet;

	String downloadingFolder;

	String completeFolder;

	String percentage;

	State state;

}
