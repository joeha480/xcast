//------------------------------------------------------------------------------
// Copyright (c) 1999-2001 Matt Welsh.  All Rights Reserved.
//------------------------------------------------------------------------------
package XCGui.AutoComplete;
import java.util.*;

/**
 * A default implementation of the autocomplete dictionary. This implementation
 * is based upon the TreeSet collection class to provide quick lookups and
 * default sorting.
 * 
 * @author Matt Welsh (matt@matt-welsh.com)
 */
public class DefaultDictionary extends TreeSet implements AutoCompleteDictionary {

  //----------------------------------------------------------------------------
  // Public methods
  //----------------------------------------------------------------------------

	public DefaultDictionary() {
		super();
	}
	
	public DefaultDictionary(Collection c) {
		super(c);
	}
/**
   * Adds an entry to the dictionary.
   *
   * @param s The string to add to the dictionary.
   */
  public void addEntry(String s) {
    super.add(s);
  }

  /**
   * Removes an entry from the dictionary.
   *
   * @param s The string to remove to the dictionary.
   * @return True if successful, false if the string is not contained or cannot
   *         be removed.
   */
  public boolean removeEntry(String s) {
    return super.remove(s);
  }

  /**
   * Perform a lookup. This routine returns the closest matching string that
   * completely contains the given string, or null if none is found.
   *
   * @param curr The string to use as the base for the lookup.
   * @return curr The closest matching string that completely contains the
   *              given string.
   */
  public String lookup(String curr) {
    if("".equals(curr)) {
      return null;
    }
    try {
      SortedSet tailSet = this.tailSet(curr);
      if(tailSet != null) {
        Object firstObj = tailSet.first();
        if(firstObj != null) {
          String first = (String)firstObj;
          if(first.startsWith(curr)) {
            return first;
          }
        }
      }
    }
    catch (Exception e) {
      return null;
    }
    return null;
  }
} 