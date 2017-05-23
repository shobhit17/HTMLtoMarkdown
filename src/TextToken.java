
public class TextToken implements Token{
	private String type;
	private String tagContent;
	
	public String getType() {
		return type;
	}
	public String getTagContent() {
		return tagContent;
	}
	TextToken(String tagContent){
		this.type="Text";
		this.tagContent=tagContent;
	}
}
