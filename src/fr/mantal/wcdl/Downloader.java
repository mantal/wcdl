package fr.mantal.wcdl;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	private String imageFormat = null;

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

	Downloader ImageFormat(String format)
	{
		this.imageFormat = "." + format;
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

			if (imageFormat == null)
				imageFormat = getFileFormat(document);

			File file = Paths.get(localPath + i + " - " + getTitleName(document) + imageFormat).toFile();

			i++;
			if (fileExist(file))
				continue;

			copyImageToFile(document, file);

		} while((url = getNextUrl(document)) != null);
	}

	private String getFileFormat(Document document) {
		String imgUrl = document.select(imageSelector).attr("src");

		return imgUrl.substring(imgUrl.lastIndexOf('.'), imgUrl.length());
	}

	private String escapePath(String path)
	{
		return path.replace('/', '-').replace('\\', '-').replace('?', '-');//todo regex
	}

	private String getTitleName(Document document)
	{
		if (titleSelector == null)
			return "";

		String text = document.select(titleSelector).first().text();

		if (titleRegex == null)
			return escapePath(text);

		Matcher matcher = Pattern.compile(titleRegex).matcher(text);

		if (!matcher.find())
			return "REGEX DOES NOT MATCHES";
		return escapePath(matcher.group(1));
	}

	private boolean copyImageToFile(Document document, File file)
	{
		String url = document.select(imageSelector).first().attr("src");

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
		String url = document.select(nextLinkSelector).first().attr("href");

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
		{
			if (url.charAt(0) == '/')
				url = url.substring(1);
			url = baseUrl + url;
		}
		return url;
	}

	private static boolean fileExist(File file)
	{
		return file.isFile() && file.exists();//todo check
	}
}