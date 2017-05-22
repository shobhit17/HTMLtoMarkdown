
public class HTMLTag {
	private String element;
	private boolean isOpenTag;
	private String attribute;
	private boolean isSelfClosed;
	
	public boolean isSelfClosed() {
		return isSelfClosed;
	}

	public String getElement() {
		return element;
	}
	
	public boolean isOpenTag() {
		return isOpenTag;
	}
	
	public String getAttribute() {
		return attribute;
	}
	
	
	public HTMLTag(String element,boolean isOpenTag,String attribute,boolean isSelfClose){
		this.element=element;
		this.isOpenTag=isOpenTag;
		this.attribute=attribute;
		this.isSelfClosed=isSelfClose;
	}
	public boolean isClosingof(HTMLTag HTMLCloseTag){
		boolean correct=false;
		if(getElement().equals(HTMLCloseTag.getElement()) && isOpenTag()!=HTMLCloseTag.isOpenTag()){
				correct=true;
		}
		return correct;
	}
}
