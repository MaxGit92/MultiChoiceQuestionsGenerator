package projetTAL;

import java.util.ArrayList;
import java.util.Collections;

public class Question {

	private String question;
	private ArrayList<Reponse> reponses;
	private int type;
	private TripletsRDF rdf;
	private int numLigne;

	//type 0 ?sujet :verbe :complement
	//type 1 ?sujet :verbe :-
	//type 2 :sujet  :verbe ?complement
	//type 3 :sujet  ?verbe :complement
	//type 4 :sujet  ?verbe
	public Question(int type, TripletsRDF rdf, int numLigne, int nbReponses) {
		this.type = type;
		this.rdf = rdf;
		this.numLigne = numLigne;
		makeQuestion();
		makeReponses(nbReponses);
	}
	
	private void makeQuestion(){
		String[] triplet = this.rdf.getTriplet(this.numLigne);
		while (this.type == 2 && triplet[2] == null)
		{
			this.type = (int)(Math.random()*5);  
		}
		if(this.type == 0){
			if (triplet[2] != null)
			{
				this.question = new String("Who " + triplet[1] + " " + triplet[2] + "?");
			}
			else
			{
				this.type = 1;
			}
		}
		if(this.type == 1){
			this.question = new String("Who " + triplet[1] + "?");
		}
		if (this.type == 2){
			this.question = new String(triplet[0] + " " + triplet[1] + " -------- ?");
		}
		if (this.type == 3){
			if (triplet[2] != null)
			{
				this.question = new String(triplet[0] + " -------- " + triplet[2] + "?");
			}
			else
			{
				this.type = 4;
			}
		}
		if (this.type == 4){
			this.question = new String(triplet[0] + " -------- " + "?");
		}
	}
	
	private void makeReponses(int nbReponses){
		reponses = new ArrayList<Reponse>();
		String[] triplet = this.rdf.getTriplet(this.numLigne);
		if(this.type == 0){
			for (int i = 0; i < this.rdf.getSize(); i++)
			{
				String[] candidat = this.rdf.getTriplet(i);
				if (candidat[1].equals(triplet[1]) && ((candidat[2] == triplet[2]) || candidat[2].equals(triplet[2])))
				{
					Reponse r = new Reponse(candidat[0], true);
					if (! this.reponses.contains(r))
					{
						this.reponses.add(r);
					}
				}
				if (this.reponses.size() == nbReponses)
				{
					break;
				}
			}
			while (this.reponses.size() < nbReponses)
			{
				int i = (int)(Math.random() * this.rdf.getSize());
				Reponse r = new Reponse(this.rdf.getTriplet(i)[0], false);
				if (!this.reponses.contains(r) && !this.rdf.getTriplet(i)[2].equals(""))
					this.reponses.add(r);
			}
		}
		
		if(this.type == 1){
			for (int i = 0; i < this.rdf.getSize(); i++)
			{
				String[] candidat = this.rdf.getTriplet(i);
				if (candidat[1].equals(triplet[1]))
				{
					Reponse r = new Reponse(candidat[0], true);
					if (! this.reponses.contains(r))
					{
						this.reponses.add(r);
					}
				}
				if (this.reponses.size() == nbReponses)
				{
					break;
				}
			}
			while (this.reponses.size() < nbReponses)
			{
				int i = (int)(Math.random() * this.rdf.getSize());
				Reponse r = new Reponse(this.rdf.getTriplet(i)[0], false);
				if (! (this.reponses.contains(r) && this.reponses.contains(r)))
					this.reponses.add(r);
			}
		}
			
		if(this.type == 2){
			for (int i = 0; i < this.rdf.getSize(); i++)
			{
				String[] candidat = this.rdf.getTriplet(i);
				if (candidat[0].equals(triplet[0]) && candidat[1].equals(triplet[1]))
				{
					Reponse r = new Reponse(candidat[2], true);
					if (! this.reponses.contains(r))
					{
						this.reponses.add(r);
					}
				}
				if (this.reponses.size() == nbReponses)
				{
					break;
				}
			}
			while (this.reponses.size() < nbReponses)
			{
				int i = (int)(Math.random() * this.rdf.getSize());
				Reponse r = new Reponse(this.rdf.getTriplet(i)[2], false);
				System.out.println(r.toString());
				System.out.println(reponses.toString());
				if (! (this.reponses.contains(r)))
					this.reponses.add(r);
			}
		}
		
		if(this.type == 3){
			for (int i = 0; i < this.rdf.getSize(); i++)
			{
				String[] candidat = this.rdf.getTriplet(i);
				if (candidat[0].equals(triplet[0]) && candidat[2].equals(triplet[2]))
				{
					Reponse r = new Reponse(candidat[1], true);
					if (! this.reponses.contains(r))
					{
						this.reponses.add(r);
					}
				}
				if (this.reponses.size() == nbReponses)
				{
					break;
				}
			}
			while (this.reponses.size() < nbReponses)
			{
				int i = (int)(Math.random() * this.rdf.getSize());
				Reponse r = new Reponse(this.rdf.getTriplet(i)[1], false);
				if (! (this.reponses.contains(r) && this.reponses.contains(r)))
					this.reponses.add(r);
			}
		}
		
		if(this.type == 4){
			for (int i = 0; i < this.rdf.getSize(); i++)
			{
				String[] candidat = this.rdf.getTriplet(i);
				if (candidat[0].equals(triplet[0]))
				{
					Reponse r = new Reponse(candidat[1], true);
					if (! this.reponses.contains(r))
					{
						this.reponses.add(r);
					}
				}
				if (this.reponses.size() == nbReponses)
				{
					break;
				}
			}
			while (this.reponses.size() < nbReponses)
			{
				int i = (int)(Math.random() * this.rdf.getSize());
				Reponse r = new Reponse(this.rdf.getTriplet(i)[1], false);
				if (! (this.reponses.contains(r) && this.reponses.contains(r)))
					this.reponses.add(r);
			}
		}
		
		Collections.shuffle(this.reponses);
	}

	public String getQuestion() {
		return question;
	}

	public ArrayList<Reponse> getReponses() {
		return reponses;
	}

	@Override
	public String toString() {
		String toRet = new String("Question: " + this.question + "\n");
		for (Reponse r : this.reponses)
		{
			if (r.getValue() == true)
			{
				toRet += "[x] ";
			}
			else
			{
				toRet += "[ ] ";
			}
			toRet += r.getTexte() + "\n";
		}
		return toRet;
	}
	
	public String getText() {
		String toRet = new String("Question: " + this.question + "\n");
		for (Reponse r : this.reponses)
		{
			toRet += "[ ] " + r.getTexte() + "\n";
		}
		return toRet;
	}
	
	public int getScore(ArrayList<Boolean> propositions)
	{
		int score = 1;
		for (int i = 0; i < this.reponses.size(); i++)
		{
			if (propositions.get(i) != this.reponses.get(i).getValue())
			{
				return 0;
			}
		}
		return score;
	}
}
