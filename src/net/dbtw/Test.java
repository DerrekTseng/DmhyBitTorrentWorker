package net.dbtw;

import java.io.File;

import net.dbtw.bittorrent.download.MagnetDownloadClient;

public class Test {

	public static void main(String[] args) throws Exception {
		
	}

	static void test() {
		String url = "magnet:?xt=urn:btih:LAPUNQ566EOZKKSBINQK2WMDYLNKNGFG";

		File dir = new File("C:\\Users\\a0926\\Desktop\\AA");

		MagnetDownloadClient magnetDownloadClient = new MagnetDownloadClient(url, dir);

		magnetDownloadClient.startAsync(state -> {
			System.out.println(String.format("%s/%s", state.getCompletePercents(), state.getRequiredPercents()));
		}, ((magnetDownloadState, throwable) -> {
			System.out.println(magnetDownloadState.getName() + " download success");
		}));
	}

}
