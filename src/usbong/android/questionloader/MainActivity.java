package usbong.android.questionloader;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import usbong.android.utils.UsbongUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	TextView question;
	TextView answer;
	TextView result;
	EditText input_ans;
	int questionCounter = 0;
	QuestionManager qm;
	Question newQues;
	Button button1;
	Button button2;
	String user_answer;
	String questionDifficulty;
	String difficulty;
	String songname;
	double score;
	double total;
	private ProgressBar mProgress;
	double progress;
	ArrayList<Integer> indices = new ArrayList<Integer>();
	String language;

	double accuracy; //added by Mike, 27 March 2015
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        	Bundle bundle = getIntent().getExtras();
        	difficulty = bundle.getString("difficulty");
        	songname = bundle.getString("song_title");
        	language = bundle.getString("language");
    		question   = (TextView)findViewById(R.id.questionView);
    		result   = (TextView)findViewById(R.id.resultView);
    		answer   = (TextView)findViewById(R.id.answerView);
    		input_ans   = (EditText)findViewById(R.id.editText1);
    		button1= (Button)findViewById(R.id.enterButton);
    		button2= (Button)findViewById(R.id.nextButton);
    		mProgress = (ProgressBar) findViewById(R.id.progressBar1);
    		mProgress.setMax(100);
    		button2.setVisibility(View.INVISIBLE);
    		
        try
        {
        	
        	InputStream isE = getResources().getAssets().open(language+"/" + songname);        	
//        	Log.d(">>>language : songname", language+" : "+songname);
        	qm = new QuestionManager();
        	qm.loadQuestions(isE, Question.DIFFICULTY_EASY);
        	newQues = qm.getQuestion(Question.DIFFICULTY_EASY,questionCounter);
        	        	
        	String string = newQues.getQuestionText();
        	String[] parts = string.split("-");
        	if (difficulty.equalsIgnoreCase("easy"))
        		questionDifficulty = parts[0];
        	else
        		questionDifficulty = parts[1];	
        	total = qm.getCount();//-1;//do a -1 because questionCounter starts at 0; added by Mike, 31 March 2015
        	System.out.println(">>>>TOTAL: "+total);

        	//System.out.println("The question is " + questionDifficulty);
        	question.setText(questionDifficulty);
//        	result.setText(""); //commented out by Mike, 27 March 2015
        	//answer.setText("Correct answer: "+ newQues.getCorrectAnswer());
        	answer.setText("");
        	//progress counter
        	//System.out.println(questionCounter +" " + total);
        	progress = questionCounter/total;
            mProgress.setProgress((int) (progress*100));
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
       
        
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.about_and_feedback_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{		
		switch(item.getItemId())
		{
			case(R.id.about):
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("About Singakamas");
				builder.setMessage(UsbongUtils.readTextFileInAssetsFolder(MainActivity.this,"credits.txt")); //don't add a '/', otherwise the file would not be found
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				       public void onClick(DialogInterface dialog, int id) {
				            dialog.cancel();
				       }
				   });
				AlertDialog alert = builder.create();
				alert.show();
				return true;
				
			case(R.id.feedback):
				//http://stackoverflow.com/questions/8701634/send-email-intent;
				//last accessed: 1 April 2015, answer by Doraemon
				//send to cloud-based service
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO, Uri.fromParts(
						"mailto","usbong.ph@gmail.com",null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Singkamas: Feedback (Android)");
				emailIntent.putExtra(Intent.EXTRA_TEXT  , UsbongUtils.defaultFeedbackMessage);
				startActivity(Intent.createChooser(emailIntent, "Sending feedback..."));
				return true;
			default:
				return super.onOptionsItemSelected(item);

		}
	}
    
    public void nextQuestion(View view)
    {
    	
    	button2.setVisibility(View.INVISIBLE);
//    	result.setText(""); //commented out by Mike, 27 March 2015
    	answer.setText("");
    	input_ans.setText("");
    	button1.setVisibility(View.INVISIBLE);
    	try
    	{
    		newQues = qm.getQuestion(Question.DIFFICULTY_EASY,questionCounter);
    		String string = newQues.getQuestionText();
        	String[] parts = string.split("-");
        	if (difficulty.equalsIgnoreCase("easy"))
        		questionDifficulty = parts[0];
        	else
        		questionDifficulty = parts[1];	
        	//question.setText("Hello");
        	//answer.setText("World");
        	question.setText(questionDifficulty);
    		//question.setText(newQues.getQuestionText());
        	progress = questionCounter/total;
            mProgress.setProgress((int) (progress*100));
            indices.clear();
            button1.setVisibility(View.VISIBLE);
        	
        	
    	}
    	catch(Exception e)
    	{
    		double totalScore = score;//Math.round(score/total); //edited by Mike, 11 April 2015
    		Intent i = new Intent(getApplicationContext(), ResultPage.class);
    		i.putExtra("score", totalScore);
    		i.putExtra("language", language);
    		startActivity(i);
    		MainActivity.this.finish();
    		//Switch to scoreboard
    	}
    	
    }

    @SuppressLint("NewApi")
	public void exitMainActivity(View view)
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Exiting...");
		builder.setMessage("Are you sure you want to return to song selection?")

		   .setCancelable(false)
		   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		       public void onClick(DialogInterface dialog, int id) {
			       	Intent intent = new Intent(MainActivity.this, SongSelection.class);
			    	intent.putExtra("language", language);
			    	startActivity(intent);
			    	MainActivity.this.finish();
		       }
		   })
		   .setNegativeButton("No", new DialogInterface.OnClickListener() {
		       public void onClick(DialogInterface dialog, int id) {
		            dialog.cancel();
		       }
		   });
		AlertDialog alert = builder.create();
		alert.show();
    }
    
    @SuppressLint("NewApi")
	public void enterAnswer(View view)
    {
    	
    	user_answer = input_ans.getText().toString();
    	//edited by Mike, 27 March 2015
    	double temp_accuracy = compareAnswer(user_answer.replaceAll("\\s+",""),newQues.getCorrectAnswer().replaceAll("\\s+",""));
    	score += Math.round(temp_accuracy);
    	//make bold
    	String newBold = makeBold(indices, newQues.getCorrectAnswer());
    	//aggregate accuracy scores
//    	accuracy = (accuracy+temp_accuracy)/(questionCounter+1);
//    	result.setText("Accuracy: " + Math.round(accuracy) + "%");
    	double temp_score = score/(questionCounter+1); //added by Mike, 31 March 2015    	
    	//Reference: http://stackoverflow.com/questions/7747469/how-can-i-truncate-a-double-to-only-two-decimal-places-in-java
    	//; answer by Bozho, last accessed: 31 March 2015
    	String a = new DecimalFormat("#.##").format(temp_score); //added by Mike, 31 March 2015
    	result.setText("Accuracy: " + a + "%");

    	score = temp_score;    	
    	
    	System.out.println("accuracy: "+accuracy);
    	System.out.println("questionCounter: "+questionCounter);
    	
    	if (temp_accuracy<100) 
    	{
    		answer.setText(Html.fromHtml("Correct answer: " + newBold));
    		
    	}
    	else {
    		answer.setText("Correct!");
    	}
    	questionCounter++;
    	try
    	{
    		newQues = qm.getQuestion(Question.DIFFICULTY_EASY,questionCounter);
    	}
    	catch(Exception e)
    	{
    		//button2.setText("End");
    		Drawable drawableX = this.getResources().getDrawable(R.drawable.end_selector);
    		button2.setBackground(drawableX);
    		
    		//added by Mike, 11 April 2015
    		progress=total;
            mProgress.setProgress((int) (progress*100));
    	}
    	button2.setVisibility(View.VISIBLE);
    	button1.setVisibility(View.INVISIBLE);
    	
    	
    }
    
    public double compareAnswer(String a, String b)
    {
    	char[] first  = a.toLowerCase().toCharArray();
    	char[] second = b.toLowerCase().toCharArray();
    	double counter = 0;
    	double minLength = Math.min(first.length, second.length);
    	double maxLength = Math.max(first.length, second.length); //added by Mike, 31 March 2015    	

    	for(int i = 0; i < minLength; i++)
    	{
    	        if (first[i] != second[i])
    	        {
    	        	indices.add(i);
//    	        	System.out.println("Here");
    	            counter++;    //this is the number of different characters
    	        }
    	        
    	}
    	//add missing characters to mistakes
    	if (first.length < second.length)
    	{
    		for (int i = first.length; i < second.length; i++)
    		{
    			indices.add(i);
    			
    		}
    	}
    	
    	return 100*((minLength-counter)/maxLength); //edited by Mike, 31 March 2015
    }
    
    public String makeBold(ArrayList<Integer> index, String s)
    {
    	
		String withspace = s;
		
		
		ArrayList<Integer> spaces = new ArrayList<Integer>();
		for (int i = 0; i < withspace.length(); i++)
		{
			if (withspace.charAt(i) == ' ')
			{
				spaces.add(i);
			}
		}
		
		for (int i = 0; i < spaces.size(); i++)
		{
			for (int j = 0; j < index.size(); j++)
			{
				if (index.get(j) >= spaces.get(i))
				{
					int temp = index.get(j)+1;
					index.set(j, temp);
				}
				
			}
		}
		
		StringBuilder sb = new StringBuilder(withspace);
		int last = index.size()-1;
		for (int i = withspace.length(); i >=0; i--)
		{
			if (last >=0 && i == index.get(last))
			{
			
				sb.insert(i+1, "</b></font>");
				sb.insert(i, "<font color='red'><b>");
				last--;
			}
			
		}
		
		
		return sb.toString();
    	
    }        
}
