package com.getnpk.filecrawler;

import java.io.File;
import java.util.regex.Pattern;


import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.crawler4j.util.IO;

/**
 * @author Nitin <getnpk at gmail dot com>
 */


public class ImageCrawler extends WebCrawler {

	private static final Pattern filters = Pattern
			.compile(".*(\\.(css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf"
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	private static final Pattern imgPatterns = Pattern
			.compile(".*(\\.(bmp|gif|jpe?g|png|tiff?))$");

	private static File storageFolder;
	private static String crawlDomain;

	private static StringBuilder builder;
	private static FileCrawler crawler;

	public static void configure(String domain, String storageFolderName,
			FileCrawler crawler) {
		ImageCrawler.crawlDomain = domain;

		
		storageFolder = new File(storageFolderName);
		if (!storageFolder.exists()) {
			
			storageFolder.mkdirs();
		}

		builder = new StringBuilder();
		ImageCrawler.crawler = crawler;
	}

	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		if (filters.matcher(href).matches()) {
			return false;
		}

		if (imgPatterns.matcher(href).matches()) {
			return true;
		}

		if (href.startsWith(crawlDomain)) {
			return true;
		}

		return false;
	}

	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();

		// We are only interested in processing images
		if (!(page.getParseData() instanceof BinaryParseData)) {
			return;
		}

		if (!imgPatterns.matcher(url).matches()) {
			return;
		}

		// Not interested in very small images
		if (page.getContentData().length < 10 * 1024) {
			return;
		}

		// get a file name for storing this image
		String extension = url.substring(url.lastIndexOf("."));
		String filename = url.substring(url.lastIndexOf("/"),
				url.lastIndexOf("."))
				+ extension;

		// store image
		IO.writeBytesToFile(page.getContentData(),
				storageFolder.getAbsolutePath() + "/" + filename);

		System.out.println("Stored: " + url);
		builder.append("Stored: " + url + "\n");
		crawler.output.setText(builder.toString());
	}

}
