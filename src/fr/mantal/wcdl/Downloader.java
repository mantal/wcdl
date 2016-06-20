package fr.mantal.wcdl;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Downloader
{
	private String imageSelector;
	private String nextLinkSelector;

	private String baseUrl;
	private String startPath;
	//Must end with path separator
	private String localPath;

	private String titleSelector = null;
	private String titleRegex = null;

	private String fileFormat = null;

	Downloader(String imageSelector, String nextLinkSelector, String baseUrl, String startPath, String localPath)
	{
		this.imageSelector = imageSelector;
		this.nextLinkSelector = nextLinkSelector;
		this.baseUrl = baseUrl;
		this.localPath = localPath + File.separator;
		this.startPath = startPath;
	}

	Downloader Title(String titleSelector)
	{
		return Title(titleSelector, null);
	}

	Downloader Title(String titleSelector, String titleRegex)
	{
		this.titleSelector = titleSelector;
		this.titleRegex = titleRegex;

		return this;
	}

	void download()
	{
		URL url;

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
		int i = 1;

		do
		{
			try
			{
				document = Jsoup.connect(url.toString()).get();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (fileFormat == null)
				fileFormat = getFileFormat(document);

			String file = localPath + i + " - " + getTitleName(document) + fileFormat;

			if (fileExist(file))
				continue;

			copyImageToFile(document, new File(file));

			i++;
		} while((url = getNextUrl(document)) != null);
	}

	private String getFileFormat(Document document) {
		String imgUrl = document.select(imageSelector).attr("src");

		return imgUrl.substring(imgUrl.lastIndexOf('.'), imgUrl.length());
	}

	private String getTitleName(Document document)
	{
		if (titleSelector == null)
			return "";

		String text = document.select(titleSelector).text();

		if (titleRegex == null)
			return text;

		Matcher matcher = Pattern.compile(titleRegex).matcher(text);

		if (!matcher.find())
			return "REGEX DOES NOT MATCHES";
		return matcher.group(1);
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