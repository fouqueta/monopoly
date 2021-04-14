package monopoly;

public abstract class Cases {
	
	String type;
	String nom;
	int position;
	
	public Cases(int position, String type, String nom) {
		this.position=position;
		this.type = type;
		this.nom = nom;
		
	}

	//Getters
	public String getNom() { return nom; }
	
	public String getType() { return type; }
	
	public int getPosition() { return position; }
	
}
