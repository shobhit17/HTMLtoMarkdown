import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Tokenizer {

	private String htmlText;
	private LinkedList<Token> tokens;
	private int totalTags;
	
	

	public LinkedList<Token> getTokens() {
		return tokens;
	}

	public int getTotalTags() {
		return totalTags;
	}

	Tokenizer(String htmlText){
		this.htmlText=htmlText;
		totalTags=0;
		tokenize();
	}

	public void tokenize() {
		StringBuffer buf = new StringBuffer(htmlText);
		tokens = new LinkedList<Token>();
		while (true) {
			Token nextToken = nextToken(buf);
			if (nextToken == null) {
				break;
			} else {
				//System.out.println("Element is "+ nextToken.getElement());
				//System.out.println("Attribute is "+nextToken.getAttribute());
				tokens.add(nextToken);
			}
		}
	}

	public String replaceSpecialChar(String text){
		text=text.replace("\\","\\\\");
		text=text.replace(".","\\.");
		text=text.replace("+","\\+");
		text=text.replace("-","\\-");
		text=text.replace("&nbsp;", " ");
		text=text.replace("&lt;", "<");
		text=text.replace("&gt;", ">");
		text=text.replace("&amp;","&");
		text=text.replace("&quot;","\"");
		text=text.replace("&apos;","\'");
		return text;
	}

	public Token nextToken(StringBuffer htmlText) {
		int indexAg=htmlText.indexOf("<");
		int indexText = -1;
		Pattern p = Pattern.compile("[^\n\t\r<]");  
		Matcher m = p.matcher(htmlText);
		if (m.find()) {
			indexText = m.start();
		}
		if(indexAg<indexText){
			int i1=indexAg;
			int i2=htmlText.indexOf(">");
			if (i2>0) {
				String tag=htmlText.substring(i1+1,i2).trim();
				boolean isSelfClosed=false;
				if(tag.charAt(tag.length()-1)=='/'){
					isSelfClosed=true;
				}
				String tagAndAttr[]=tag.split(" ");
				String element=tagAndAttr[0];
				String attribute=null;
				boolean isOpenTag = true;
				if (element.indexOf("/") == 0) {
					isOpenTag = false;
					element = element.substring(1);
				}

				if(element.equalsIgnoreCase("a")&&isOpenTag){
					if(tagAndAttr.length>1){
						for(int i=0;i<tagAndAttr.length;i++){
							if(tagAndAttr[i].contains("href")){
								int startLink=tagAndAttr[i].indexOf("\"");
								int endLink=tagAndAttr[i].lastIndexOf("\"");
								if(startLink<0){
									startLink=tagAndAttr[i].indexOf("\'");
									endLink=tagAndAttr[i].lastIndexOf("\'");
								}
								if(startLink<0){
									startLink=tagAndAttr[i].indexOf("=");
									endLink=tagAndAttr[i].length();
								}
								if(endLink>startLink){
									attribute=tagAndAttr[i].substring(startLink+1,endLink );
								}
								else{
									attribute="#";
								}
							}

						}
					}

				}
				if(element.equalsIgnoreCase("img")){
					if(tagAndAttr.length>1){
						for(int i=0;i<tagAndAttr.length;i++){
							if(tagAndAttr[i].contains("src")){
								int startLink=tagAndAttr[i].indexOf("\"");
								int endLink=tagAndAttr[i].lastIndexOf("\"");
								if(startLink<0){
									startLink=tagAndAttr[i].indexOf("\'");
									endLink=tagAndAttr[i].lastIndexOf("\'");
								}
								if(startLink<0){
									startLink=tagAndAttr[i].indexOf("=");
									endLink=tagAndAttr[i].length();
								}
								if(endLink>startLink)
									attribute=tagAndAttr[i].substring(startLink+1,endLink );
							}
						}
					}

				}


				htmlText.delete(0, i2 + 1);
				element=element.replaceAll("[^a-zA-Z0-9]", ""); //cleaning of Element 
				totalTags++;
				return new HTMLTag(element,isOpenTag,attribute,isSelfClosed);
			} else {
				return null;
			}
		}
		else{
			int i2=htmlText.indexOf("<");
			if(i2>0){
				String tagContent=htmlText.substring(0,i2);
				htmlText.delete(0, i2);
				tagContent=tagContent.trim();
				tagContent=replaceSpecialChar(tagContent);
				return new TextToken(tagContent);
			}
			else{
				return null;
			}
		}
	}
}
