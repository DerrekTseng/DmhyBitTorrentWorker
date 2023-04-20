package net.dbtw.crawlers.dmhy;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DmhyWebsite {

	private String dmhyUrl = "https://share.dmhy.org";

	private String searchFormat = "/topics/list/page/";

	public String getPageContent(Integer pageNum) {

		Objects.requireNonNull(pageNum);

		String actualUrl = dmhyUrl + searchFormat + pageNum.toString();

		log.info("Fetch Dmhy Page: {}", actualUrl);

		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			HttpGet httpGet = new HttpGet(actualUrl);
			return httpclient.execute(httpGet, new BasicHttpClientResponseHandler());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public List<DmhyItemBean> parsePageContent(String content) {
		List<DmhyItemBean> dmhyItemBeans = new ArrayList<>();

		Document doc = Jsoup.parse(content);

		Element table = doc.getElementById("topic_list");

		Element tbody = table.getElementsByTag("tbody").get(0);

		SimpleDateFormat dmhy_sdf = new SimpleDateFormat("yyyy/MM/ddHH:mm");
		SimpleDateFormat bean_sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

		for (Element tr : tbody.getElementsByTag("tr")) {

			DmhyItemBean dmhyItemBean = new DmhyItemBean();

			Elements tds = tr.getElementsByTag("td");

			// get Date
			Element dateTd = tds.get(0);
			String dmhy_date = dateTd.getElementsByTag("span").get(0).html();
			dmhyItemBean.setTime(bean_sdf.format(parseDate(dmhy_sdf, dmhy_date)));

			// get Category
			Element categoryTd = tds.get(1);
			dmhyItemBean.setCategory(categoryTd.text());

			// get Title
			Element titleTd = tds.get(2);
			Element titleA = titleTd.getElementsByTag("a").stream().filter(e -> e.attr("target").equals("_blank")).findFirst().orElse(null);
			dmhyItemBean.setUrl(titleA.attr("href"));
			dmhyItemBean.setTitle(titleA.text());

			// get Magnet
			Element magnetTd = tds.get(3);
			String magnetHref = magnetTd.getElementsByTag("a").get(0).attr("href");
			magnetHref = urlDecode(magnetHref);
			magnetHref = magnetHref.replace(" ", "");
			magnetHref = magnetHref.replace("\"", "");
			dmhyItemBean.setMagnet(magnetHref);

			// get Size
			Element sizeTd = tds.get(4);
			dmhyItemBean.setSize(sizeTd.text());

			// add to List
			dmhyItemBeans.add(dmhyItemBean);

			log.info("Found DmhyItem: {}", dmhyItemBean);
		}

		return dmhyItemBeans;
	}

	private Date parseDate(SimpleDateFormat sdf, String str) {
		try {
			return sdf.parse(str);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String urlDecode(String url) {
		try {
			return URLDecoder.decode(url, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
