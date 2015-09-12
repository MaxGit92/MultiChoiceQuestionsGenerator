package projetTAL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.*;

/** This class demonstrates building and using a Stanford CoreNLP pipeline. */
public class TripletsRDF {
	private Annotation annotation;
	private ArrayList<String[]> triplets;
	private StanfordCoreNLP pipeline;
	private ArrayList<String> relations;
	
        public TripletsRDF(String texte, StanfordCoreNLP pipeline)
	{
		// Create a CoreNLP pipeline. This line just builds the default pipeline.
	    // In comments we show how you can build a particular pipeline
	    // Properties props = new Properties();
	    // props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
	    // props.put("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
	    // props.put("ner.applyNumericClassifiers", "false");
	    // StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		this.pipeline = pipeline;
		this.annotation = new Annotation(texte);
		this.triplets = new ArrayList<String[]>(); 
		this.relations = new ArrayList<String>();
	}
        
	public TripletsRDF(String texte)
	{
		// Create a CoreNLP pipeline. This line just builds the default pipeline.
	    // In comments we show how you can build a particular pipeline
	    // Properties props = new Properties();
	    // props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
	    // props.put("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
	    // props.put("ner.applyNumericClassifiers", "false");
	    // StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		this.pipeline = new StanfordCoreNLP();
		this.annotation = new Annotation(texte);
		this.triplets = new ArrayList<String[]>(); 
		this.relations = new ArrayList<String>();
	}
		
	public void setTexte(String texte)
	{
		this.annotation = new Annotation(texte);
	}
	
	public String[] getTriplet(int i)
	{
		return this.triplets.get(i);
	}
	
	public String getRelationName(int i)
	{
		return this.relations.get(i);
	}
	
	public int getSize()
	{
		return triplets.size();
	}

	public void writeFile(String filename) throws IOException
	{
		PrintWriter out = new PrintWriter(filename);
		out.print(this.toString());
		IOUtils.closeIgnoringExceptions(out);
	}

	public void writeFile(PrintWriter out) throws IOException
	{
		out.print(this.toString());
	}
	
	private void addTriplet(String[] r, String relation)
	{
		this.triplets.add(r);
		this.relations.add(relation);
	}
	
	public void createTtl() throws FileNotFoundException
	{
		this.createTtl(false);
	}
	
