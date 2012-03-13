package com.atlantbh.nutch.filter.xpath;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Node;

public class FilterUtils {
	
	/**
	 * Returns the same value. If null returns the defaultValue.
	 * 
	 * @param value The value to return if not null.
	 * @param defaultValue The value to return if null.
	 * @return value or defaultValue. Depends of value is null.
	 */
	public static <T> T getNullSafe(T value, T defaultValue) {
		return value == null?defaultValue:value;
	}
	
	/**
	 * Extracts the text content from the supplied node.
	 * 
	 * @param node The node to extract text from.
	 * @return String that contains the textual content of the node.
	 */
	public static String extractTextContentFromRawNode(Object node) {
		
		// Extract data
		String value = null;
		if (node instanceof Node) {
			value = ((Node) node).getTextContent();
		} else {
			value = String.valueOf(node);
		}
		
		return value;
	}
	
	/**
	 * Check's if the url math the regex. Regex null safe.
	 * 
	 * @param regex The regex to match against.
	 * @param url The data to match.
	 * @return True if the url matches the regex, otherwise false.
	 */
	public static boolean isMatch(String regex, String data) {
		
		// Compile regex pattern
		Pattern pattern = null;
		if(regex != null) {
			pattern = Pattern.compile(regex);
		} 
					
		if(pattern != null) {
			if(pattern.matcher(data).matches()) {
				return true;		
			}
		} else {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if the string is entirely made of the provided characters.
	 * 
	 * @param string The string to check.
	 * @param charactesr The characters to check against.
	 * @return True if the string is entirely made of the characters supplied, otherwise false.
	 */
	public static boolean isMadeOf(String string, String characters) {
		
		// Initialize StringBuilder
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.setLength(1);
		
		// Iterate trough string and check if it contains one of the characters
		for(int i=0;i<string.length();i++) {
			
			stringBuilder.setCharAt(0, string.charAt(i));
			if(!characters.contains(stringBuilder)) {
				return false;
			}
		}
		
		return true;
	}
}
