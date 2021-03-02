public abstract class Cases {
	
	String type;
	String nom;
	
	public Cases(String type, String nom) {
		this.type = type;
		this.nom = nom;
	}

	//Getters
	public String getNom() { return nom; }
	
	public String getType() { return type; }
}
