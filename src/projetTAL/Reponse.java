package projetTAL;

public class Reponse {
	private String texte;
	private Boolean value;
	
	public Reponse(String texte, Boolean value)
	{
		this.texte = texte;
		this.value = value;
	}
	
	String getTexte()
	{
		return this.texte;
	}
	
	Boolean getValue()
	{
		return this.value;
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj.getClass() == this.getClass())
		{
			Reponse o = (Reponse)obj;
			return this.texte.equals(o.getTexte());
		}
		return false;
	}

	@Override
	public String toString() {
		return "Reponse [texte=" + texte + ", value=" + value + "]";
	}

	
	
}
