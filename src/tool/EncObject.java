package tool;

import java.math.BigInteger;

public class EncObject implements Comparable<EncObject> {
	public int index;
	public BigInteger encrypted;
	
	public EncObject(int index, BigInteger encrypted) {
		this.index = index;
		this.encrypted = encrypted;
	}
	
	@Override
	public int compareTo(EncObject encObject) {
		return Integer.compare(this.index, encObject.index);
	}
}
