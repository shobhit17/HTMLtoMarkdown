
public class HTMLTag {
	private String element;
	private boolean closingReq;
	private boolean isOpenTag;
	private String attribute;
	
	public String getElement() {
		return element;
	}
	public void setElement(String element) {
		this.element = element;
	}
	public boolean isClosingReq() {
		return closingReq;
	}
	public void setClosingReq(boolean closingReq) {
		this.closingReq = closingReq;
	}
	public boolean isOpenTag() {
		return isOpenTag;
	}
	public void setOpenTag(boolean isOpenTag) {
		this.isOpenTag = isOpenTag;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	
	
	/*public HTMLTag(String element,boolean isOpenTag){
		this.element=element;
		this.isOpenTag=isOpenTag;
	}*/
	
	public HTMLTag(String element,boolean isOpenTag,String attribute){
		this.element=element;
		this.isOpenTag=isOpenTag;
		this.attribute=attribute;
	}
	public boolean isClosingof(HTMLTag HTMLCloseTag){
		boolean correct=false;
		if(getElement().equals(HTMLCloseTag.getElement()) && isOpenTag()!=HTMLCloseTag.isOpenTag()){
				correct=true;
		}
		return correct;
	}
}
