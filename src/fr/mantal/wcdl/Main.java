package fr.mantal.wcdl;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main
{
	public static void main(String[] args)
	{
		Downloader.fromConfig(getPath(args)).download();
	}

	private static String getPath(String[] args)
	{
		String arg0 = args.length > 1 ? args[0] : System.getProperty("user.dir");

		if (Files.isDirectory(Paths.get(arg0)))
			return Paths.get(arg0, ".config.wcdl").toString();
		return arg0;
	}
}
