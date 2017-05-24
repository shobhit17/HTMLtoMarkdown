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
	}

	public void tokenize() {
		StringBuffer buf = new StringBuffer(htmlText);
		tokens = new LinkedList<Token>();
		while (true) {
			Token nextToken = nextToken(buf);  //Returns next token and delete the content read from the buf
			if (nextToken == null) {
				break;
			} else {
				//System.out.println("Element is "+ nextToken.getElement());
				//System.out.println("Attribute is "+nextToken.getAttribute());
				tokens.add(nextToken);
			}
		}
	}

	private String replaceSpecialChar(String text){
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

	private Token nextToken(StringBuffer htmlText) {
		int indexAg=htmlText.indexOf("<");
		int indexText = -1;
		Pattern textRegex = Pattern.compile("[^\n\t\r<]"); //Any character except Whitespaces and <  
		Matcher match = textRegex.matcher(htmlText);
		if (match.find()) {
			indexText = match.start();  				//indexText is index of first character except <
		}
		if(indexAg<indexText){                  // to find if next token will be a HTMLtag or text. True when HTMLTag
			int i1=indexAg;
			int i2=htmlText.indexOf(">"); 
			if (i2>0) {
				String tag=htmlText.substring(i1+1,i2).trim();		//content of tag will be from < to >	
				boolean isSelfClosed=false;
				if(tag.charAt(tag.length()-1)=='/'){
					isSelfClosed=true;								
				}
				String tagAndAttr[]=tag.split(" ");
				String element=tagAndAttr[0];					// first word will be the actual tag
				String attribute=null;
				boolean isOpenTag = true;
				if (element.indexOf("/") == 0) {				// true when tag is close
					isOpenTag = false;
					element = element.substring(1);
				}

				if(element.equalsIgnoreCase("a")&&isOpenTag){   		// to extract href attribute in case of a tag
					if(tagAndAttr.length>1){
						for(int i=0;i<tagAndAttr.length;i++){
							if(tagAndAttr[i].contains("href")){
								int startLink=tagAndAttr[i].indexOf("\"");		// case when link is surrounded using " "
								int endLink=tagAndAttr[i].lastIndexOf("\"");
								if(startLink<0){
									startLink=tagAndAttr[i].indexOf("\'");		// case if link is surrounded using ' '
									endLink=tagAndAttr[i].lastIndexOf("\'");
								}
								if(startLink<0){
									startLink=tagAndAttr[i].indexOf("=");  		// case when link is not surrounded
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
				if(element.equalsIgnoreCase("img")){			//to extract src link in case of img tag
					if(tagAndAttr.length>1){
						for(int i=0;i<tagAndAttr.length;i++){
							if(tagAndAttr[i].contains("src")){
								int startLink=tagAndAttr[i].indexOf("\"");		// case when link is surrounded using " "
								int endLink=tagAndAttr[i].lastIndexOf("\"");	
								if(startLink<0){
									startLink=tagAndAttr[i].indexOf("\'");		// case when link is surrounded using ' '
									endLink=tagAndAttr[i].lastIndexOf("\'");
								}
								if(startLink<0){
									startLink=tagAndAttr[i].indexOf("=");		// case when link is not surrounded
									endLink=tagAndAttr[i].length();
								}
								if(endLink>startLink)
									attribute=tagAndAttr[i].substring(startLink+1,endLink );
							}
						}
					}

				}


				htmlText.delete(0, i2 + 1);						// removes the content from htmlText after converting it to node 
				element=element.replaceAll("[^a-zA-Z0-9]", ""); //cleaning of Element 
				totalTags++;
				return new HTMLTag(element,isOpenTag,attribute,isSelfClosed);
			} else {
				return null;
			}
		}
		else{										// Case when token is Text
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
