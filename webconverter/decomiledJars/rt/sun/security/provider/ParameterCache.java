package sun.security.provider;

import java.math.BigInteger;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAGenParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.spec.DHParameterSpec;

public final class ParameterCache
{
  private static final Map<Integer, DSAParameterSpec> dsaCache;
  private static final Map<Integer, DHParameterSpec> dhCache = new ConcurrentHashMap();
  
  private ParameterCache() {}
  
  public static DSAParameterSpec getCachedDSAParameterSpec(int paramInt1, int paramInt2)
  {
    return (DSAParameterSpec)dsaCache.get(Integer.valueOf(paramInt1 + paramInt2));
  }
  
  public static DHParameterSpec getCachedDHParameterSpec(int paramInt)
  {
    return (DHParameterSpec)dhCache.get(Integer.valueOf(paramInt));
  }
  
  public static DSAParameterSpec getDSAParameterSpec(int paramInt, SecureRandom paramSecureRandom)
    throws NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException
  {
    if (paramInt <= 1024) {
      return getDSAParameterSpec(paramInt, 160, paramSecureRandom);
    }
    if (paramInt == 2048) {
      return getDSAParameterSpec(paramInt, 224, paramSecureRandom);
    }
    return null;
  }
  
  public static DSAParameterSpec getDSAParameterSpec(int paramInt1, int paramInt2, SecureRandom paramSecureRandom)
    throws NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException
  {
    DSAParameterSpec localDSAParameterSpec = getCachedDSAParameterSpec(paramInt1, paramInt2);
    if (localDSAParameterSpec != null) {
      return localDSAParameterSpec;
    }
    localDSAParameterSpec = getNewDSAParameterSpec(paramInt1, paramInt2, paramSecureRandom);
    dsaCache.put(Integer.valueOf(paramInt1 + paramInt2), localDSAParameterSpec);
    return localDSAParameterSpec;
  }
  
  public static DHParameterSpec getDHParameterSpec(int paramInt, SecureRandom paramSecureRandom)
    throws NoSuchAlgorithmException, InvalidParameterSpecException
  {
    DHParameterSpec localDHParameterSpec = getCachedDHParameterSpec(paramInt);
    if (localDHParameterSpec != null) {
      return localDHParameterSpec;
    }
    AlgorithmParameterGenerator localAlgorithmParameterGenerator = AlgorithmParameterGenerator.getInstance("DH");
    localAlgorithmParameterGenerator.init(paramInt, paramSecureRandom);
    AlgorithmParameters localAlgorithmParameters = localAlgorithmParameterGenerator.generateParameters();
    localDHParameterSpec = (DHParameterSpec)localAlgorithmParameters.getParameterSpec(DHParameterSpec.class);
    dhCache.put(Integer.valueOf(paramInt), localDHParameterSpec);
    return localDHParameterSpec;
  }
  
  public static DSAParameterSpec getNewDSAParameterSpec(int paramInt1, int paramInt2, SecureRandom paramSecureRandom)
    throws NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException
  {
    AlgorithmParameterGenerator localAlgorithmParameterGenerator = AlgorithmParameterGenerator.getInstance("DSA");
    if (paramInt1 < 1024)
    {
      localAlgorithmParameterGenerator.init(paramInt1, paramSecureRandom);
    }
    else
    {
      localObject = new DSAGenParameterSpec(paramInt1, paramInt2);
      localAlgorithmParameterGenerator.init((AlgorithmParameterSpec)localObject, paramSecureRandom);
    }
    Object localObject = localAlgorithmParameterGenerator.generateParameters();
    DSAParameterSpec localDSAParameterSpec = (DSAParameterSpec)((AlgorithmParameters)localObject).getParameterSpec(DSAParameterSpec.class);
    return localDSAParameterSpec;
  }
  
