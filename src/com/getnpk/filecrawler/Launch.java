package com.getnpk.filecrawler;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nitin <getnpk at gmail dot com>
 */

public class Launch {

	public static void main(String args[]) {
	
		Pattern imgPatterns = Pattern.compile(".*(\\.(bmp|gif|jpe?g|png|tiff?))$");
		
		
		new FileCrawler("http://www.google.com/", 
				System.getProperty("user.dir") + File.separator + "Downloads", 
				imgPatterns );
	
		//System.out.println(imgPatterns.matcher(".tiff").matches());
		
	}
}
