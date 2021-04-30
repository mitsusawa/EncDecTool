package tool;

import java.math.BigInteger;

public class DecObject implements Comparable<DecObject> {
	public int index;
	public byte[] decrypted;
	
	public DecObject(int index, byte[] decrypted) {
		this.index = index;
		this.decrypted = decrypted;
	}
	
	@Override
	public int compareTo(DecObject decObject) {
		return Integer.compare(this.index, decObject.index);
	}
}
