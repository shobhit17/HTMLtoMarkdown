import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;



public class Converter implements Runnable{

	private String fileName;
	private int totalTags;
	private int tagsConverted;
	enum EmptyTags{HR,BR,IMG,LINK,META};

	Converter(String fileName){
		this.fileName=fileName;
	}

	
	private StringBuffer readfile(String htmlFile){
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

	private void writeIntoFile(String file,String content){
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

	private StringBuffer convert(StringBuffer fileContents){
		Tokenizer tokenizer=new Tokenizer(fileContents.toString());
		LinkedList<Token> tokens=tokenizer.getTokens();
		System.out.println("Tokenization Completed....");
		totalTags=tokenizer.getTotalTags();
		MarkDownGenerator markDownGenerator=new MarkDownGenerator(tokens);
		tagsConverted=markDownGenerator.getTagsConverted();
		return markDownGenerator.getMarkdownText();
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
	}


}
