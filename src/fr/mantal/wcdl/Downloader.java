package fr.mantal.wcdl;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Downloader
{

	private String imageSelector;
	private String nextLinkSelector;

	private String baseUrl;
	private String startPath;
	//Must end with path separator
	private String localPath;

	public Downloader(String imageSelector, String nextLinkSelector, String baseUrl, String startPath, String localPath)
	{

		this.imageSelector = imageSelector;
		this.nextLinkSelector = nextLinkSelector;
		this.baseUrl = baseUrl;
		this.startPath = startPath;
		this.localPath = localPath;
	}

	public void download()
	{
		URL url = null;

		try
		{
			url = new URL(baseUrl + startPath);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			return;
		}

		Document document = null;

		int i = 1;//TODO
		do
		{
			String file = localPath + i++ + ".gif";

			try
			{
				document = Jsoup.connect(url.toString()).get();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (fileExist(file))
				continue;

			copyImageToFile(document, new File(file));

		} while((url = getNextUrl(document)) != null);
	}

	private boolean copyImageToFile(Document document, File file)
	{
		String url = document.select(imageSelector).attr("src");

		url = fixUrl(url);
		try
		{
			FileUtils.copyURLToFile(new URL(url), file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private URL getNextUrl(Document document)
	{
		String url = document.select(nextLinkSelector).attr("href");

		if (url.isEmpty())
			return null;

		url = fixUrl(url);
		try
		{
			return new URL(url);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private String fixUrl(String url)
	{
		if (!url.startsWith("http"))
			url = baseUrl + url;
		return url;
	}

	private static boolean fileExist(String file)
	{
		File res = new File(file);
		return res.isFile() && res.exists();
	}
}