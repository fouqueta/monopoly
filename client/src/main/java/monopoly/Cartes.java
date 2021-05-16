package monopoly;

public class Cartes {
	private String type;
	private String contenu;
	private String typeAction;
	private int parametres;
	
	public Cartes(String type, String contenu, String typeAction, int parametres) {
		this.type = type;
		this.contenu = contenu;
		this.typeAction = typeAction;
		this.parametres = parametres;
	}

	public String getContenu() { return contenu; }
	
	public String getType() { return type; }

	public String getTypeAction() {	return typeAction; }

	public int getParametres() { return parametres; }
	
	public void setContenu(String contenu) { this.contenu = contenu; }
	
	public void setTypeAction(String typeAction) { this.typeAction = typeAction; }

	public void setParametres(int parametres) {	this.parametres = parametres; }
	
}