  static
  {
    dsaCache = new ConcurrentHashMap();
    BigInteger localBigInteger1 = new BigInteger("fca682ce8e12caba26efccf7110e526db078b05edecbcd1eb4a208f3ae1617ae01f35b91a47e6df63413c5e12ed0899bcd132acd50d99151bdc43ee737592e17", 16);
    BigInteger localBigInteger2 = new BigInteger("962eddcc369cba8ebb260ee6b6a126d9346e38c5", 16);
    BigInteger localBigInteger3 = new BigInteger("678471b27a9cf44ee91a49c5147db1a9aaf244f05a434d6486931d2d14271b9e35030b71fd73da179069b32e2935630e1c2062354d0da20a6c416e50be794ca4", 16);
    BigInteger localBigInteger4 = new BigInteger("e9e642599d355f37c97ffd3567120b8e25c9cd43e927b3a9670fbec5d890141922d2c3b3ad2480093799869d1e846aab49fab0ad26d2ce6a22219d470bce7d777d4a21fbe9c270b57f607002f3cef8393694cf45ee3688c11a8c56ab127a3daf", 16);
    BigInteger localBigInteger5 = new BigInteger("9cdbd84c9f1ac2f38d0f80f42ab952e7338bf511", 16);
    BigInteger localBigInteger6 = new BigInteger("30470ad5a005fb14ce2d9dcd87e38bc7d1b1c5facbaecbe95f190aa7a31d23c4dbbcbe06174544401a5b2c020965d8c2bd2171d3668445771f74ba084d2029d83c1c158547f3a9f1a2715be23d51ae4d3e5a1f6a7064f316933a346d3f529252", 16);
    BigInteger localBigInteger7 = new BigInteger("fd7f53811d75122952df4a9c2eece4e7f611b7523cef4400c31e3f80b6512669455d402251fb593d8d58fabfc5f5ba30f6cb9b556cd7813b801d346ff26660b76b9950a5a49f9fe8047b1022c24fbba9d7feb7c61bf83b57e7c6a8a6150f04fb83f6d3c51ec3023554135a169132f675f3ae2b61d72aeff22203199dd14801c7", 16);
    BigInteger localBigInteger8 = new BigInteger("9760508f15230bccb292b982a2eb840bf0581cf5", 16);
    BigInteger localBigInteger9 = new BigInteger("f7e1a085d69b3ddecbbcab5c36b857b97994afbbfa3aea82f9574c0b3d0782675159578ebad4594fe67107108180b449167123e84c281613b7cf09328cc8a6e13c167a8b547c8d28e0a3ae1e2bb3a675916ea37f0bfa213562f1fb627a01243bcca4f1bea8519089a883dfe15ae59f06928b665e807b552564014c3bfecf492a", 16);
    dsaCache.put(Integer.valueOf(672), new DSAParameterSpec(localBigInteger1, localBigInteger2, localBigInteger3));
    dsaCache.put(Integer.valueOf(928), new DSAParameterSpec(localBigInteger4, localBigInteger5, localBigInteger6));
    dsaCache.put(Integer.valueOf(1184), new DSAParameterSpec(localBigInteger7, localBigInteger8, localBigInteger9));
    BigInteger localBigInteger10 = new BigInteger("8f7935d9b9aae9bfabed887acf4951b6f32ec59e3baf3718e8eac4961f3efd3606e74351a9c4183339b809e7c2ae1c539ba7475b85d011adb8b47987754984695cac0e8f14b3360828a22ffa27110a3d62a993453409a0fe696c4658f84bdd20819c3709a01057b195adcd00233dba5484b6291f9d648ef883448677979cec04b434a6ac2e75e9985de23db0292fc1118c9ffa9d8181e7338db792b730d7b9e349592f68099872153915ea3d6b8b4653c633458f803b32a4c2e0f27290256e4e3f8a3b0838a1c450e4e18c1a29a37ddf5ea143de4b66ff04903ed5cf1623e158d487c608e97f211cd81dca23cb6e380765f822e342be484c05763939601cd667", 16);
    BigInteger localBigInteger11 = new BigInteger("baf696a68578f7dfdee7fa67c977c785ef32b233bae580c0bcd5695d", 16);
    BigInteger localBigInteger12 = new BigInteger("16a65c58204850704e7502a39757040d34da3a3478c154d4e4a5c02d242ee04f96e61e4bd0904abdac8f37eeb1e09f3182d23c9043cb642f88004160edf9ca09b32076a79c32a627f2473e91879ba2c4e744bd2081544cb55b802c368d1fa83ed489e94e0fa0688e32428a5c78c478c68d0527b71c9a3abb0b0be12c44689639e7d3ce74db101a65aa2b87f64c6826db3ec72f4b5599834bb4edb02f7c90e9a496d3a55d535bebfc45d4f619f63f3dedbb873925c2f224e07731296da887ec1e4748f87efb5fdeb75484316b2232dee553ddaf02112b0d1f02da30973224fe27aeda8b9d4b2922d9ba8be39ed9e103a63c52810bc688b7e2ed4316e1ef17dbde", 16);
    dsaCache.put(Integer.valueOf(2272), new DSAParameterSpec(localBigInteger10, localBigInteger11, localBigInteger12));
    BigInteger localBigInteger13 = new BigInteger("95475cf5d93e596c3fcd1d902add02f427f5f3c7210313bb45fb4d5bb2e5fe1cbd678cd4bbdd84c9836be1f31c0777725aeb6c2fc38b85f48076fa76bcd8146cc89a6fb2f706dd719898c2083dc8d896f84062e2c9c94d137b054a8d8096adb8d51952398eeca852a0af12df83e475aa65d4ec0c38a9560d5661186ff98b9fc9eb60eee8b030376b236bc73be3acdbd74fd61c1d2475fa3077b8f080467881ff7e1ca56fee066d79506ade51edbb5443a563927dbc4ba520086746175c8885925ebc64c6147906773496990cb714ec667304e261faee33b3cbdf008e0c3fa90650d97d3909c9275bf4ac86ffcb3d03e6dfc8ada5934242dd6d3bcca2a406cb0b", 16);
    BigInteger localBigInteger14 = new BigInteger("f8183668ba5fc5bb06b5981e6d8b795d30b8978d43ca0ec572e37e09939a9773", 16);
    BigInteger localBigInteger15 = new BigInteger("42debb9da5b3d88cc956e08787ec3f3a09bba5f48b889a74aaf53174aa0fbe7e3c5b8fcd7a53bef563b0e98560328960a9517f4014d3325fc7962bf1e049370d76d1314a76137e792f3f0db859d095e4a5b932024f079ecf2ef09c797452b0770e1350782ed57ddf794979dcef23cb96f183061965c4ebc93c9c71c56b925955a75f94cccf1449ac43d586d0beee43251b0b2287349d68de0d144403f13e802f4146d882e057af19b6f6275c6676c8fa0e3ca2713a3257fd1b27d0639f695e347d8d1cf9ac819a26ca9b04cb0eb9b7b035988d15bbac65212a55239cfc7e58fae38d7250ab9991ffbc97134025fe8ce04c4399ad96569be91a546f4978693c7a", 16);
    dsaCache.put(Integer.valueOf(2304), new DSAParameterSpec(localBigInteger13, localBigInteger14, localBigInteger15));
    dhCache.put(Integer.valueOf(512), new DHParameterSpec(localBigInteger1, localBigInteger3));
    dhCache.put(Integer.valueOf(768), new DHParameterSpec(localBigInteger4, localBigInteger6));
    dhCache.put(Integer.valueOf(1024), new DHParameterSpec(localBigInteger7, localBigInteger9));
    dhCache.put(Integer.valueOf(2048), new DHParameterSpec(localBigInteger10, localBigInteger12));
  }
}
