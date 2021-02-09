
public class CasesSpeciales extends Cases {
	
	String evenement;
	int transaction;
	
	public CasesSpeciales(String type, String evenement, int transaction) {
		super("Speciale");
		this.evenement = evenement;
		this.transaction = transaction;		
	}
	
}