	public void createTtl(boolean verbOnly) throws FileNotFoundException
	{
		this.pipeline.annotate(this.annotation);
		List<CoreMap> sentences = this.annotation.get(CoreAnnotations.SentencesAnnotation.class);
	    Map<Integer, CorefChain> corefChains = this.annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
	    
	    for (CoreMap sentence : sentences) {
	    	SemanticGraph graph = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
	    	for (SemanticGraphEdge relation : graph.edgeIterable()){
	    		String relname = relation.getRelation().getShortName(); 
	    		if (relname.equals("nsubj") || relname.equals("nsubjpass") ){
	    			String[] r = new String[3];
	    			
	    			/* Sujet de la relation */
	    			IndexedWord subject = relation.getDependent();
	    			Entry<Integer, CorefChain> chaine = findChain(corefChains, subject, sentences.indexOf(sentence)+1);
	    			if (chaine != null){
	    				r[0] = chaine.getValue().getRepresentativeMention().toString().split("\"", 3)[1];
	    			}
	    			else {
	    				r[0] = subject.originalText();
	    			}
	    			
	    			if (verbOnly && ! relation.getGovernor().tag().contains("VB")){
	    				continue;
	    			}
	    			
	    			ArrayList<IndexedWord> verbesATraiter = new ArrayList<IndexedWord>();
	    			verbesATraiter.add(relation.getGovernor());
	    			ArrayList<IndexedWord> groupeVerbal = new ArrayList<IndexedWord>();
	    			groupeVerbal.add(relation.getGovernor());
	    		
	    			while (!verbesATraiter.isEmpty())
	    			{
	    				IndexedWord currentVerb = verbesATraiter.remove(0);
	    				for (SemanticGraphEdge edge : graph.getOutEdgesSorted(currentVerb))
	    				{
	    					String name = edge.getRelation().getShortName();
	    					//if (name.contains("comp") || name.contains("neg") || name.contains("aux") || name.contains("cop") || name.contains("mod") || name.contains("det"))
	    					if (name.contains("comp") || name.contains("neg") || name.contains("aux") || name.contains("cop"))
	    					{
	    						groupeVerbal.add(edge.getDependent());
	    						if (name.contains("xcomp") || name.contains("mod"))
	    						{
	    							verbesATraiter.add(edge.getDependent());
	    						}
	    					}
	    				}
	    			}
	    			
	    			ArrayList<IndexedWord> sortedGV = new ArrayList<>();
	    			for (IndexedWord v : groupeVerbal)
	    			{
	    				int i;
	    				for (i = 0; (i < sortedGV.size() && sortedGV.get(i).beginPosition() < v.beginPosition()); i++);
	    				sortedGV.add(i,v);
	    			}
	    			r[1] = new String();
	    			for (IndexedWord v : sortedGV)
	    			{
	    				r[1] += v.originalText() + " ";
	    			}
	    			r[1] = r[1].trim();
	    			boolean noObject = true;
	    			for (IndexedWord currentVerb : groupeVerbal)
	    			{
	    				for (SemanticGraphEdge edge : graph.getOutEdgesSorted(currentVerb))
	    				{
	    					String name = edge.getRelation().getShortName();
	    					//if (name.contains("obj") || name.contains("mod") || name.contains("prep") || name.contains("agent"))
		    				if (name.contains("obj") || name.contains("prep") || name.contains("agent"))	    						
	    					{
	    						String prep = new String();
	    						if (name.contains("prep") && edge.getRelation().getSpecific() != null)
	    							prep = edge.getRelation().getSpecific().toString() + " ";
	    						IndexedWord object = edge.getDependent();
	    		    			Entry<Integer, CorefChain> chaine_obj = findChain(corefChains, object, sentences.indexOf(sentence)+1);
	    		    			if (chaine_obj != null){
	    		    				r[2] = (prep + chaine_obj.getValue().getRepresentativeMention().toString().split("\"", 3)[1]).trim();
	    		    			}
	    		    			else {
	    		    				r[2] = (prep + object.originalText()).trim();
	    		    			}
	    		    			noObject = false;
	        					addTriplet(r.clone(), prep.trim());
	    					}
	    				}
	    			}
	    			if (noObject)
	    			{
                                        r[2] = new String();
	    				addTriplet(r.clone(), null);		
	    			}
	    		}
	    	}
	    }
	    writeFileTTL();
	}
	
    private Entry<Integer, CorefChain> findChain(Map<Integer, CorefChain> corefChains, IndexedWord mention, int sentenceIndex)
    {
    	for (Entry<Integer, CorefChain> chaine : corefChains.entrySet())
    		 for (CorefChain.CorefMention m : chaine.getValue().getMentionsInTextualOrder())
    		 {
    			 if (m.sentNum == sentenceIndex && mention.index() >= m.startIndex && mention.index() < m.endIndex)
    				 return chaine;
    		 }
    	return null;
    }
    
    public String toCompleteString()
    {
    	int i = 0;
    	String toRet = new String();
    	
    	for (String[] r : this.triplets)
	    {
	    	toRet += ":" + r[0] + "\t:" + r[1] + "\t:" + r[2] + "\t[" + this.relations.get(i++) + "]\n";
	    }
    	return toRet;
    }
    
    public void writeFileTTL() throws FileNotFoundException
    {
    	PrintWriter out = new PrintWriter(new File("out.ttl"));
    	
    	for (String[] r : this.triplets)
	    {
	    	out.println(":" + r[0] + "\t:" + r[1] + "\t:" + r[2]);
	    }
    	out.close();
    }
    
    @Override
    public String toString()
    {
    	String toRet = new String(); 
    	for (String[] r : this.triplets)
	    {
	    	toRet += ":" + r[0] + "\t:" + r[1] + "\t:" + r[2] + "\n";
	    }
    	return toRet;
    }
}

