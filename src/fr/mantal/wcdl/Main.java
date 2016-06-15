package fr.mantal.wcdl;

public class Main
{
	public static void main(String[] args)
	{
		String localPath = args.length > 1 ? args[0] : "C:\\Users\\Username\\Pictures\\";

		Downloader phdcomics = new Downloader("#comic", "td[align=left]>a", "http://www.phdcomics.com/comics/", "archive.php?comicid=1", localPath + "PHD comics\\");
		Downloader sinfest = new Downloader("td[colspan=\"2\"]>img", "table[border=\"0\"] td.style5>a", "http://www.sinfest.net/", "view.php?date=2000-01-17", localPath + "sinfest\\");
		Downloader darkLegacy = new Downloader(".comic-image", ".nextLink", "http://www.darklegacycomics.com/", "1", localPath + "Dark Legacy\\");

		phdcomics.download();
		sinfest.download();
		darkLegacy.download();
	}
}
