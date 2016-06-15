package fr.mantal.wcdl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Main {
	private static boolean fileExist(String file) {
		File f = new File(file);
		return f.isFile(); 
	}
	
	public static void main(String[] args) {
		
		String url = "http://www.phdcomics.com/comics/archive.php?comicid=";
		String baseFile = "D:/Pictures/Comics/phd comics/";

		for (int i = 0; i <= 1885; i++) {
			String file = baseFile + i + ".gif";
			
			if (fileExist(file))
				continue;
			
			Document doc = null;
			try {
				doc = Jsoup.connect(url + i).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				FileUtils.copyURLToFile(new URL(doc.select("#comic").attr("src")), new File(file));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
