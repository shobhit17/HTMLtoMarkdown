import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class test {
	
	static int spaceInList;
	static Stack<HTMLTag> stack;
	static int tableHeadCount;
	static boolean contentInHead=false;
	static int totalTags;
	static int tagsConverted;
	enum EmptyTags{HR,BR,IMG};
	
	static String getMarkdownEquivalent(HTMLTag tag){
		String mark="";
		tagsConverted++;
		switch(tag.getElement().toLowerCase()){
		case "head":if(tag.isOpenTag())
						contentInHead=true;
					else{
						contentInHead=false;
					}
					break;
		case "p":mark=System.lineSeparator();break;
		case "br":mark=System.lineSeparator();break;
		case "section":
		case "code":mark+="`";break;
		case "h1":if(tag.isOpenTag())mark=System.lineSeparator()+"# ";break;
		case "h2":if(tag.isOpenTag())mark=System.lineSeparator()+"## ";break;
		case "h3":if(tag.isOpenTag())mark=System.lineSeparator()+"### ";break;
		case "h4":if(tag.isOpenTag())mark=System.lineSeparator()+"#### ";break;
		case "h5":if(tag.isOpenTag())mark=System.lineSeparator()+"##### ";break;
		case "h6":if(tag.isOpenTag())mark=System.lineSeparator()+"###### ";break;
		case "hr":if(tag.isOpenTag())mark=System.lineSeparator()+"---"+System.lineSeparator();break;
		case "strong":
		case "b":mark="**";break;
		case "i":
		case "em":mark="_";break;
		case "strike":mark="~~";break;
		case "blockquote":if(tag.isOpenTag())mark=">";break;
		case "ol":
		case "ul":if(tag.isOpenTag())spaceInList+=3;
				  else spaceInList-=3;
				  break;
		case "li":
			if(tag.isOpenTag()){
				mark+=System.lineSeparator();
				for(int i=0;i<spaceInList;i++)mark+=" ";
				if(stack.peek().getElement().equalsIgnoreCase("ul")){
					mark+="* ";
				}
				else{
					mark+="1. ";
				}
			}
			break;
		case "pre":mark+=(System.lineSeparator()+"```"+System.lineSeparator());break;
		case "tr":if(tag.isOpenTag())
					mark+=System.lineSeparator();
				  else{
					  if(tableHeadCount>0)
						  mark+=System.lineSeparator();
					  for(int i=0;i<tableHeadCount;i++){
						  mark+=("|---");
					  }
					  tableHeadCount=0;
				  }
				  break;
		case "th":
			      if(tag.isOpenTag()){
			    	  mark+=("| ");
			    	  tableHeadCount+=1;  
			      }
				  break;
		case "td":
				  if(tag.isOpenTag()){
		    	    mark+=("| ");
		    	  }
			  break;		
		default:mark="";tagsConverted--; 
		}
		return mark;
	}
	 
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
//	                System.out.println("Element is "+ nextToken.getElement());
//	                System.out.println("Attribute is "+nextToken.getAttribute());
	            	queue.add(nextToken);
	            }
	        }
	        
	        return queue;
	    }
	 
	 private static HTMLTag nextToken(StringBuffer htmlText) {
		 int indexAg=htmlText.indexOf("<");
		 int indexText = -1;
		 Pattern p = Pattern.compile("[a-zA-Z0-9$+-]");
		 Matcher m = p.matcher(htmlText);
		 if (m.find()) {
		     indexText = m.start();
		 }
	     if(indexAg<indexText){
		        int i1=indexAg;
		        int i2=htmlText.indexOf(">");
		        
		        if (i2>0) {
		            String tag=htmlText.substring(i1+1,i2).trim();
		            String tagAndAttr[]=tag.split(" ");
		            String element=tagAndAttr[0];
		            String attribute="#";
		            if(element.equalsIgnoreCase("a")){
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
		            				attribute=tagAndAttr[i].substring(startLink+1,endLink );
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
		            				attribute=tagAndAttr[i].substring(startLink+1,endLink );
		            			}
		            		}
		            	}
			            	
		            }
		            
		            boolean isOpenTag = true;
		            if (element.indexOf("/") == 0) {
		                isOpenTag = false;
		                element = element.substring(1);
		            }
		            htmlText.delete(0, i2 + 1);
		            Pattern np = Pattern.compile("[^a-zA-Z0-9]");
		            element = np.matcher(element).replaceAll("");//removes / in self closing tags
		            totalTags++;
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
		        	String attribute=htmlText.substring(0,i2);
		        	htmlText.delete(0, i2);
		        	attribute=attribute.trim();
		        	attribute=attribute.replace("\\","\\\\");
		        	attribute=attribute.replace(".","\\.");
		        	attribute=attribute.replace("+","\\+");
		        	attribute=attribute.replace("-","\\-");
		  
		        	return new HTMLTag(element, isOpenTag, attribute);
		        }
	        	else{
	        		return null;
	        	}
	        }
	 }
	
	 public static void main(String[] args) {
		totalTags=0;
		tagsConverted=0;
		FileReader fileReader;
		String fileName="normal";
		String fileContents="";
		try {
			fileReader = new FileReader(fileName+".html");
			int i ;
			while((i=fileReader.read())!=-1){
				char ch = (char)i;
				fileContents = fileContents+ch; 
			  }
			fileReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not Found");
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		LinkedList<HTMLTag> tokens=tokenize(fileContents);
		System.out.println("Tokenization Completed....");
		ListIterator<HTMLTag> it = tokens.listIterator(0);
		stack=new Stack<HTMLTag>();
		spaceInList=0;
		String markdownText="";
		while(it.hasNext()){
			HTMLTag tag=it.next();
			if(!tag.getElement().equalsIgnoreCase("Text")){
//				System.out.println("Inside Tags");
				if(!(tag.getElement().equalsIgnoreCase("img")||tag.getElement().equalsIgnoreCase("a")))
					markdownText+=(getMarkdownEquivalent(tag));
				if(tag.isOpenTag()){
					if(!checkEmptyTag(tag))
						stack.push(tag);
				}
				else if(!tag.isOpenTag()){
					if(stack.peek().getElement().equalsIgnoreCase(tag.getElement())){
						stack.pop();
					}
					else{
						System.out.println("Tags in HTML file are not balanced");
						System.out.println(stack.peek().getElement()+" != "+tag.getElement());
						break;
					}
				}
				if(tag.getElement().equalsIgnoreCase("a")&&tag.isOpenTag()){
					String href=tag.getAttribute();
					String textOfLink="#";
					tag=it.next();
					if(tag.getElement().equalsIgnoreCase("Text")){
						textOfLink=tag.getAttribute();
					}
					markdownText+=("["+textOfLink+"]("+href+")");
				}
				else if(tag.getElement().equalsIgnoreCase("img")){
					String href=tag.getAttribute();
					markdownText+=("![Image]("+href+")");
				}
			}
			else{
				if(!contentInHead)
				markdownText+=(tag.getAttribute());
			}
		}
		System.out.println("MarkdownText is\n");
		System.out.println(markdownText);
		System.out.println();
		System.out.println("Total Tags "+totalTags);
		System.out.println("Tags Converted "+tagsConverted);
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter(fileName+".md"));
		    out.write(markdownText); 
		    out.close();
		}
		catch (IOException e)
		{
		    e.printStackTrace();
		}
	}
	

}
