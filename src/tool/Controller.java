package tool;

import java.math.BigInteger;

public class Controller {
	
	private Key key;
	
	public BigInteger encrypt(byte[] data) throws Exception {
		// データ
		BigInteger intMsg = new BigInteger(data);
		
		// 暗号化
		return intMsg.modPow(key.e, key.n);
	}
	
	public byte[] decrypt(BigInteger intEnc) throws Exception {
		// 復号
		BigInteger intDec = intEnc.modPow(key.d, key.n);
		return intDec.toByteArray();
	}
	
	public Controller(Key key) {
		this.key = key;
	}
}
