/*
 * Created on 2005-mar-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package XCCore;

import java.io.File;
import java.util.Properties;

/**
 * ... beskrivning ...
 * 
 * @author  Joel Håkansson, TPB
 * @version 2005-mar-15
 * @see 
 * @since 1.0
 */
public final class XCMessages extends XCProperties {

	public static final String MENU_FILE = "menu_file";
	public static final String MENU_FILE_ITEM_OPEN = "menu_file_item_open";
	public static final String MENU_FILE_SUBMENU_IMPORT = "menu_file_submenu_import";
	public static final String MENU_FILE_SUBMENU_IMPORT_ITEM_MIF = "menu_file_submenu_import_item_mif";
	public static final String MENU_FILE_SUBMENU_IMPORT_ITEM_FM = "menu_file_submenu_import_item_fm";
	public static final String MENU_FILE_SUBMENU_EXPORT = "menu_file_submenu_export";
	public static final String MENU_FILE_ITEM_SAVE = "menu_file_item_save";
	public static final String MENU_FILE_ITEM_SAVEAS = "menu_file_item_saveas";
	public static final String MENU_FILE_ITEM_SAVEASANDREOPEN = "menu_file_item_saveasandreopen";
	public static final String MENU_FILE_ITEM_EXIT = "menu_file_item_exit";
	public static final String MENU_EDIT = "menu_edit";
	//public static final String MENU_EDIT_SUBMENU_RENDER = "menu_edit_submenu_render_key";
	public static final String MENU_EDIT_ITEM_UPDATE = "menu_edit_item_update";
	//public static final String MENU_EDIT_SUBMENU_RENDER_ITEM_ORIGINAL = "menu_edit_submenu_render_item_original";
	public static final String MENU_EDIT_ITEM_FIND = "menu_edit_item_find";
	public static final String MENU_EDIT_ITEM_CHECK = "menu_edit_item_check";
	public static final String MENU_EDIT_ITEM_SETTINGS = "menu_edit_item_settings";
	public static final String MENU_TOOLS = "menu_tools";
	public static final String MENU_VIEW = "menu_view";
	public static final String MENU_VIEW_ITEM_ORIGINAL = "menu_view_item_original";
	public static final String MENU_VIEW_ITEM_MODIFIED = "menu_view_item_modified";
	public static final String MENU_VIEW_ITEM_HELP = "menu_view_item_help";
	public static final String MENU_VIEW_ITEM_LOG = "menu_view_item_log";
	public static final String MENU_HELP = "menu_help";
	public static final String MENU_HELP_ITEM_CONTENTS = "menu_help_item_contents";
	public static final String MENU_HELP_ITEM_API_REFERENCE = "menu_help_item_apireference";
	public static final String MENU_HELP_ITEM_ABOUT = "menu_help_item_about";

	public static final String TAB_MAPPING = "tab_elements";
	public static final String TAB_POSTPROCESSING = "tab_postprocessing";
	public static final String TAB_ORIGINAL_VIEW = "tab_original_view";
	public static final String TAB_MODIFIED_VIEW = "tab_modified_view";
	public static final String TAB_HELP_VIEW = "tab_help_view";
	public static final String TAB_LOG_VIEW = "tab_log_view";
	public static final String TAB_VIEW = "tab_view";
	public static final String TAB_NEW = "tab_new";
	
	//public static final String TAB_TREE_VIEW_TAB_ORIGINAL = "tab_tree_view_tab_original";
	public static final String DIALOG_EXIT = "dialog_exit";
	public static final String DIALOG_XML_FILTER_DESCRIPTION = "dialog_xml_filter_description";
	public static final String DIALOG_LOAD_LIST_WARNING = "dialog_load_list_warning";
	
	public static final String MSG_DOCUMENT_SAVED = "msg_document_saved";
	public static final String MSG_WARNING = "msg_warning";
	public static final String MSG_FATAL_ERROR = "msg_fatal_error";
	public static final String MSG_FATAL_ERROR_NO_DISPLAY = "msg_error_no_display";
	
