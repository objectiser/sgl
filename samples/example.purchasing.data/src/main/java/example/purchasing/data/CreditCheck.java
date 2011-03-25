package example.purchasing.data;

public class CreditCheck implements java.io.Serializable {

	private static final long serialVersionUID = -433085658049851608L;

	private int m_amount=0;
	
	public CreditCheck(int amount) {
		m_amount = amount;
	}
	
	public int getAmount() {
		return(m_amount);
	}
}
