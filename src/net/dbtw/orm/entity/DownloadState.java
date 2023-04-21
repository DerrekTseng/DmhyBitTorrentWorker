package net.dbtw.orm.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
public class DownloadState {

	public static enum State {
		Init, Waiting, Downloading, Finish, TorrentItemNotFound, Error;
	}

	@Id
	String torrentId;

	@Type(type = "text")
	String downloadingFolder;

	@Type(type = "text")
	String completeFolder;

	String percentage;

	State state;

}
