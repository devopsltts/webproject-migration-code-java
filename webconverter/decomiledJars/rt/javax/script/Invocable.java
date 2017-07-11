package javax.script;

public abstract interface Invocable
{
  public abstract Object invokeMethod(Object paramObject, String paramString, Object... paramVarArgs)
    throws ScriptException, NoSuchMethodException;
  
  public abstract Object invokeFunction(String paramString, Object... paramVarArgs)
    throws ScriptException, NoSuchMethodException;
  
  public abstract <T> T getInterface(Class<T> paramClass);
  
  public abstract <T> T getInterface(Object paramObject, Class<T> paramClass);
}
