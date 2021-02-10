
public class CasesSpeciales extends Cases {
	
	private String evenement;
	private int transaction;
	
	public CasesSpeciales(String evenement, int transaction) {
		super("Speciale");
		this.evenement = evenement;
		this.transaction = transaction;		
	}
	
}
