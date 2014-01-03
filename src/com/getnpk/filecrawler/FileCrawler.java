package com.getnpk.filecrawler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * @author Nitin <getnpk at gmail dot com>
 */

class FileCrawler extends JFrame implements Runnable{

	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane;
	private JPanel crawlPanel;
	private JPanel settingsPanel;
	private JPanel aboutPanel;
	
	public JTextArea output;
	private JTextField searchURL;
	private static String crawlDomains;
	
	private static CrawlController controller;
	
	public FileCrawler(String url) {

		setTitle("Simple File Crawler");
		setSize(600, 400);
		setBackground(Color.gray);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		getContentPane().add(topPanel);

		
		// Create the tab pages
		createCrawlPanel(url);
		createAboutPanel();

		// Create a tabbed pane
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Crawl", crawlPanel);
		tabbedPane.addTab("About", aboutPanel);
		topPanel.add(tabbedPane, BorderLayout.CENTER);
		
		setVisible(true);
		
	}

	public void createCrawlPanel(String url) {
		crawlPanel = new JPanel();
		crawlPanel.setLayout(new FlowLayout());

		JLabel searchLabel = new JLabel("Web URL:");
		crawlPanel.add(searchLabel);

		searchURL = new JTextField(40);
		searchURL.setText(url);
		
		crawlPanel.add(searchURL);

		JButton searchButton = new JButton("Crawl");
		crawlPanel.add(searchButton);
		
		output = new JTextArea(40,50);
		output.setBackground(Color.WHITE);
		crawlPanel.add(output);
		
		searchButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				System.out.println(searchURL.getText());
				
				crawlDomains = searchURL.getText();
				
				setVisible(false);
				
				new Thread(new FileCrawler(crawlDomains)).start();
							
			}
			
		});
	       
	}



	public void createAboutPanel() {
		aboutPanel = new JPanel();
		aboutPanel.setLayout(new GridLayout(3, 2)); //rows and cols

		aboutPanel.add(new JLabel("What is this for?"));
		aboutPanel.add(new TextArea("Crawling images app,  just for fun!"));
		aboutPanel.add(new JLabel("Whom to contact?"));
		aboutPanel.add(new TextArea("Contact me! getnpk [at] gmail [dot] com"));

	}


	@Override
	public void run() {
		
		
        String rootFolder = "C:\\Users\\nitinkp\\Desktop\\dump";
        String storageFolder = "C:\\Users\\nitinkp\\Desktop\\dump\\pics";

        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(rootFolder);

        /*
         * Since images are binary content, we need to set this parameter to
         * true to make sure they are included in the crawl.
         */
        config.setIncludeBinaryContentInCrawling(true);

        

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        
		try {
			controller = new CrawlController(config, pageFetcher, robotstxtServer);
		} catch (Exception e) {
			e.printStackTrace();
		}

        controller.addSeed(crawlDomains);
                
		ImageCrawler.configure(crawlDomains, storageFolder, this);
        controller.start(ImageCrawler.class, 2);
		
	}
}