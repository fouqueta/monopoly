package monopoly;

public class CasesSpeciales extends Cases {
	
	private int transaction;
	
	public CasesSpeciales(int position, String nom, int transaction) {
		super(position, "Speciale", nom);
		this.transaction = transaction;		
	}

	//Getters
	public int getTransaction() { return transaction; }

}
