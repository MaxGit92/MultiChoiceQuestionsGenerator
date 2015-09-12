package projetTAL;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.*;

public class QCM {
	private ArrayList<Question> qcm;
	private TripletsRDF rdf;
	private int nbQuestions;
	private int nbChoix;
	private String titre;
	
	public QCM(String text, String titre, int nbQuestions, int nbChoix) throws FileNotFoundException{
		this.qcm = new ArrayList<Question>();
		this.titre = titre;
		this.rdf = new TripletsRDF(text);
		this.rdf.createTtl(false);
		this.nbQuestions = nbQuestions;
		this.nbChoix = nbChoix;
		for(int i = 0; i < this.nbQuestions; i++){
			this.qcm.add(new Question((int)(Math.random()*5), this.rdf, (int)(Math.random()*this.rdf.getSize()), this.nbChoix));
		}
	}
        
        public QCM(String text, String titre, int nbQuestions, int nbChoix, StanfordCoreNLP pipeline) throws FileNotFoundException{
		this.qcm = new ArrayList<Question>();
		this.titre = titre;
		this.rdf = new TripletsRDF(text, pipeline);
		this.rdf.createTtl(false);
		this.nbQuestions = nbQuestions;
		this.nbChoix = nbChoix;
		for(int i = 0; i < this.nbQuestions; i++){
			int alea = (int)(Math.random()*4);
			this.qcm.add(new Question(alea, this.rdf, (int)(Math.random()*this.rdf.getSize()), this.nbChoix));
		}
	}
        
        public int getNbQuestion()
        {
            return this.nbQuestions;
        }
        
        public String getTitre()
        {
            return this.titre;
        }
        
        public Question getQuestion(int i)
        {
            return qcm.get(i);
        }

	public int getScore(ArrayList<ArrayList<Boolean>> propositions) throws IOException
	{
		int score = 0;
		for (int i = 0; i < nbQuestions; i++)
		{
			score += this.qcm.get(i).getScore(propositions.get(i));
		}
		writeScore(score, this.titre);
		return score;
	}
	
	public void writeScore(int score, String titre) throws IOException
	{
		File file = new File("score.csv");
		Boolean notFirst = file.exists();
				
		PrintWriter out = new PrintWriter(new FileWriter("score.csv", true));
		
		if(!notFirst)
			out.println("Nom du fichier, Nombre de Questions, Nombre de réponses, Score");
		titre = titre.replace("\\", "/");
		String[] t = titre.split("/");
		out.println(""+t[t.length-1].replace(".", "/").split("/")[0]+", "+this.nbQuestions+", "+ this.nbChoix+", "+score);
		out.close();
	}

	public String toString() {
		String toRet = new String();
		for (Question q : qcm)
		{
			toRet += q.toString() + "\n";
		}
		return toRet;
	}
        
        public String toText() {
		String toRet = new String();
		for (Question q : qcm)
		{
			toRet += q.getText() + "\n";
		}
		return toRet;
	}

}
