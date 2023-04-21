package net.dbtw.dto;

import lombok.Data;
import net.dbtw.orm.entity.DownloadState.State;

@Data
public class DownloadingBean {

	private String name;

	private String percent;
	
	private State state;
}
