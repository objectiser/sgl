package example.purchasing.data;

public class AcceptQuote implements java.io.Serializable {

	private static final long serialVersionUID = 4993358744492060677L;

	public int supplierId=0;
	
	public int getSupplierId() {
		return(supplierId);
	}
	
	public void setSupplierId(int id) {
		supplierId = id;
	}
}
