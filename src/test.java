import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;


public class test {

	static Stack<Integer> listFlag;

	static String getMarkdownEquivalent(HTMLTag tag){
		String mark="";
		switch(tag.getElement()){
		case "h1":mark="#";break;
		case "h2":mark="##";break;
		case "h3":mark="###";break;
		case "h4":mark="####";break;
		case "h5":mark="#####";break;
		case "h6":mark="######";break;
		case "hr":mark="---";break;
		case "strong":
		case "b":mark="**";break;
		case "<i>":
		case "</i>":	
		case "</em>":
		case "<em>":mark="_";break;
		case "</strike>":
		case "<strike>":mark="~~";break;
		case "<blockquote>":mark=">";break;
		case "<ul>":listFlag.push(new Integer(1));break;
		case "</ul>":listFlag.pop();break;
		case "<ol>":listFlag.push(new Integer(2));break;
		case "</ol>":listFlag.pop();break;
		case "<li>":
			if(listFlag.peek()==1)mark=" +";
			else if(listFlag.peek()==2)mark=" 1.";
			break;
		default:mark=tag.getElement(); 
		}
		
		
		return mark;
	}
	 public enum EmptyTags{HR,BR};
	 public static boolean checkEmptyTag(HTMLTag tag){
		 boolean isEmptyTag=false;
		 for(EmptyTags e:EmptyTags.values()){
			 if(e.toString().equalsIgnoreCase(tag.getElement())){
				 isEmptyTag=true;
			 }
		 }
		 return isEmptyTag;
	 }
	 public static LinkedList<HTMLTag> tokenize(String text) {
	        StringBuffer buf = new StringBuffer(text);
	        LinkedList<HTMLTag> queue = new LinkedList<HTMLTag>();

	        while (true) {
	            HTMLTag nextToken = nextToken(buf);
	            if (nextToken == null) {
	                break;
	            } else {
	                queue.add(nextToken);
	            }
	        }
	        
	        return queue;
	    }
	 
	 private static HTMLTag nextToken(StringBuffer htmlText) {
	        if(htmlText.charAt(0)=='<'){
		        int i1=0;
		        int i2=htmlText.indexOf(">");
		        
		        if (i2>0) {
		            String tag=htmlText.substring(i1+1,i2).trim();
		            String tagAndAttr[]=tag.split(" ");
		            String element=tagAndAttr[0];
		            String attribute=null;
		            if(tagAndAttr.length>1)
		            	attribute=tagAndAttr[1];
		            
		            boolean isOpenTag = true;
		            if (element.indexOf("/") == 0) {
		                isOpenTag = false;
		                element = element.substring(1);
		            }
		            //element = element.replaceAll("[^a-zA-Z0-9!-]+", "");
		            htmlText.delete(0, i2 + 1);
		            return new HTMLTag(element, isOpenTag,attribute);
		        } else {
		            return null;
		            }
	        }
	        else{
	        	int i2=htmlText.indexOf("<");
	        	if(i2>0){
		        	String element="Text";
		        	boolean isOpenTag=false;
		        	String attribute=htmlText.substring(0,i2).trim();
		        	return new HTMLTag(element, isOpenTag, attribute);
		        }
	        	else{
	        		return null;
	        	}
	        }
	 }
	public static void main(String[] args) {
		FileReader fileReader;
		String fileContents="";
		try {
			fileReader = new FileReader("readme.txt");
			int i ;
			while((i=fileReader.read())!=-1){
				char ch = (char)i;
				fileContents = fileContents+ch; 
			  }
			System.out.println(fileContents);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		LinkedList<HTMLTag> tokens=tokenize(fileContents);
		ListIterator<HTMLTag> it = tokens.listIterator(0);
		Stack<HTMLTag> stack=new Stack<HTMLTag>();
		String markdownText="";
		while(it.hasNext()){
			HTMLTag tag=it.next();
			if(!tag.getElement().equalsIgnoreCase("Text")){
				if(tag.isOpenTag()){
					markdownText+=getMarkdownEquivalent(tag);
					if(!checkEmptyTag(tag))
						stack.push(tag);
				}
				else if(!tag.isOpenTag()){
					if(stack.peek().getElement().equalsIgnoreCase(tag.getElement())){
						stack.pop();
					}
					else{
						System.out.println("Tags in HTML file are not balanced");
					}
				}
			}
			else{
				markdownText+=tag.getAttribute();
			}
		}
	}
	

}
