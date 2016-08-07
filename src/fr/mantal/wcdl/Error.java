package fr.mantal.wcdl;

class Error
{
	// https://www.freebsd.org/cgi/man.cgi?query=sysexits&sektion=3
	public static final int FILE_ERROR = 66;
	public static final int CONFIG_ERROR = 78;

	public static void Fatal(int code, String message)
	{
		System.out.println(message);
		System.exit(code);
	}
}
