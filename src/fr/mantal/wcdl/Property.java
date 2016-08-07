package fr.mantal.wcdl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Property
{
	private String selector = null;
	private String attribute = null;
	private String regex = null;


	Property(String selector, String attribute, String regex)
	{
		this.selector = selector;
		this.attribute = attribute;
		this.regex = regex;
	}

	Property(String selector, String attribute)
	{
		this(selector, attribute, null);
	}

	Property(String selector)
	{
		this(selector, null, null);
	}

	Property()
	{
	}

	String get(Document document)
	{
		if (selector == null)//TODO check if this should happen
			return "";

		Element element = document.select(selector).first();

		String text = attribute == null ? element.text() : element.attr(attribute);

		if (regex == null)
			return text;

		Matcher matcher = Pattern.compile(regex).matcher(text);

		if (!matcher.find())
			return null;
		return matcher.group(1);
	}
}
