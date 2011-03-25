package example.purchasing.data;

public class CreditReport implements java.io.Serializable {

	private static final long serialVersionUID = -433085658049851608L;

	private boolean m_creditOk=false;
	
	public CreditReport(boolean ok) {
		m_creditOk = ok;
	}
	
	public boolean isCreditOk() {
		return(m_creditOk);
	}
}
