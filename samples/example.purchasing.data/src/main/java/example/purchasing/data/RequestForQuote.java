package example.purchasing.data;

public class RequestForQuote implements java.io.Serializable {

	private static final long serialVersionUID = 718298130323948999L;

	public String productCode=null;
	
	public String getProductCode() {
		return(productCode);
	}
	
	public void setProductCode(String code) {
		productCode = code;
	}
}
