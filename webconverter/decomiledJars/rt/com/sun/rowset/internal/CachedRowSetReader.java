package com.sun.rowset.internal;

import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.RowSetInternal;
import javax.sql.RowSetReader;
import javax.sql.rowset.CachedRowSet;

public class CachedRowSetReader
  implements RowSetReader, Serializable
{
  private int writerCalls = 0;
  private boolean userCon = false;
  private int startPosition;
  private JdbcRowSetResourceBundle resBundle;
  static final long serialVersionUID = 5049738185801363801L;
  
  public CachedRowSetReader()
  {
    try
    {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  public void readData(RowSetInternal paramRowSetInternal)
    throws SQLException
  {
    Connection localConnection = null;
    try
    {
      CachedRowSet localCachedRowSet = (CachedRowSet)paramRowSetInternal;
      if ((localCachedRowSet.getPageSize() == 0) && (localCachedRowSet.size() > 0)) {
        localCachedRowSet.close();
      }
      this.writerCalls = 0;
      this.userCon = false;
      localConnection = connect(paramRowSetInternal);
      if ((localConnection == null) || (localCachedRowSet.getCommand() == null)) {
        throw new SQLException(this.resBundle.handleGetObject("crsreader.connecterr").toString());
      }
      try
      {
        localConnection.setTransactionIsolation(localCachedRowSet.getTransactionIsolation());
      }
      catch (Exception localException2) {}
      PreparedStatement localPreparedStatement = localConnection.prepareStatement(localCachedRowSet.getCommand());
      decodeParams(paramRowSetInternal.getParams(), localPreparedStatement);
      try
      {
        localPreparedStatement.setMaxRows(localCachedRowSet.getMaxRows());
        localPreparedStatement.setMaxFieldSize(localCachedRowSet.getMaxFieldSize());
        localPreparedStatement.setEscapeProcessing(localCachedRowSet.getEscapeProcessing());
        localPreparedStatement.setQueryTimeout(localCachedRowSet.getQueryTimeout());
      }
      catch (Exception localException3)
      {
        throw new SQLException(localException3.getMessage());
      }
      if (localCachedRowSet.getCommand().toLowerCase().indexOf("select") != -1)
      {
        ResultSet localResultSet = localPreparedStatement.executeQuery();
        if (localCachedRowSet.getPageSize() == 0)
        {
          localCachedRowSet.populate(localResultSet);
        }
        else
        {
          localPreparedStatement = localConnection.prepareStatement(localCachedRowSet.getCommand(), 1004, 1008);
          decodeParams(paramRowSetInternal.getParams(), localPreparedStatement);
          try
          {
            localPreparedStatement.setMaxRows(localCachedRowSet.getMaxRows());
            localPreparedStatement.setMaxFieldSize(localCachedRowSet.getMaxFieldSize());
            localPreparedStatement.setEscapeProcessing(localCachedRowSet.getEscapeProcessing());
            localPreparedStatement.setQueryTimeout(localCachedRowSet.getQueryTimeout());
          }
          catch (Exception localException4)
          {
            throw new SQLException(localException4.getMessage());
          }
          localResultSet = localPreparedStatement.executeQuery();
          localCachedRowSet.populate(localResultSet, this.startPosition);
        }
        localResultSet.close();
      }
      else
      {
        localPreparedStatement.executeUpdate();
      }
      localPreparedStatement.close();
      try
      {
        localConnection.commit();
      }
      catch (SQLException localSQLException3) {}
      if (getCloseConnection() == true) {
        localConnection.close();
      }
      try
      {
        if ((localConnection != null) && (getCloseConnection() == true))
        {
          try
          {
            if (!localConnection.getAutoCommit()) {
              localConnection.rollback();
            }
          }
          catch (Exception localException1) {}
          localConnection.close();
          localConnection = null;
        }
      }
      catch (SQLException localSQLException1) {}
      return;
    }
    catch (SQLException localSQLException2)
    {
      throw localSQLException2;
    }
    finally
    {
      try
      {
        if ((localConnection != null) && (getCloseConnection() == true))
        {
          try
          {
            if (!localConnection.getAutoCommit()) {
              localConnection.rollback();
            }
          }
          catch (Exception localException5) {}
          localConnection.close();
          localConnection = null;
        }
      }
      catch (SQLException localSQLException4) {}
    }
  }
  
  public boolean reset()
    throws SQLException
  {
    this.writerCalls += 1;
    return this.writerCalls == 1;
  }
  
  public Connection connect(RowSetInternal paramRowSetInternal)
    throws SQLException
  {
    if (paramRowSetInternal.getConnection() != null)
    {
      this.userCon = true;
      return paramRowSetInternal.getConnection();
    }
    if (((RowSet)paramRowSetInternal).getDataSourceName() != null) {
      try
      {
        InitialContext localInitialContext = new InitialContext();
        localObject = (DataSource)localInitialContext.lookup(((RowSet)paramRowSetInternal).getDataSourceName());
        if (((RowSet)paramRowSetInternal).getUsername() != null) {
          return ((DataSource)localObject).getConnection(((RowSet)paramRowSetInternal).getUsername(), ((RowSet)paramRowSetInternal).getPassword());
        }
        return ((DataSource)localObject).getConnection();
      }
      catch (NamingException localNamingException)
      {
        Object localObject = new SQLException(this.resBundle.handleGetObject("crsreader.connect").toString());
        ((SQLException)localObject).initCause(localNamingException);
        throw ((Throwable)localObject);
      }
    }
    if (((RowSet)paramRowSetInternal).getUrl() != null) {
      return DriverManager.getConnection(((RowSet)paramRowSetInternal).getUrl(), ((RowSet)paramRowSetInternal).getUsername(), ((RowSet)paramRowSetInternal).getPassword());
    }
    return null;
  }
  
  private void decodeParams(Object[] paramArrayOfObject, PreparedStatement paramPreparedStatement)
    throws SQLException
  {
    Object[] arrayOfObject = null;
    for (int i = 0; i < paramArrayOfObject.length; i++) {
      if ((paramArrayOfObject[i] instanceof Object[]))
      {
        arrayOfObject = (Object[])paramArrayOfObject[i];
        if (arrayOfObject.length == 2)
        {
          if (arrayOfObject[0] == null)
          {
            paramPreparedStatement.setNull(i + 1, ((Integer)arrayOfObject[1]).intValue());
          }
          else if (((arrayOfObject[0] instanceof Date)) || ((arrayOfObject[0] instanceof Time)) || ((arrayOfObject[0] instanceof Timestamp)))
          {
            System.err.println(this.resBundle.handleGetObject("crsreader.datedetected").toString());
            if ((arrayOfObject[1] instanceof Calendar))
            {
              System.err.println(this.resBundle.handleGetObject("crsreader.caldetected").toString());
              paramPreparedStatement.setDate(i + 1, (Date)arrayOfObject[0], (Calendar)arrayOfObject[1]);
            }
            else
            {
              throw new SQLException(this.resBundle.handleGetObject("crsreader.paramtype").toString());
            }
          }
          else if ((arrayOfObject[0] instanceof Reader))
          {
            paramPreparedStatement.setCharacterStream(i + 1, (Reader)arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
          }
          else if ((arrayOfObject[1] instanceof Integer))
          {
            paramPreparedStatement.setObject(i + 1, arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
          }
        }
        else if (arrayOfObject.length == 3)
        {
          if (arrayOfObject[0] == null)
          {
            paramPreparedStatement.setNull(i + 1, ((Integer)arrayOfObject[1]).intValue(), (String)arrayOfObject[2]);
          }
          else
          {
            if ((arrayOfObject[0] instanceof InputStream)) {
              switch (((Integer)arrayOfObject[2]).intValue())
              {
              case 0: 
                paramPreparedStatement.setUnicodeStream(i + 1, (InputStream)arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
                break;
              case 1: 
                paramPreparedStatement.setBinaryStream(i + 1, (InputStream)arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
                break;
              case 2: 
                paramPreparedStatement.setAsciiStream(i + 1, (InputStream)arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
                break;
              default: 
                throw new SQLException(this.resBundle.handleGetObject("crsreader.paramtype").toString());
              }
            }
            if (((arrayOfObject[1] instanceof Integer)) && ((arrayOfObject[2] instanceof Integer))) {
              paramPreparedStatement.setObject(i + 1, arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue(), ((Integer)arrayOfObject[2]).intValue());
            } else {
              throw new SQLException(this.resBundle.handleGetObject("crsreader.paramtype").toString());
            }
          }
        }
        else {
          paramPreparedStatement.setObject(i + 1, paramArrayOfObject[i]);
        }
      }
      else
      {
        paramPreparedStatement.setObject(i + 1, paramArrayOfObject[i]);
      }
    }
  }
  
  protected boolean getCloseConnection()
  {
    return this.userCon != true;
  }
  
  public void setStartPosition(int paramInt)
  {
    this.startPosition = paramInt;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    try
    {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
}
