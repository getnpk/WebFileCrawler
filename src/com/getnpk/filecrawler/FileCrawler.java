package com.getnpk.filecrawler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.*;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * @author Nitin <getnpk at gmail dot com>
 */

class FileCrawler extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;

	public Pattern imgPatterns;

	private JTabbedPane tabbedPane;
	private JPanel crawlPanel;
	private JPanel settingsPanel;
	private JPanel aboutPanel;

	public JTextArea output;

	private JTextField folderURL;
	private JTextField searchURL;
	private static String crawlDomain;
	private static String targetFolder;

	private static CrawlController controller;

	private ArrayList<JCheckBox> boxes;

	public FileCrawler(String url, String target, Pattern imgPatterns) {

		this.imgPatterns = imgPatterns;

		setTitle("Web File Crawler");
		setSize(610, 400);
		setBackground(Color.gray);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		getContentPane().add(topPanel);

		// Create the tab pages
		createCrawlPanel(url, target);
		createSettingsPanel(imgPatterns);
		createAboutPanel();

		// Create a tabbed pane
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Crawl", crawlPanel);
		tabbedPane.addTab("Settings", settingsPanel);
		tabbedPane.addTab("About", aboutPanel);


		topPanel.add(tabbedPane, BorderLayout.CENTER);

		setVisible(true);

	}

	public void createCrawlPanel(String url, String target) {
		crawlPanel = new JPanel();
		crawlPanel.setLayout(new FlowLayout());

		JLabel folderLabel = new JLabel("Target DIR:");
		crawlPanel.add(folderLabel);

		folderURL = new JTextField(40);
		folderURL.setText(target);
		crawlPanel.add(folderURL);

		JButton folderButton = new JButton("Select");
		crawlPanel.add(folderButton);

		JLabel searchLabel = new JLabel("Web URL:");
		crawlPanel.add(searchLabel);

		searchURL = new JTextField(40);
		searchURL.setText(url);
		crawlPanel.add(searchURL);

		JButton searchButton = new JButton("Crawl");
		crawlPanel.add(searchButton);

		output = new JTextArea(40, 50);
		output.setBackground(Color.WHITE);
		crawlPanel.add(output);

		folderButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {

				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(false);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int option = chooser.showSaveDialog(FileCrawler.this);
				if (option == JFileChooser.APPROVE_OPTION) {
					folderURL.setText((chooser.getSelectedFile() != null) ? chooser
							.getSelectedFile().getAbsolutePath() : System
							.getProperty("user.dir") + File.separator + "dump");
				} else {
					folderURL.setText(System.getProperty("user.dir")
							+ File.separator + "dump");
				}

			}

		});

		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {

				System.out.println("Target Folder: " + folderURL.getText());
				System.out.println("Crawl URL: " + searchURL.getText());

				crawlDomain = searchURL.getText();
				targetFolder = folderURL.getText();

				setVisible(false);

				System.out.println(imgPatterns);
				new Thread(new FileCrawler(crawlDomain, targetFolder,
						imgPatterns), "main gui").start();

			}

		});

	}

	public void createSettingsPanel(Pattern imgPatterns) {
		settingsPanel = new JPanel();
		settingsPanel.setLayout(new FlowLayout());

		HandlerClass handler = new HandlerClass();

		String[] types = { "bmp", "gif", "jpeg", "png", "tiff" };
		boxes = new ArrayList<JCheckBox>();

		for (String type : types) {
			JCheckBox box = new JCheckBox(type);

			if (imgPatterns.matcher("." + box.getText()).matches())
				box.setSelected(true);
			else
				box.setSelected(false);

			box.addItemListener(handler);
			settingsPanel.add(box);

			boxes.add(box);
		}
	}

	private class HandlerClass implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent event) {
			StringBuilder builder = new StringBuilder();
			builder.append(".*(\\\\.(");

			for (JCheckBox box : boxes) {
				if (box.isSelected())
					builder.append(box.getText() + "|");
			}
			builder.append("abc?))$");

			imgPatterns = Pattern.compile(builder.toString());
			System.out.println(imgPatterns);
		}
	}

	public void createAboutPanel() {
		aboutPanel = new JPanel();
		aboutPanel.setLayout(new GridLayout(3, 2)); // rows and cols

		aboutPanel.add(new JLabel("What is this for?"));
		aboutPanel.add(new TextArea("Crawling images app,  just for fun!"));
		aboutPanel.add(new JLabel("Whom to contact?"));
		aboutPanel.add(new TextArea("getnpk [at] gmail [dot] com"));

	}

	@Override
	public void run() {

		String rootFolder = folderURL.getText();
		String storageFolder = folderURL.getText() + File.separator
				+ "Downloads";

		CrawlConfig config = new CrawlConfig();

		config.setCrawlStorageFolder(rootFolder);

		/*
		 * Since images are binary content, we need to set this parameter to
		 * true to make sure they are included in the crawl.
		 */
		config.setIncludeBinaryContentInCrawling(true);

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig,
				pageFetcher);

		try {
			controller = new CrawlController(config, pageFetcher,
					robotstxtServer);
		} catch (Exception e) {
			e.printStackTrace();
		}

		controller.addSeed(crawlDomain);

		ImageCrawler.configure(crawlDomain, storageFolder, this);
		controller.start(ImageCrawler.class, 2);

	}
}