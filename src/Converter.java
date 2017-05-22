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


public class Converter implements Runnable{
	
	private String fileName;
	private int spaceInList;
	private Stack<HTMLTag> stack;
	private int tableHeadCount;
	private boolean contentInHead=false;
	private int totalTags;
	private int tagsConverted;
	enum EmptyTags{HR,BR,IMG,LINK,META};
	
	Converter(String fileName){
		totalTags=0;
		tagsConverted=0;
		stack=new Stack<HTMLTag>();			//Stack to check tag balancing
		spaceInList=0;                      //Variable to keep track of spaces to indent list in markdown
		this.fileName=fileName;
		
	}
	
	public String getMarkdownEquivalent(HTMLTag tag){
		String mark="";
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
	 
	 public boolean checkEmptyTag(HTMLTag tag){
		 boolean isEmptyTag=false;
		 for(EmptyTags e:EmptyTags.values()){
			 if(e.toString().equalsIgnoreCase(tag.getElement())){
				 isEmptyTag=true;
			 }
		 }
		 if(tag.getElement().equals("!--"))
			 isEmptyTag=true;
		 return isEmptyTag;
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
	 
	 public LinkedList<HTMLTag> tokenize(StringBuffer text) {
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
	 
	 public HTMLTag nextToken(StringBuffer htmlText) {
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
		            return new HTMLTag(element, isOpenTag,attribute,isSelfClosed);
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
		        	attribute=replaceSpecialChar(attribute);
		        	return new HTMLTag(element, isOpenTag, attribute,false);
		        }
	        	else{
	        		return null;
	        	}
	        }
	 }
	 
	 public StringBuffer readfile(String htmlFile){
		 FileReader fileReader;
		 StringBuffer fileContents=new StringBuffer("");
		 try {
				fileReader = new FileReader(htmlFile);
				int i ;
				while((i=fileReader.read())!=-1){
					char ch = (char)i;
					fileContents = fileContents.append(ch); 
				  }
				fileReader.close();
			} catch (FileNotFoundException e) {
				System.out.println("File not Found");
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
		 return fileContents;
	 }
	 
	 public void writeIntoFile(String file,String content){
		 try {
			    BufferedWriter out = new BufferedWriter(new FileWriter(file));
			    out.write(content); 
			    out.close();
			}
			catch (IOException e)
			{
			    e.printStackTrace();
			}
	 }
	 
	 public StringBuffer convert(StringBuffer fileContents){
		 	LinkedList<HTMLTag> tokens=tokenize(fileContents);
			System.out.println("Tokenization Completed....");
			
			ListIterator<HTMLTag> it = tokens.listIterator(0);
			StringBuffer markdownText=new StringBuffer("");
			while(it.hasNext()){
				HTMLTag tag=it.next();
				if(!tag.getElement().equalsIgnoreCase("Text")){
					tagsConverted++;
//					System.out.println("Inside Tags");
					if(!(tag.getElement().equalsIgnoreCase("img")||tag.getElement().equalsIgnoreCase("a")))
						markdownText.append((getMarkdownEquivalent(tag)));
					if(tag.isOpenTag()){
						if((!checkEmptyTag(tag))&&(!tag.isSelfClosed()))
							stack.push(tag);
					}
					else{
						if(stack.peek().getElement().equalsIgnoreCase(tag.getElement())){
							stack.pop();
						}
						else{
							System.out.println("Tags in HTML file are not balanced");
							System.out.println(stack.peek().getElement());
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
							markdownText.append("["+textOfLink+"]("+href+")");
						}
						else{
							tag=it.previous();
						}
					}
					else if(tag.getElement().equalsIgnoreCase("img")){
						String href=tag.getAttribute();
						markdownText.append("![Image]("+href+")");
					}
				}
				else{
					if(!contentInHead){      //Do not print text inside Head Tag of HTML eg. Title 
						
						markdownText.append(tag.getAttribute());
					}
				}
			}
			if(!stack.isEmpty()){
				System.out.println("HTML file is not Balanced");
			}
			return markdownText;

	 }
	 

	@Override
	public void run() {
		StringBuffer fileContents=readfile("TestData\\"+fileName+".html");
//		System.out.println("HTML File:");
//		System.out.println(fileContents);
		StringBuffer markdownText=convert(fileContents);
		writeIntoFile("TestData\\"+fileName+".md",markdownText.toString());
//		System.out.println("MarkdownText is\n");
//		System.out.println(markdownText);
		System.out.println();
		System.out.println(fileName+": Total Tags "+totalTags);
		System.out.println(fileName+": Tags Converted "+tagsConverted);
		System.out.println(fileName+" KB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);
	}
	

}