	public XCMessages(File languageFile) {
		super(languageFile, "Default language");
	}

	protected Properties getDefaults() {
		Properties defaults;
		defaults = new Properties();
		defaults.setProperty(MENU_FILE, "File");
		defaults.setProperty(MENU_FILE_ITEM_OPEN, "Open...");
		defaults.setProperty(MENU_FILE_SUBMENU_IMPORT, "Import...");
		defaults.setProperty(MENU_FILE_SUBMENU_IMPORT_ITEM_MIF, "FrameMaker MIF");
		defaults.setProperty(MENU_FILE_SUBMENU_IMPORT_ITEM_FM, "FrameMaker FM");
		defaults.setProperty(MENU_FILE_SUBMENU_EXPORT, "Export...");
		defaults.setProperty(MENU_FILE_ITEM_SAVE, "Save");
		defaults.setProperty(MENU_FILE_ITEM_SAVEAS, "Save as...");
		defaults.setProperty(MENU_FILE_ITEM_EXIT, "Exit");
		defaults.setProperty(MENU_EDIT, "Edit");
		//defaults.setProperty(MENU_EDIT_SUBMENU_RENDER, "Render");
		defaults.setProperty(MENU_EDIT_ITEM_UPDATE, "Update");
		//defaults.setProperty(MENU_EDIT_SUBMENU_RENDER_ITEM_ORIGINAL, "Original");
		defaults.setProperty(MENU_EDIT_ITEM_FIND, "Find...");
		defaults.setProperty(MENU_EDIT_ITEM_CHECK, "Check");
		defaults.setProperty(MENU_EDIT_ITEM_SETTINGS, "Settings...");
		defaults.setProperty(MENU_TOOLS, "Tools");
		defaults.setProperty(MENU_VIEW, "View");
		defaults.setProperty(MENU_VIEW_ITEM_ORIGINAL, "Original");
		defaults.setProperty(MENU_VIEW_ITEM_MODIFIED, "Modified");
		defaults.setProperty(MENU_VIEW_ITEM_HELP, "Help");
		defaults.setProperty(MENU_VIEW_ITEM_LOG, "Log");
		defaults.setProperty(MENU_HELP, "Help");
		defaults.setProperty(MENU_HELP_ITEM_CONTENTS, "Contents");
		defaults.setProperty(MENU_HELP_ITEM_API_REFERENCE, "API reference");
		defaults.setProperty(MENU_HELP_ITEM_ABOUT, "About XCast");
		defaults.setProperty(TAB_ORIGINAL_VIEW, "Original");
		defaults.setProperty(TAB_MODIFIED_VIEW, "Modified");
		defaults.setProperty(TAB_HELP_VIEW, "Help");
		defaults.setProperty(TAB_LOG_VIEW, "Log");
		defaults.setProperty(TAB_VIEW, "View");
		defaults.setProperty(TAB_MAPPING, "Mapping");
		defaults.setProperty(TAB_POSTPROCESSING, "Post processing");
		defaults.setProperty(DIALOG_EXIT, "Exit?");
		defaults.setProperty(DIALOG_XML_FILTER_DESCRIPTION, "xml-files");
		defaults.setProperty(DIALOG_LOAD_LIST_WARNING, "{0} out of {1} settings haven't changed.");		
		defaults.setProperty(MSG_DOCUMENT_SAVED, "The document has been saved.");
		defaults.setProperty(MSG_WARNING, "Warning!");
		defaults.setProperty(MSG_FATAL_ERROR, "Fatal Error");
		defaults.setProperty(MSG_FATAL_ERROR_NO_DISPLAY, "Fatal Error: Unable to find display");		
		return defaults;
	}
	
	protected Properties verify(Properties props) {
		return props;
	}
	
}
