package mobi.chouette.exchange;

/**
 * Generic cvdtc test element
 * @author gjamot
 *
 */
public class Test {
	private int level;
	private String code;
	private String severity;
	
	
	public Test(int level, String code, String severity) {
		this.level = level;
		this.code = code;
		this.severity = severity;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public String getSeverity() {
		return this.severity;
	}
}
