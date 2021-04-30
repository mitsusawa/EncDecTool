package tool;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class KeyGen {
	// パラメータ
	private final int BIT_LENGTH;
	
	public Key gen() throws Exception {
		// 素数の生成
		Random rand = new SecureRandom();   // 乱数
		BigInteger p = BigInteger.probablePrime(this.BIT_LENGTH / 2, rand);  // bitLengthを持つ、おそらく素数である正のBigIntegerを返す
		BigInteger q = BigInteger.probablePrime(this.BIT_LENGTH / 2, rand);  // bitLengthを持つ、おそらく素数である正のBigIntegerを返す
		// System.out.println("p="+p);
		// System.out.println("q="+q);
		
		// Calculate products
		BigInteger n = p.multiply(q);   // n=p×q
		BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));   // φ=(p-1)×(q-1)
		// System.out.println("n="+n);
		// System.out.println("φ="+phi);
		
		// eの生成
		BigInteger e;
		do {
			e = new BigInteger(phi.bitLength(), rand);  // このコンストラクタの書き方でランダムに生成された BigIntegerを構築する
			// eが1か、φか、eとφの最大公約数が1ではない場合は繰り返す
		} while (e.compareTo(BigInteger.ONE) <= 0 || e.compareTo(phi) >= 0 || !e.gcd(phi).equals(BigInteger.ONE));
		
		// dの生成
		BigInteger d = e.modInverse(phi);   // ed≡1 mod φとなるdを求める
		
		Key key = new Key(n, e, d);
		
		return key;
	}
	
	public KeyGen() {
		this.BIT_LENGTH = 64;
	}
}
