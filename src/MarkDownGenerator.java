import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;



public class MarkDownGenerator {
	private LinkedList<Token> tokens;
	private StringBuffer markdownText;
	private boolean contentInHead;
	private int spaceInList;
	private Stack<HTMLTag> stack;
	private int tableHeadCount;	
	private int tagsConverted;
	enum EmptyTags{HR,BR,IMG,LINK,META};
	
	
	public StringBuffer getMarkdownText() {
		return markdownText;
	}

	public int getTagsConverted() {
		return tagsConverted;
	}

	
	MarkDownGenerator(LinkedList<Token> tokens) {
		this.tokens=tokens;
		tagsConverted=0;
		stack=new Stack<HTMLTag>();
		contentInHead=false;
		markdownText=new StringBuffer("");
	}
	
	private String getMarkdownEquivalent(HTMLTag tag){
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
				if(((HTMLTag)stack.peek()).getElement().equalsIgnoreCase("ul")){
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

	private boolean checkEmptyTag(HTMLTag tag){
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


	
	public void generateMarkdownText(){
		ListIterator<Token> it = tokens.listIterator(0);
		while(it.hasNext()){
			Token tag=it.next();
			if(tag.getType().equalsIgnoreCase("HTMLTag")){
				HTMLTag htmlTag=(HTMLTag)tag;
				tagsConverted++;
				if(!(htmlTag.getElement().equalsIgnoreCase("img")||htmlTag.getElement().equalsIgnoreCase("a")))
					markdownText.append((getMarkdownEquivalent(htmlTag)));
				
				// To check tag balancing in HTML
				if(htmlTag.isOpenTag()){
					if((!checkEmptyTag(htmlTag))&&(!htmlTag.isSelfClosed()))
						stack.push(htmlTag);
				}
				else{
					if(stack.peek().getElement().equalsIgnoreCase(htmlTag.getElement())){
						stack.pop();
					}
					else{
						System.out.println("Tags in HTML file are not balanced");
						System.out.println(stack.peek().getElement()+" != "+htmlTag.getElement());
						break;
					}
				}
				
				//to get mardown equivalent in case of Links
				if(htmlTag.getElement().equalsIgnoreCase("a")&&htmlTag.isOpenTag()){
					String href=htmlTag.getAttribute();
					String textOfLink="#";
					tag=it.next();
					if(tag.getType().equalsIgnoreCase("Text")){
						textOfLink=((TextToken)tag).getTagContent();
						markdownText.append("["+textOfLink+"]("+href+")");
					}
					else{
						tag=it.previous();
					}
				}
				//to get markdown equivalent in case of img
				else if(htmlTag.getElement().equalsIgnoreCase("img")){
					String href=htmlTag.getAttribute();
					markdownText.append("![Image]("+href+")");
				}
			}
			else{
				if(!contentInHead){      //Do not print text inside Head Tag of HTML eg. Title 
					markdownText.append(((TextToken)tag).getTagContent());
				}
			}
		}
		if(!stack.isEmpty()){
			System.out.println("HTML file is not Balanced");
		}
		

	}
}
