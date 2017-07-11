package sun.security.smartcardio;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CardTerminals.State;

final class PCSCTerminals
  extends CardTerminals
{
  private static long contextId;
  private Map<String, ReaderState> stateMap;
  private static final Map<String, Reference<TerminalImpl>> terminals = new HashMap();
  
  PCSCTerminals() {}
  
  static synchronized void initContext()
    throws PCSCException
  {
    if (contextId == 0L) {
      contextId = PCSC.SCardEstablishContext(0);
    }
  }
  
  private static synchronized TerminalImpl implGetTerminal(String paramString)
  {
    Reference localReference = (Reference)terminals.get(paramString);
    TerminalImpl localTerminalImpl = localReference != null ? (TerminalImpl)localReference.get() : null;
    if (localTerminalImpl != null) {
      return localTerminalImpl;
    }
    localTerminalImpl = new TerminalImpl(contextId, paramString);
    terminals.put(paramString, new WeakReference(localTerminalImpl));
    return localTerminalImpl;
  }
  
  public synchronized List<CardTerminal> list(CardTerminals.State paramState)
    throws CardException
  {
    if (paramState == null) {
      throw new NullPointerException();
    }
    try
    {
      String[] arrayOfString1 = PCSC.SCardListReaders(contextId);
      ArrayList localArrayList = new ArrayList(arrayOfString1.length);
      if (this.stateMap == null) {
        if (paramState == CardTerminals.State.CARD_INSERTION) {
          paramState = CardTerminals.State.CARD_PRESENT;
        } else if (paramState == CardTerminals.State.CARD_REMOVAL) {
          paramState = CardTerminals.State.CARD_ABSENT;
        }
      }
      for (String str : arrayOfString1)
      {
        TerminalImpl localTerminalImpl = implGetTerminal(str);
        ReaderState localReaderState;
        switch (1.$SwitchMap$javax$smartcardio$CardTerminals$State[paramState.ordinal()])
        {
        case 1: 
          localArrayList.add(localTerminalImpl);
          break;
        case 2: 
          if (localTerminalImpl.isCardPresent()) {
            localArrayList.add(localTerminalImpl);
          }
          break;
        case 3: 
          if (!localTerminalImpl.isCardPresent()) {
            localArrayList.add(localTerminalImpl);
          }
          break;
        case 4: 
          localReaderState = (ReaderState)this.stateMap.get(str);
          if ((localReaderState != null) && (localReaderState.isInsertion())) {
            localArrayList.add(localTerminalImpl);
          }
          break;
        case 5: 
          localReaderState = (ReaderState)this.stateMap.get(str);
          if ((localReaderState != null) && (localReaderState.isRemoval())) {
            localArrayList.add(localTerminalImpl);
          }
          break;
        default: 
          throw new CardException("Unknown state: " + paramState);
        }
      }
      return Collections.unmodifiableList(localArrayList);
    }
    catch (PCSCException localPCSCException)
    {
      throw new CardException("list() failed", localPCSCException);
    }
  }
  
  public synchronized boolean waitForChange(long paramLong)
    throws CardException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Timeout must not be negative: " + paramLong);
    }
    if (this.stateMap == null)
    {
      this.stateMap = new HashMap();
      waitForChange(0L);
    }
    if (paramLong == 0L) {
      paramLong = -1L;
    }
    try
    {
      String[] arrayOfString = PCSC.SCardListReaders(contextId);
      int i = arrayOfString.length;
      if (i == 0) {
        throw new IllegalStateException("No terminals available");
      }
      int[] arrayOfInt = new int[i];
      ReaderState[] arrayOfReaderState = new ReaderState[i];
      Object localObject;
      for (int j = 0; j < arrayOfString.length; j++)
      {
        localObject = arrayOfString[j];
        ReaderState localReaderState = (ReaderState)this.stateMap.get(localObject);
        if (localReaderState == null) {
          localReaderState = new ReaderState();
        }
        arrayOfReaderState[j] = localReaderState;
        arrayOfInt[j] = localReaderState.get();
      }
      arrayOfInt = PCSC.SCardGetStatusChange(contextId, paramLong, arrayOfInt, arrayOfString);
      this.stateMap.clear();
      for (j = 0; j < i; j++)
      {
        localObject = arrayOfReaderState[j];
        ((ReaderState)localObject).update(arrayOfInt[j]);
        this.stateMap.put(arrayOfString[j], localObject);
      }
      return true;
    }
    catch (PCSCException localPCSCException)
    {
      if (localPCSCException.code == -2146435062) {
        return false;
      }
      throw new CardException("waitForChange() failed", localPCSCException);
    }
  }
  
  /* Error */
  static List<CardTerminal> waitForCards(List<? extends CardTerminal> paramList, long paramLong, boolean paramBoolean)
    throws CardException
  {
    // Byte code:
    //   0: lload_1
    //   1: lconst_0
    //   2: lcmp
    //   3: ifne +15 -> 18
    //   6: ldc2_w 106
    //   9: lstore_1
    //   10: ldc2_w 106
    //   13: lstore 4
    //   15: goto +6 -> 21
    //   18: lconst_0
    //   19: lstore 4
    //   21: aload_0
    //   22: invokeinterface 250 1 0
    //   27: anewarray 116	java/lang/String
    //   30: astore 6
    //   32: iconst_0
    //   33: istore 7
    //   35: aload_0
    //   36: invokeinterface 252 1 0
    //   41: astore 8
    //   43: aload 8
    //   45: invokeinterface 248 1 0
    //   50: ifeq +80 -> 130
    //   53: aload 8
    //   55: invokeinterface 249 1 0
    //   60: checkcast 128	javax/smartcardio/CardTerminal
    //   63: astore 9
    //   65: aload 9
    //   67: instanceof 136
    //   70: ifne +37 -> 107
    //   73: new 112	java/lang/IllegalArgumentException
    //   76: dup
    //   77: new 117	java/lang/StringBuilder
    //   80: dup
    //   81: invokespecial 219	java/lang/StringBuilder:<init>	()V
    //   84: ldc 2
    //   86: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   89: aload 9
    //   91: invokevirtual 218	java/lang/Object:getClass	()Ljava/lang/Class;
    //   94: invokevirtual 214	java/lang/Class:getName	()Ljava/lang/String;
    //   97: invokevirtual 223	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   100: invokevirtual 220	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   103: invokespecial 215	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   106: athrow
    //   107: aload 9
    //   109: checkcast 136	sun/security/smartcardio/TerminalImpl
    //   112: astore 10
    //   114: aload 6
    //   116: iload 7
    //   118: iinc 7 1
    //   121: aload 10
    //   123: getfield 213	sun/security/smartcardio/TerminalImpl:name	Ljava/lang/String;
    //   126: aastore
    //   127: goto -84 -> 43
    //   130: aload 6
    //   132: arraylength
    //   133: newarray int
    //   135: astore 8
    //   137: aload 8
    //   139: iconst_0
    //   140: invokestatic 228	java/util/Arrays:fill	([II)V
    //   143: getstatic 209	sun/security/smartcardio/PCSCTerminals:contextId	J
    //   146: lload 4
    //   148: aload 8
    //   150: aload 6
    //   152: invokestatic 239	sun/security/smartcardio/PCSC:SCardGetStatusChange	(JJ[I[Ljava/lang/String;)[I
    //   155: astore 8
    //   157: lload_1
    //   158: lstore 4
    //   160: aconst_null
    //   161: astore 9
    //   163: iconst_0
    //   164: istore 7
    //   166: iload 7
    //   168: aload 6
    //   170: arraylength
    //   171: if_icmpge +63 -> 234
    //   174: aload 8
    //   176: iload 7
    //   178: iaload
    //   179: bipush 32
    //   181: iand
    //   182: ifeq +7 -> 189
    //   185: iconst_1
    //   186: goto +4 -> 190
    //   189: iconst_0
    //   190: istore 10
    //   192: iload 10
    //   194: iload_3
    //   195: if_icmpne +33 -> 228
    //   198: aload 9
    //   200: ifnonnull +12 -> 212
    //   203: new 120	java/util/ArrayList
    //   206: dup
    //   207: invokespecial 226	java/util/ArrayList:<init>	()V
    //   210: astore 9
    //   212: aload 9
    //   214: aload 6
    //   216: iload 7
    //   218: aaload
    //   219: invokestatic 241	sun/security/smartcardio/PCSCTerminals:implGetTerminal	(Ljava/lang/String;)Lsun/security/smartcardio/TerminalImpl;
    //   222: invokeinterface 251 2 0
    //   227: pop
    //   228: iinc 7 1
    //   231: goto -65 -> 166
    //   234: aload 9
    //   236: ifnull +9 -> 245
    //   239: aload 9
    //   241: invokestatic 230	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   244: areturn
    //   245: goto -102 -> 143
    //   248: astore 9
    //   250: aload 9
    //   252: getfield 208	sun/security/smartcardio/PCSCException:code	I
    //   255: ldc 1
    //   257: if_icmpne +7 -> 264
    //   260: invokestatic 229	java/util/Collections:emptyList	()Ljava/util/List;
    //   263: areturn
    //   264: new 127	javax/smartcardio/CardException
    //   267: dup
    //   268: ldc 7
    //   270: aload 9
    //   272: invokespecial 233	javax/smartcardio/CardException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   275: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	276	0	paramList	List<? extends CardTerminal>
    //   0	276	1	paramLong	long
    //   0	276	3	paramBoolean	boolean
    //   13	146	4	l	long
    //   30	185	6	arrayOfString	String[]
    //   33	196	7	i	int
    //   41	134	8	localObject1	Object
    //   63	177	9	localObject2	Object
    //   248	23	9	localPCSCException	PCSCException
    //   112	10	10	localTerminalImpl	TerminalImpl
    //   190	6	10	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   143	244	248	sun/security/smartcardio/PCSCException
    //   245	248	248	sun/security/smartcardio/PCSCException
  }
  
  private static class ReaderState
  {
    private int current = 0;
    private int previous = 0;
    
    ReaderState() {}
    
    int get()
    {
      return this.current;
    }
    
    void update(int paramInt)
    {
      this.previous = this.current;
      this.current = paramInt;
    }
    
    boolean isInsertion()
    {
      return (!present(this.previous)) && (present(this.current));
    }
    
    boolean isRemoval()
    {
      return (present(this.previous)) && (!present(this.current));
    }
    
    static boolean present(int paramInt)
    {
      return (paramInt & 0x20) != 0;
    }
  }
}
