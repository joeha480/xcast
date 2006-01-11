package XCCore;

import java.io.File;

/**
 * 
 * ... beskrivning ...
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-sep-22
 * @since 1.0
 */
public abstract class XCTool {
	public final static int TOOL_TYPE_ANALYZER = 0;
	public final static int TOOL_TYPE_AUTOMATIC_TRANSFORMER = 1;
	public final static int TOOL_TYPE_INTERACTIVE_TRANSFORMER = 1;
	protected XCBridge bridge;
	private int toolType;

	/**
	 * 
	 * @param bridge
	 * @param toolType
	 */
	public XCTool(XCBridge bridge, int toolType) {
		this.bridge = bridge;
		this.toolType = toolType;
	}
	
	/**
	 * 
	 * @param doc
	 * @return returns true if the execution was successful, false otherwise
	 */
	public abstract boolean execute();

}
