package java.sql;

public class SQLWarning
  extends SQLException
{
  private static final long serialVersionUID = 3917336774604784856L;
  
  public SQLWarning(String paramString1, String paramString2, int paramInt)
  {
    super(paramString1, paramString2, paramInt);
    DriverManager.println("SQLWarning: reason(" + paramString1 + ") SQLState(" + paramString2 + ") vendor code(" + paramInt + ")");
  }
  
  public SQLWarning(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
    DriverManager.println("SQLWarning: reason(" + paramString1 + ") SQLState(" + paramString2 + ")");
  }
  
  public SQLWarning(String paramString)
  {
    super(paramString);
    DriverManager.println("SQLWarning: reason(" + paramString + ")");
  }
  
  public SQLWarning()
  {
    DriverManager.println("SQLWarning: ");
  }
  
  public SQLWarning(Throwable paramThrowable)
  {
    super(paramThrowable);
    DriverManager.println("SQLWarning");
  }
  
  public SQLWarning(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    DriverManager.println("SQLWarning : reason(" + paramString + ")");
  }
  
  public SQLWarning(String paramString1, String paramString2, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramThrowable);
    DriverManager.println("SQLWarning: reason(" + paramString1 + ") SQLState(" + paramString2 + ")");
  }
  
  public SQLWarning(String paramString1, String paramString2, int paramInt, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramInt, paramThrowable);
    DriverManager.println("SQLWarning: reason(" + paramString1 + ") SQLState(" + paramString2 + ") vendor code(" + paramInt + ")");
  }
  
  public SQLWarning getNextWarning()
  {
    try
    {
      return (SQLWarning)getNextException();
    }
    catch (ClassCastException localClassCastException)
    {
      throw new Error("SQLWarning chain holds value that is not a SQLWarning");
    }
  }
  
  public void setNextWarning(SQLWarning paramSQLWarning)
  {
    setNextException(paramSQLWarning);
  }
}
