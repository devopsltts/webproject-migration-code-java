package java.awt;

import java.awt.event.KeyEvent;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import sun.awt.AppContext;

public class AWTKeyStroke
  implements Serializable
{
  static final long serialVersionUID = -6430539691155161871L;
  private static Map<String, Integer> modifierKeywords;
  private static VKCollection vks;
  private static Object APP_CONTEXT_CACHE_KEY;
  private static AWTKeyStroke APP_CONTEXT_KEYSTROKE_KEY;
  private char keyChar = 65535;
  private int keyCode = 0;
  private int modifiers;
  private boolean onKeyRelease;
  
  private static Class<AWTKeyStroke> getAWTKeyStrokeClass()
  {
    Object localObject = (Class)AppContext.getAppContext().get(AWTKeyStroke.class);
    if (localObject == null)
    {
      localObject = AWTKeyStroke.class;
      AppContext.getAppContext().put(AWTKeyStroke.class, AWTKeyStroke.class);
    }
    return localObject;
  }
  
  protected AWTKeyStroke() {}
  
  protected AWTKeyStroke(char paramChar, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.keyChar = paramChar;
    this.keyCode = paramInt1;
    this.modifiers = paramInt2;
    this.onKeyRelease = paramBoolean;
  }
  
  protected static void registerSubclass(Class<?> paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException("subclass cannot be null");
    }
    synchronized (AWTKeyStroke.class)
    {
      localObject1 = (Class)AppContext.getAppContext().get(AWTKeyStroke.class);
      if ((localObject1 != null) && (localObject1.equals(paramClass))) {
        return;
      }
    }
    if (!AWTKeyStroke.class.isAssignableFrom(paramClass)) {
      throw new ClassCastException("subclass is not derived from AWTKeyStroke");
    }
    ??? = getCtor(paramClass);
    Object localObject1 = "subclass could not be instantiated";
    if (??? == null) {
      throw new IllegalArgumentException((String)localObject1);
    }
    try
    {
      AWTKeyStroke localAWTKeyStroke = (AWTKeyStroke)((Constructor)???).newInstance((Object[])null);
      if (localAWTKeyStroke == null) {
        throw new IllegalArgumentException((String)localObject1);
      }
    }
    catch (NoSuchMethodError localNoSuchMethodError)
    {
      throw new IllegalArgumentException((String)localObject1);
    }
    catch (ExceptionInInitializerError localExceptionInInitializerError)
    {
      throw new IllegalArgumentException((String)localObject1);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new IllegalArgumentException((String)localObject1);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new IllegalArgumentException((String)localObject1);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new IllegalArgumentException((String)localObject1);
    }
    synchronized (AWTKeyStroke.class)
    {
      AppContext.getAppContext().put(AWTKeyStroke.class, paramClass);
      AppContext.getAppContext().remove(APP_CONTEXT_CACHE_KEY);
      AppContext.getAppContext().remove(APP_CONTEXT_KEYSTROKE_KEY);
    }
  }
  
  private static Constructor getCtor(Class paramClass)
  {
    Constructor localConstructor = (Constructor)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Constructor run()
      {
        try
        {
          Constructor localConstructor = this.val$clazz.getDeclaredConstructor((Class[])null);
          if (localConstructor != null) {
            localConstructor.setAccessible(true);
          }
          return localConstructor;
        }
        catch (SecurityException localSecurityException) {}catch (NoSuchMethodException localNoSuchMethodException) {}
        return null;
      }
    });
    return localConstructor;
  }
  
  private static synchronized AWTKeyStroke getCachedStroke(char paramChar, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    Object localObject = (Map)AppContext.getAppContext().get(APP_CONTEXT_CACHE_KEY);
    AWTKeyStroke localAWTKeyStroke1 = (AWTKeyStroke)AppContext.getAppContext().get(APP_CONTEXT_KEYSTROKE_KEY);
    if (localObject == null)
    {
      localObject = new HashMap();
      AppContext.getAppContext().put(APP_CONTEXT_CACHE_KEY, localObject);
    }
    if (localAWTKeyStroke1 == null) {
      try
      {
        Class localClass = getAWTKeyStrokeClass();
        localAWTKeyStroke1 = (AWTKeyStroke)getCtor(localClass).newInstance((Object[])null);
        AppContext.getAppContext().put(APP_CONTEXT_KEYSTROKE_KEY, localAWTKeyStroke1);
      }
      catch (InstantiationException localInstantiationException)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
      }
    }
    localAWTKeyStroke1.keyChar = paramChar;
    localAWTKeyStroke1.keyCode = paramInt1;
    localAWTKeyStroke1.modifiers = mapNewModifiers(mapOldModifiers(paramInt2));
    localAWTKeyStroke1.onKeyRelease = paramBoolean;
    AWTKeyStroke localAWTKeyStroke2 = (AWTKeyStroke)((Map)localObject).get(localAWTKeyStroke1);
    if (localAWTKeyStroke2 == null)
    {
      localAWTKeyStroke2 = localAWTKeyStroke1;
      ((Map)localObject).put(localAWTKeyStroke2, localAWTKeyStroke2);
      AppContext.getAppContext().remove(APP_CONTEXT_KEYSTROKE_KEY);
    }
    return localAWTKeyStroke2;
  }
  
  public static AWTKeyStroke getAWTKeyStroke(char paramChar)
  {
    return getCachedStroke(paramChar, 0, 0, false);
  }
  
  public static AWTKeyStroke getAWTKeyStroke(Character paramCharacter, int paramInt)
  {
    if (paramCharacter == null) {
      throw new IllegalArgumentException("keyChar cannot be null");
    }
    return getCachedStroke(paramCharacter.charValue(), 0, paramInt, false);
  }
  
  public static AWTKeyStroke getAWTKeyStroke(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return getCachedStroke(65535, paramInt1, paramInt2, paramBoolean);
  }
  
  public static AWTKeyStroke getAWTKeyStroke(int paramInt1, int paramInt2)
  {
    return getCachedStroke(65535, paramInt1, paramInt2, false);
  }
  
  public static AWTKeyStroke getAWTKeyStrokeForEvent(KeyEvent paramKeyEvent)
  {
    int i = paramKeyEvent.getID();
    switch (i)
    {
    case 401: 
    case 402: 
      return getCachedStroke(65535, paramKeyEvent.getKeyCode(), paramKeyEvent.getModifiers(), i == 402);
    case 400: 
      return getCachedStroke(paramKeyEvent.getKeyChar(), 0, paramKeyEvent.getModifiers(), false);
    }
    return null;
  }
  
  public static AWTKeyStroke getAWTKeyStroke(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("String cannot be null");
    }
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, " ");
    int i = 0;
    boolean bool = false;
    int j = 0;
    int k = 0;
    synchronized (AWTKeyStroke.class)
    {
      if (modifierKeywords == null)
      {
        HashMap localHashMap = new HashMap(8, 1.0F);
        localHashMap.put("shift", Integer.valueOf(65));
        localHashMap.put("control", Integer.valueOf(130));
        localHashMap.put("ctrl", Integer.valueOf(130));
        localHashMap.put("meta", Integer.valueOf(260));
        localHashMap.put("alt", Integer.valueOf(520));
        localHashMap.put("altGraph", Integer.valueOf(8224));
        localHashMap.put("button1", Integer.valueOf(1024));
        localHashMap.put("button2", Integer.valueOf(2048));
        localHashMap.put("button3", Integer.valueOf(4096));
        modifierKeywords = Collections.synchronizedMap(localHashMap);
      }
    }
    ??? = localStringTokenizer.countTokens();
    for (Object localObject1 = 1; localObject1 <= ???; localObject1++)
    {
      String str = localStringTokenizer.nextToken();
      if (j != 0)
      {
        if ((str.length() != 1) || (localObject1 != ???)) {
          throw new IllegalArgumentException("String formatted incorrectly");
        }
        return getCachedStroke(str.charAt(0), 0, i, false);
      }
      Object localObject3;
      if ((k != 0) || (bool) || (localObject1 == ???))
      {
        if (localObject1 != ???) {
          throw new IllegalArgumentException("String formatted incorrectly");
        }
        localObject3 = "VK_" + str;
        int m = getVKValue((String)localObject3);
        return getCachedStroke(65535, m, i, bool);
      }
      if (str.equals("released"))
      {
        bool = true;
      }
      else if (str.equals("pressed"))
      {
        k = 1;
      }
      else if (str.equals("typed"))
      {
        j = 1;
      }
      else
      {
        localObject3 = (Integer)modifierKeywords.get(str);
        if (localObject3 != null) {
          i |= ((Integer)localObject3).intValue();
        } else {
          throw new IllegalArgumentException("String formatted incorrectly");
        }
      }
    }
    throw new IllegalArgumentException("String formatted incorrectly");
  }
  
  private static VKCollection getVKCollection()
  {
    if (vks == null) {
      vks = new VKCollection();
    }
    return vks;
  }
  
  private static int getVKValue(String paramString)
  {
    VKCollection localVKCollection = getVKCollection();
    Integer localInteger = localVKCollection.findCode(paramString);
    if (localInteger == null)
    {
      int i = 0;
      try
      {
        i = KeyEvent.class.getField(paramString).getInt(KeyEvent.class);
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        throw new IllegalArgumentException("String formatted incorrectly");
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new IllegalArgumentException("String formatted incorrectly");
      }
      localInteger = Integer.valueOf(i);
      localVKCollection.put(paramString, localInteger);
    }
    return localInteger.intValue();
  }
  
  public final char getKeyChar()
  {
    return this.keyChar;
  }
  
  public final int getKeyCode()
  {
    return this.keyCode;
  }
  
  public final int getModifiers()
  {
    return this.modifiers;
  }
  
  public final boolean isOnKeyRelease()
  {
    return this.onKeyRelease;
  }
  
  public final int getKeyEventType()
  {
    if (this.keyCode == 0) {
      return 400;
    }
    return this.onKeyRelease ? 402 : 401;
  }
  
  public int hashCode()
  {
    return (this.keyChar + '\001') * (2 * (this.keyCode + 1)) * (this.modifiers + 1) + (this.onKeyRelease ? 1 : 2);
  }
  
  public final boolean equals(Object paramObject)
  {
    if ((paramObject instanceof AWTKeyStroke))
    {
      AWTKeyStroke localAWTKeyStroke = (AWTKeyStroke)paramObject;
      return (localAWTKeyStroke.keyChar == this.keyChar) && (localAWTKeyStroke.keyCode == this.keyCode) && (localAWTKeyStroke.onKeyRelease == this.onKeyRelease) && (localAWTKeyStroke.modifiers == this.modifiers);
    }
    return false;
  }
  
  public String toString()
  {
    if (this.keyCode == 0) {
      return getModifiersText(this.modifiers) + "typed " + this.keyChar;
    }
    return getModifiersText(this.modifiers) + (this.onKeyRelease ? "released" : "pressed") + " " + getVKText(this.keyCode);
  }
  
  static String getModifiersText(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((paramInt & 0x40) != 0) {
      localStringBuilder.append("shift ");
    }
    if ((paramInt & 0x80) != 0) {
      localStringBuilder.append("ctrl ");
    }
    if ((paramInt & 0x100) != 0) {
      localStringBuilder.append("meta ");
    }
    if ((paramInt & 0x200) != 0) {
      localStringBuilder.append("alt ");
    }
    if ((paramInt & 0x2000) != 0) {
      localStringBuilder.append("altGraph ");
    }
    if ((paramInt & 0x400) != 0) {
      localStringBuilder.append("button1 ");
    }
    if ((paramInt & 0x800) != 0) {
      localStringBuilder.append("button2 ");
    }
    if ((paramInt & 0x1000) != 0) {
      localStringBuilder.append("button3 ");
    }
    return localStringBuilder.toString();
  }
  
  static String getVKText(int paramInt)
  {
    VKCollection localVKCollection = getVKCollection();
    Integer localInteger = Integer.valueOf(paramInt);
    String str = localVKCollection.findName(localInteger);
    if (str != null) {
      return str.substring(3);
    }
    int i = 25;
    Field[] arrayOfField = KeyEvent.class.getDeclaredFields();
    for (int j = 0; j < arrayOfField.length; j++) {
      try
      {
        if ((arrayOfField[j].getModifiers() == i) && (arrayOfField[j].getType() == Integer.TYPE) && (arrayOfField[j].getName().startsWith("VK_")) && (arrayOfField[j].getInt(KeyEvent.class) == paramInt))
        {
          str = arrayOfField[j].getName();
          localVKCollection.put(str, localInteger);
          return str.substring(3);
        }
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
      }
    }
    return "UNKNOWN";
  }
  
  protected Object readResolve()
    throws ObjectStreamException
  {
    synchronized (AWTKeyStroke.class)
    {
      if (getClass().equals(getAWTKeyStrokeClass())) {
        return getCachedStroke(this.keyChar, this.keyCode, this.modifiers, this.onKeyRelease);
      }
    }
    return this;
  }
  
  private static int mapOldModifiers(int paramInt)
  {
    if ((paramInt & 0x1) != 0) {
      paramInt |= 0x40;
    }
    if ((paramInt & 0x8) != 0) {
      paramInt |= 0x200;
    }
    if ((paramInt & 0x20) != 0) {
      paramInt |= 0x2000;
    }
    if ((paramInt & 0x2) != 0) {
      paramInt |= 0x80;
    }
    if ((paramInt & 0x4) != 0) {
      paramInt |= 0x100;
    }
    paramInt &= 0x3FC0;
    return paramInt;
  }
  
  private static int mapNewModifiers(int paramInt)
  {
    if ((paramInt & 0x40) != 0) {
      paramInt |= 0x1;
    }
    if ((paramInt & 0x200) != 0) {
      paramInt |= 0x8;
    }
    if ((paramInt & 0x2000) != 0) {
      paramInt |= 0x20;
    }
    if ((paramInt & 0x80) != 0) {
      paramInt |= 0x2;
    }
    if ((paramInt & 0x100) != 0) {
      paramInt |= 0x4;
    }
    return paramInt;
  }
  
  static
  {
    APP_CONTEXT_CACHE_KEY = new Object();
    APP_CONTEXT_KEYSTROKE_KEY = new AWTKeyStroke();
    Toolkit.loadLibraries();
  }
}
