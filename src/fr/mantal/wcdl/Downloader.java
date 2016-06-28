package fr.mantal.wcdl;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class Downloader
{

	private String baseUrl;
	private String startPath;
	private String localPath;

	private Property image;
	private Property next;
	private Property title;

	private String imageFormat = null;

	Downloader(String imageSelector, String nextLinkSelector, String baseUrl, String startPath, String localPath)
	{
		this.image = new Property(imageSelector, "src");
		this.next = new Property(nextLinkSelector, "href");
		this.title = new Property();
		this.baseUrl = baseUrl;
		this.localPath = localPath + File.separator;
		this.startPath = startPath;
	}

	Downloader Title(Property title)
	{
		this.title = title;
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

			File file = Paths.get(localPath + i + " - " + escapePath(title.get(document)) + imageFormat).toFile();

			i++;
			if (fileExist(file))
				continue;

			copyImageToFile(document, file);

		} while((url = getNextUrl(document)) != null);
	}

	private String getFileFormat(Document document) {
		String imgUrl = image.get(document);

		return imgUrl.substring(imgUrl.lastIndexOf('.'), imgUrl.length());
	}

	private String escapePath(String path)
	{
		return path.replace('/', '-').replace('\\', '-').replace('?', '-');//todo regex
	}

	private boolean copyImageToFile(Document document, File file)
	{
		String url = image.get(document);

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
		String url = next.get(document);

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