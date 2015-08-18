package usbong.android.questionloader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class QuestionParser 
{
	
	public List<Question> parse(InputStream is, int difficulty)
	{
		List<Question> questionList = new ArrayList<Question>();
        try
        {
        	BufferedReader br = new BufferedReader(new InputStreamReader(is));
        	String currentLine;
        	Question currentQuestion = null;
        	while((currentLine=br.readLine())!=null)
        	{
//        		System.out.println("currentLine: "+currentLine);
//        		System.out.println("currentLine.charAt(0): "+currentLine.charAt(0));
//        		System.out.println("Character.isDigit(currentLine.charAt(0)): "+Character.isDigit(currentLine.charAt(0)));        		        		
        		currentLine = currentLine.trim();
        	
        		System.out.println(">>>>>currentLine: "+currentLine);
        		//comment by Mike, 31 March 2015
        		//make sure that there are three new lines after the last line of the song
        		if (currentLine.equals("") && currentQuestion!=null)
        		{
        			questionList.add(currentQuestion);
        			currentQuestion = null;
        		}        		
        		//I had to add "||Character.isDigit(currentLine.charAt(1)" because it seems that perhaps due to character encoding, charAt(0) produces a dot (top of :) and not a digit
        		else if (currentLine.length()>=1){
        			if ((Character.isDigit(currentLine.charAt(0)))||(currentLine.length()>1 && Character.isDigit(currentLine.charAt(1))))
        			{
	//        			System.out.println("inside: Character.isDigit(currentLine.charAt(0))");       			
	        			// digit, new question
	        			currentQuestion = new Question();
	        			int dotIndex = currentLine.indexOf('.');
	        			String questionString = currentLine.substring(dotIndex+1).trim();
	        			System.out.println(">>>questionString: "+questionString);
	        			currentQuestion.setQuestionText(questionString);
	        			currentQuestion.setDifficulty(difficulty);
        			}
	        		else if (currentLine.charAt(0)=='*')
	        		{
	        			// add answer
	        				// remove letter 
	        			int dotIndex = currentLine.indexOf('*');
	//        			System.out.println(">>>> currentLine: "+currentLine);
	//        			System.out.println(">>>> currentLine.substring(dotIndex+1): "+currentLine.substring(dotIndex+1).trim());        			
	        			String answerString = currentLine.substring(dotIndex+1).trim();       			
	        			
	        			currentQuestion.addAnswer(answerString); 
	        		}
	        		else if (currentLine.charAt(0)=='+')
	        		{
	        			// add answer
	        				// remove letter 
	        			int dotIndex = currentLine.indexOf('+');
	//        			System.out.println(">>>> currentLine: "+currentLine);
	//        			System.out.println(">>>> currentLine.substring(dotIndex+1): "+currentLine.substring(dotIndex+1).trim());        			
	        			String videoString = currentLine.substring(dotIndex+1).trim();       			
	        			
	        			currentQuestion.setVideo(videoString); 
	        		}
	        		else if (currentLine.charAt(0)=='$')
	        		{
	        			// add answer
	        				// remove letter 
	        			try{
	        			int dotIndex = currentLine.indexOf('$');
	//        			System.out.println(">>>> currentLine: "+currentLine);
	//        			System.out.println(">>>> currentLine.substring(dotIndex+1): "+currentLine.substring(dotIndex+1).trim());        			
	        			String spotifyString = currentLine.substring(dotIndex+1).trim();       				        			
	        			currentQuestion.setLink(spotifyString); }
	        			catch (Exception e)
	        			{
	        				
	        			}
	        		}
        		}
        	}
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        System.out.println(questionList);
        System.out.println(questionList.size());        
        
        return questionList;
	}
}
