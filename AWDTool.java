package net.nwie.awdtool;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.filefilter.WildcardFileFilter;

public class AWDTool {

	static String sourcePath = "F:\\AWD\\docvault\\recovery";
	static String destinationPath = "F:\\AWD\\docvault\\recovery";
	static String searchFileLocation = "F:\\AWD\\server\\success";

	public static void main(String[] args) {
		File sourceDir = new File(sourcePath);
		/*
		 * File[] nameFilterfiles = sourceDir.listFiles(nameFilter);
		 * moveFile(nameFilterfiles); File[] sizeFilterfiles =
		 * sourceDir.listFiles(sizeFilter); moveFile(sizeFilterfiles);
		 */
		File[] searchFilterfiles = sourceDir.listFiles(searchFilter);
		searchFile(searchFilterfiles);
	}

	private static void searchFile(File[] files) {

		File destDir = new File(destinationPath);
		if (files.length == 0) {
			System.out.println("There is no such files");
		} else {
			for (File aFile : files) {
				try {
					if (!destDir.exists()) {
						destDir.mkdir();
					} else {
						if (aFile.getName().contains("_fn-awd-")) {
							File searchDir = new File(searchFileLocation);
							Path path = Paths.get(aFile.getName());
							Path fileName = path.getFileName();
							String str = fileName.toString();
							String fileStr = str.substring(0, str.lastIndexOf('.'));
							System.out.println("Out " + fileStr);
							FileFilter wildCardFilter = new WildcardFileFilter(fileStr + "*");
							File[] searchFilterfiles = searchDir.listFiles(wildCardFilter);

							try (BufferedReader br = new BufferedReader(new FileReader(aFile))) {
								// PrintWriter pw = new PrintWriter(new FileWriter(aFile));

								String line = br.readLine();
								// line.replaceAll("\\s+", "");
								while (line != null) {
									// System.out.println(line);
									// System.out.println(line.length());
									// System.out.println(numberOfWords(line));
									String tokens[] = line.split("\\s+");
									for (String guidString : tokens) {
										if (guidString.length() == 39) {
											System.out.println(guidString);
											for (File xmlFile : searchFilterfiles) {
												readCDATAFromXMLUsingStax(xmlFile, guidString);
												/*
												 * if (!line.trim().equals(line)) {
												 * 
												 * pw.println(line); pw.flush(); }
												 */
											}
										}
									}
									line = br.readLine();
								}
								br.close();

							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (Exception exp) {
					System.out.println(exp);
				}
			}
		}
	}

	public static void readCDATAFromXMLUsingStax(File xmlFile, String brLine) {
		XMLStreamReader r = null;
		try (InputStream in = new BufferedInputStream(new FileInputStream(xmlFile));) {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			r = factory.createXMLStreamReader(in);
			while (r.hasNext()) {
				switch (r.getEventType()) {
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.CDATA:
					r.getText().contains(brLine);
					System.out.println(r.getText().contains(brLine));
					break;
				default:
					break;
				}
				r.next();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (r != null) {
				try {
					r.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private static void moveFile(File[] files) {

		File destDir = new File(destinationPath);
		if (files.length == 0) {
			System.out.println("There is no such files");
		} else {
			for (File aFile : files) {
				try {
					if (!destDir.exists()) {
						destDir.mkdir();
					} else {
						aFile.renameTo(new File(destDir + "\\" + aFile.getName()));
						System.out.println(aFile.getName() + " - " + aFile.length());
					}
				} catch (Exception exp) {
					System.out.println(exp);
				}

			}
		}
	}

	static FilenameFilter nameFilter = new FilenameFilter() {
		public boolean accept(File file, String name) {
			if (name.contains("PaymentSummary")) {
				// filters files whose extension is PaymentSummary
				return true;
			} else {
				return false;
			}
		}
	};

	static FileFilter sizeFilter = new FileFilter() {
		public boolean accept(File file) {
			if (file.isFile() && file.length() <= 1 * 1024 * 1024) {
				// filters files whose size less than or equal to 1MB
				return true;
			} else {
				return false;
			}
		}
	};

	static FileFilter searchFilter = new FileFilter() {
		public boolean accept(File file) {
			if (file.isFile() && file.length() <= 1 * 1024) {
				// filters files whose size less than or equal to 1MB
				return true;
			} else {
				return false;
			}
		}
	};

	public static String removeExtension(String s) {

		String separator = System.getProperty("file.separator");
		String filename;

		// Remove the path upto the filename.
		int lastSeparatorIndex = s.lastIndexOf(separator);
		if (lastSeparatorIndex == -1) {
			filename = s;
		} else {
			filename = s.substring(lastSeparatorIndex + 1);
		}

		// Remove the extension.
		int extensionIndex = filename.lastIndexOf(".");
		if (extensionIndex == -1)
			return filename;

		return filename.substring(0, extensionIndex);
	}

}
