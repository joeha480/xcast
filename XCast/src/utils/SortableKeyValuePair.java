/*
 * Created on 2005-jun-01
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package utils;

/**
 * ... beskrivning ...
 * 
 * @author  Joel Håkansson, TPB
 * @version 2005-jun-01
 * @see 
 * @since 1.0
 */
public class SortableKeyValuePair implements Comparable {
	private String key;
	private int value;
	/**
	 * 
	 */
	public SortableKeyValuePair(String key, int value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	public int compareTo(Object o) {
		return key.toString().compareToIgnoreCase(o.toString());
	}
	
	public String getKey() {
		return key;
	}
	
	public int getValue() {
		return value;
	}
	
	public String toString() {
		return key + value + hashCode();
	}

}
