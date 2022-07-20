package com.kisszo.news.netty.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;



public class XssSanitizerUtil {
private static List<Pattern> XSS_INPUT_PATTERNS = new ArrayList<Pattern>();
    
	static {
			// Avoid anything between script tags
			XSS_INPUT_PATTERNS.add(Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE));

			// avoid iframes
			XSS_INPUT_PATTERNS.add(Pattern.compile("<iframe(.*?)>(.*?)</iframe>", Pattern.CASE_INSENSITIVE));

			// Avoid anything in a src='...' type of expression
			XSS_INPUT_PATTERNS.add(Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));

			XSS_INPUT_PATTERNS.add(Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));

			XSS_INPUT_PATTERNS.add(Pattern.compile("src[\r\n]*=[\r\n]*([^>]+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));

			// Remove any lonesome </script> tag
			XSS_INPUT_PATTERNS.add(Pattern.compile("</script>", Pattern.CASE_INSENSITIVE));

			// Remove any lonesome <script ...> tag
			XSS_INPUT_PATTERNS.add(Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));

			// Avoid eval(...) expressions
			XSS_INPUT_PATTERNS.add(Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));

			// Avoid expression(...) expressions
			XSS_INPUT_PATTERNS.add(Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));

			// Avoid javascript:... expressions
			XSS_INPUT_PATTERNS.add(Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE));

			// Avoid vbscript:... expressions
			XSS_INPUT_PATTERNS.add(Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE));

			// Avoid onload= expressions
			XSS_INPUT_PATTERNS.add(Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
	}

	/**
	 * This method takes a string and strips out any potential script injections.
	 *
	 * @param value
	 * @return String - the new "sanitized" string.
	 */
	public static Boolean stripXSS(String value) {
		boolean flag = false;
		int length = value.length();
		String changedvalue = value;
		System.out.println("search text +"+value);
		try {

			if (changedvalue != null) {
				// NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
				// avoid encoded attacks.
				//value = ESAPI.encoder().canonicalize(value);

				// Avoid null characters
				changedvalue = changedvalue.replaceAll("\0", "");
				
				// test against known XSS input patterns
				for (Pattern xssInputPattern : XSS_INPUT_PATTERNS) {
					changedvalue = xssInputPattern.matcher(changedvalue).replaceAll("");
					
					
				}
				System.out.println("text length "+ changedvalue.length() +" ---"+length);
				if(changedvalue.length() != length){
					flag = true;
				}
			}
			
			

		} catch (Exception ex) {
			System.out.println("Could not strip XSS from value = " + value + " | ex = " + ex.getMessage());
		}

		return flag;
	}


}
