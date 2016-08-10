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

class Downloader
{
	private String baseUrl;
	private String startPath;
	private String localPath;

	private Property content;
	private Property next;
	private Property title;
	private Property skipCondition;
	private Property keepCondition;

	private String fileFormat = null;
	private boolean internal = false;

	private final static String userAgent = " wcdl/0.1 (+https://github.com/mantal/wcdl/issues)";

	static Downloader fromConfig(String path)//todo better param name
	{
		String file = null;
		try {
			file = FileUtils.readFileToString(new File(path), "utf-8");
		} catch (IOException e) {
			Error.Fatal(Error.FILE_ERROR, "Could not open config file: " + e.getMessage());
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
				document = Jsoup.connect(url.toString())
						.userAgent(userAgent)
						.get();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String titleString = title.get(document);

			if (skipContent(document))
			{
				System.out.println("Skipping: '" + titleString + "' from " + url.toString());
				continue;
			}

			System.out.println("Downloading: '" + titleString + "' from " + url.toString());

			if (fileFormat == null)
				fileFormat = getFileFormat(document);

			File file = Paths.get(localPath + i + " - " + escapePath(titleString) + fileFormat).toFile();

			i++;
			if (fileExist(file))
				continue;

			copyContentToFile(document, file);

		} while((url = getNextUrl(document)) != null);
	}

	private boolean skipContent(Document document)
	{
		if (skipCondition != null && skipCondition.get(document) != null)
			return true;
		if (keepCondition != null && keepCondition.get(document) == null)
			return true;
		return false;
	}

	private String getFileFormat(Document document)
	{
		String contentUrl = content.get(document);

		return contentUrl.substring(contentUrl.lastIndexOf('.'), contentUrl.length());
	}

	private String escapePath(String path)
	{
		return path.replace('/', '-').replace('\\', '-').replace('?', '-').replace(':', '-');//todo regex
	}

	private boolean copyContentToFile(Document document, File file)
	{
		String selectedContent = content.get(document);

		if (selectedContent == null || selectedContent.isEmpty())
			Error.Fatal(Error.CONFIG_ERROR, "Content not found or empty");

		if (internal)
			return downloadFromHTML(selectedContent, file);

		selectedContent = fixUrl(selectedContent);

		return downloadFromURL(selectedContent, file);
	}

	private boolean downloadFromURL(String url, File file)
	{
		try
		{
			byte[] buffer = Jsoup.connect(url.toString())
					.userAgent(userAgent)
					.ignoreContentType(true)
					.execute()
					.bodyAsBytes();
			FileUtils.writeByteArrayToFile(file, buffer);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;

	}

	private boolean downloadFromHTML(String content, File file)
	{
		try
		{
			FileUtils.writeStringToFile(file, content, "UTF-16");
		}
		catch (IOException e)
		{
			Error.Fatal(Error.FILE_ERROR, "Could not write content to file");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private URL getNextUrl(Document document)
	{
		String url = next.get(document);

		if (url == null || url.isEmpty())
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