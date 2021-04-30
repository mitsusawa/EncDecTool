package tool;

import java.math.BigInteger;

public class Key {
	public BigInteger n;
	public BigInteger e;
	public BigInteger d;
	
	public Key(BigInteger n, BigInteger e, BigInteger d) {
		this.n = n;
		this.e = e;
		this.d = d;
	}
}
