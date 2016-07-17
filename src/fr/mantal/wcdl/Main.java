package fr.mantal.wcdl;

import java.nio.file.Paths;

public class Main
{
	public static void main(String[] args)
	{
		String localPath = args.length > 1 ? args[0] : Paths.get(System.getProperty("user.home"), "Pictures", "Comics").toString();

		Downloader phdcomics = Downloader.FromConfig(Paths.get(localPath, "PHD comics").toString());
		Downloader sinfest = Downloader.FromConfig(Paths.get(localPath, "Sinfest").toString());
		Downloader darkLegacy = new Downloader(".comic-image", ".nextLink", "http://www.darklegacycomics.com/", "1", Paths.get(localPath, "Dark Legacy").toString()).Title(new Property("title"));
		Downloader dilbert = new Downloader(".img-comic", ".nav-right>a", "http://dilbert.com/", "strip/1989-04-16", Paths.get(localPath, "Dilbert").toString()).Title(new Property(".comic-title-date")).ImageFormat("gif");
		Downloader theDevilsPanties = Downloader.FromConfig(Paths.get(localPath, "The Devil's Panties").toString());

		//phdcomics.download();
		//sinfest.download();
		//darkLegacy.download();
		//dilbert.download();
		theDevilsPanties.download();
	}
}
