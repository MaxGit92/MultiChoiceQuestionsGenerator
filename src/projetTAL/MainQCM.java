package projetTAL;

import java.io.FileNotFoundException;

import edu.stanford.nlp.io.IOUtils;

public class MainQCM {

	public static void main(String[] args) throws FileNotFoundException {
	    String texte;
	    QCM qcm;
	    String titre = "pas de titre";
	    if (args.length > 0) {
	      texte = new String(IOUtils.slurpFileNoExceptions(args[0]));
	      titre = args[0];
	    } else {
	      texte = new String("David hit himself. Mickael hit David. The cat destroyed my notebook. It is bored about their project."); //Paris is the most beautiful city in the Universe. Maxence is cute. He is strong too. His cat is cute too. Mickael is working with him. He is bored about their project. Kosgi Santosh sent an email to Stanford University. He didn't get a reply. His wife, Barbara, was sad. Angered, he decided to send them eggplant pizzas and Barbara. But Maxence slept at home. He will be late tomorrow. I can't play tennis. In 1945, the U.S. destroyed the city of Hiroshima.");
	    }
		qcm = new QCM(texte, titre, 10, 2);
		System.out.print(qcm.toString());
	}
}
