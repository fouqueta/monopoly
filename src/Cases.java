public abstract class Cases {
	
	String type;
	
	public Cases(String type) {
		this.type = type;
	}

	public abstract String getNom();
	
	public String getType() { return type; }
}
