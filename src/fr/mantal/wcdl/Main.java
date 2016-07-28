package fr.mantal.wcdl;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main
{
	public static void main(String[] args)
	{
		Downloader.fromConfig(getPath(args[0])).download();
	}

	private static String getPath(String arg0)
	{
		if (arg0.isEmpty())
			arg0 = System.getProperty("user.dir");

		if (Files.isDirectory(Paths.get(arg0)))
			return Paths.get(arg0, "config.wcdl").toString();
		return arg0;
	}
}
