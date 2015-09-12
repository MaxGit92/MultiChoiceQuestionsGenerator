package projetTAL;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.io.EncodingPrintWriter.out;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;

/** This class demonstrates building and using a Stanford CoreNLP pipeline. */
public class ApplicationTAL {
	
    private static Entry<Integer, CorefChain> findChain(Map<Integer, CorefChain> corefChains, IndexedWord mention, int sentenceIndex)
    {
    	for (Entry<Integer, CorefChain> chaine : corefChains.entrySet())
    		 for (CorefChain.CorefMention m : chaine.getValue().getMentionsInTextualOrder())
    		 {
    			 if (m.sentNum == sentenceIndex && mention.index() >= m.startIndex && mention.index() < m.endIndex)
    				 return chaine;
    		 }
    	return null;
    }
  
  /** Usage: java -cp "*" StanfordCoreNlpDemo [inputFile [outputTextFile [outputXmlFile]]] */
  public static void main(String[] args) throws IOException {
    // set up optional output files
    PrintWriter out;
    if (args.length > 1) {
      out = new PrintWriter(args[1]);
    } else {
      out = new PrintWriter(System.out);
    }
    PrintWriter xmlOut = null;
    if (args.length > 2) {
      xmlOut = new PrintWriter(args[2]);
    }

    // Create a CoreNLP pipeline. This line just builds the default pipeline.
    // In comments we show how you can build a particular pipeline
    // Properties props = new Properties();
    // props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
    // props.put("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    // props.put("ner.applyNumericClassifiers", "false");
    // StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    StanfordCoreNLP pipeline = new StanfordCoreNLP();

    // Initialize an Annotation with some text to be annotated. The text is the argument to the constructor.
    Annotation annotation;
    if (args.length > 0) {
      annotation = new Annotation(IOUtils.slurpFileNoExceptions(args[0]));
    } else {
      annotation = new Annotation("Kosgi Santosh sent an email to Stanford University. He didn't get a reply. His wife, Barbara, was sad. Angered, he decided to send them eggplant pizzas and Barbara. But Maxence slept at home. He will be late tomorrow. I can't play tennis.");
    }

    // run all the selected Annotators on this text
    pipeline.annotate(annotation);
   

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    
    Map<Integer, CorefChain> corefChains = annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
    
    ArrayList<String[]> relations = new ArrayList<String[]>();
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
    					if (name.contains("comp") || name.contains("neg") || name.contains("aux") || name.contains("cop"))
    					{
    						groupeVerbal.add(edge.getDependent());
    						if (name.contains("xcomp"))
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
    			boolean noObject = true;
    			for (IndexedWord currentVerb : groupeVerbal)
    			{
    				for (SemanticGraphEdge edge : graph.getOutEdgesSorted(currentVerb))
    				{
    					String name = edge.getRelation().getShortName();
    					if (name.contains("obj") || name.contains("mod") || name.contains("prep") || name.contains("agent"))
    					{
    						String prep = new String();
    						if (name.contains("prep") && edge.getRelation().getSpecific() != null)
    							prep = edge.getRelation().getSpecific().toString() + " ";
    						IndexedWord object = edge.getDependent();
    		    			Entry<Integer, CorefChain> chaine_obj = findChain(corefChains, object, sentences.indexOf(sentence)+1);
    		    			if (chaine_obj != null){
    		    				r[2] = prep + chaine_obj.getValue().getRepresentativeMention().toString().split("\"", 3)[1];
    		    			}
    		    			else {
    		    				r[2] = prep + object.originalText();
    		    			}
    		    			noObject = false;
        					relations.add(r.clone());
    					}
    				}
    			}
    			if (noObject)
    			{
    				relations.add(r.clone());    				
    			}
    		}
    	}
    }
    for (String[] r : relations)
    {
    	out.println(":" + r[0] + "\t:" + r[1] + "\t:" + r[2]);
    }
				
    			/* 
    			 * Parcourir toutes les relations du verbe
    			 *  Si aux ou xcomp ou neg -> ajouter a la liste du verbe
    			 * Tant qu'on ajoute à verbs, reparcourir
    			 * 
    			 * Reparcourir toutes les relations iobj, dobj, prep_, mod
    			 */
    IOUtils.closeIgnoringExceptions(out);
    IOUtils.closeIgnoringExceptions(xmlOut);
  }
}

