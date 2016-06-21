package fr.mantal.wcdl;

import java.nio.file.Paths;

public class Main
{
	public static void main(String[] args)
	{
		String localPath = args.length > 1 ? args[0] : Paths.get(System.getProperty("user.home"), "Pictures", "Comics").toString();

		Downloader phdcomics = new Downloader("#comic", "td[align=left]>a", "http://www.phdcomics.com/comics/", "archive.php?comicid=1", Paths.get(localPath, "PHD comics").toString()).Title("title", "PHD Comics: (.+)");
		Downloader sinfest = new Downloader("td[colspan=\"2\"]>img", "table[border=\"0\"] td.style5:last-child>a", "http://www.sinfest.net/", "view.php?date=2000-01-17", Paths.get(localPath, "Sinfest").toString()).Title("td.style3", ".+: (.+)");
		Downloader darkLegacy = new Downloader(".comic-image", ".nextLink", "http://www.darklegacycomics.com/", "1", Paths.get(localPath, "Dark Legacy").toString()).Title("title");

		phdcomics.download();
		sinfest.download();
		darkLegacy.download();
	}
}
