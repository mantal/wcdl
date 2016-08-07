package fr.mantal.wcdl;

import com.google.gson.Gson;
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

	private Property content;
	private Property next;
	private Property title;

	private String fileFormat = null;

	static Downloader fromConfig(String path)//todo better param name
	{
		String file = null;
		try {
			file = FileUtils.readFileToString(new File(path), "utf-8");
		} catch (IOException e) {
			System.out.println("Can not open config file: " + e.getMessage());
			System.exit(ExitError.CONFIG_OPEN_ERROR);
		}
		Downloader res = new Gson().fromJson(file, Downloader.class);
		res.localPath = Paths.get(path).getParent().toString() + File.separator;
		return res;
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

			File file = Paths.get(localPath + i + " - " + escapePath(title.get(document)) + fileFormat).toFile();

			i++;
			if (fileExist(file))
				continue;

			copyContentToFile(document, file);

		} while((url = getNextUrl(document)) != null);
	}

	private String getFileFormat(Document document) {
		String contentUrl = content.get(document);

		return contentUrl.substring(contentUrl.lastIndexOf('.'), contentUrl.length());
	}

	private String escapePath(String path)
	{
		return path.replace('/', '-').replace('\\', '-').replace('?', '-');//todo regex
	}

	private boolean copyContentToFile(Document document, File file)
	{
		String url = content.get(document);

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