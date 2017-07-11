package com.sun.rowset.internal;

import com.sun.rowset.CachedRowSetImpl;
import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import javax.sql.RowSetInternal;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.serial.SQLInputImpl;
import javax.sql.rowset.serial.SerialArray;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialStruct;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.TransactionalWriter;
import sun.reflect.misc.ReflectUtil;

public class CachedRowSetWriter
  implements TransactionalWriter, Serializable
{
  private transient Connection con;
  private String selectCmd;
  private String updateCmd;
  private String updateWhere;
  private String deleteCmd;
  private String deleteWhere;
  private String insertCmd;
  private int[] keyCols;
  private Object[] params;
  private CachedRowSetReader reader;
  private ResultSetMetaData callerMd;
  private int callerColumnCount;
  private CachedRowSetImpl crsResolve;
  private ArrayList<Integer> status;
  private int iChangedValsInDbAndCRS;
  private int iChangedValsinDbOnly;
  private JdbcRowSetResourceBundle resBundle;
  static final long serialVersionUID = -8506030970299413976L;
  
  public CachedRowSetWriter()
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
  
  public boolean writeData(RowSetInternal paramRowSetInternal)
    throws SQLException
  {
    long l = 0L;
    boolean bool = false;
    PreparedStatement localPreparedStatement = null;
    this.iChangedValsInDbAndCRS = 0;
    this.iChangedValsinDbOnly = 0;
    CachedRowSetImpl localCachedRowSetImpl = (CachedRowSetImpl)paramRowSetInternal;
    this.crsResolve = new CachedRowSetImpl();
    this.con = this.reader.connect(paramRowSetInternal);
    if (this.con == null) {
      throw new SQLException(this.resBundle.handleGetObject("crswriter.connect").toString());
    }
    initSQLStatements(localCachedRowSetImpl);
    RowSetMetaDataImpl localRowSetMetaDataImpl1 = (RowSetMetaDataImpl)localCachedRowSetImpl.getMetaData();
    RowSetMetaDataImpl localRowSetMetaDataImpl2 = new RowSetMetaDataImpl();
    int i = localRowSetMetaDataImpl1.getColumnCount();
    int j = localCachedRowSetImpl.size() + 1;
    this.status = new ArrayList(j);
    this.status.add(0, null);
    localRowSetMetaDataImpl2.setColumnCount(i);
    for (int k = 1; k <= i; k++)
    {
      localRowSetMetaDataImpl2.setColumnType(k, localRowSetMetaDataImpl1.getColumnType(k));
      localRowSetMetaDataImpl2.setColumnName(k, localRowSetMetaDataImpl1.getColumnName(k));
      localRowSetMetaDataImpl2.setNullable(k, 2);
    }
    this.crsResolve.setMetaData(localRowSetMetaDataImpl2);
    if (this.callerColumnCount < 1)
    {
      if (this.reader.getCloseConnection() == true) {
        this.con.close();
      }
      return true;
    }
    bool = localCachedRowSetImpl.getShowDeleted();
    localCachedRowSetImpl.setShowDeleted(true);
    localCachedRowSetImpl.beforeFirst();
    for (k = 1; localCachedRowSetImpl.next(); k++) {
      if (localCachedRowSetImpl.rowDeleted())
      {
        if (deleteOriginalRow(localCachedRowSetImpl, this.crsResolve))
        {
          this.status.add(k, Integer.valueOf(1));
          l += 1L;
        }
        else
        {
          this.status.add(k, Integer.valueOf(3));
        }
      }
      else if (localCachedRowSetImpl.rowInserted())
      {
        localPreparedStatement = this.con.prepareStatement(this.insertCmd);
        if (insertNewRow(localCachedRowSetImpl, localPreparedStatement, this.crsResolve))
        {
          this.status.add(k, Integer.valueOf(2));
          l += 1L;
        }
        else
        {
          this.status.add(k, Integer.valueOf(3));
        }
      }
      else if (localCachedRowSetImpl.rowUpdated())
      {
        if (updateOriginalRow(localCachedRowSetImpl))
        {
          this.status.add(k, Integer.valueOf(0));
          l += 1L;
        }
        else
        {
          this.status.add(k, Integer.valueOf(3));
        }
      }
      else
      {
        int m = localCachedRowSetImpl.getMetaData().getColumnCount();
        this.status.add(k, Integer.valueOf(3));
        this.crsResolve.moveToInsertRow();
        for (int n = 0; n < i; n++) {
          this.crsResolve.updateNull(n + 1);
        }
        this.crsResolve.insertRow();
        this.crsResolve.moveToCurrentRow();
      }
    }
    if (localPreparedStatement != null) {
      localPreparedStatement.close();
    }
    localCachedRowSetImpl.setShowDeleted(bool);
    localCachedRowSetImpl.beforeFirst();
    this.crsResolve.beforeFirst();
    if (l != 0L)
    {
      SyncProviderException localSyncProviderException = new SyncProviderException(l + " " + this.resBundle.handleGetObject("crswriter.conflictsno").toString());
      SyncResolverImpl localSyncResolverImpl = (SyncResolverImpl)localSyncProviderException.getSyncResolver();
      localSyncResolverImpl.setCachedRowSet(localCachedRowSetImpl);
      localSyncResolverImpl.setCachedRowSetResolver(this.crsResolve);
      localSyncResolverImpl.setStatus(this.status);
      localSyncResolverImpl.setCachedRowSetWriter(this);
      throw localSyncProviderException;
    }
    return true;
  }
  
  private boolean updateOriginalRow(CachedRowSet paramCachedRowSet)
    throws SQLException
  {
    int i = 0;
    int j = 0;
    ResultSet localResultSet1 = paramCachedRowSet.getOriginalRow();
    localResultSet1.next();
    try
    {
      this.updateWhere = buildWhereClause(this.updateWhere, localResultSet1);
      String str1 = this.selectCmd.toLowerCase();
      int k = str1.indexOf("where");
      if (k != -1)
      {
        String str2 = this.selectCmd.substring(0, k);
        this.selectCmd = str2;
      }
      PreparedStatement localPreparedStatement = this.con.prepareStatement(this.selectCmd + this.updateWhere, 1005, 1007);
      for (i = 0; i < this.keyCols.length; i++) {
        if (this.params[i] != null) {
          localPreparedStatement.setObject(++j, this.params[i]);
        }
      }
      try
      {
        localPreparedStatement.setMaxRows(paramCachedRowSet.getMaxRows());
        localPreparedStatement.setMaxFieldSize(paramCachedRowSet.getMaxFieldSize());
        localPreparedStatement.setEscapeProcessing(paramCachedRowSet.getEscapeProcessing());
        localPreparedStatement.setQueryTimeout(paramCachedRowSet.getQueryTimeout());
      }
      catch (Exception localException1) {}
      ResultSet localResultSet2 = null;
      localResultSet2 = localPreparedStatement.executeQuery();
      ResultSetMetaData localResultSetMetaData = localResultSet2.getMetaData();
      if (localResultSet2.next())
      {
        if (localResultSet2.next()) {
          return true;
        }
        localResultSet2.first();
        int m = 0;
        Vector localVector = new Vector();
        String str3 = this.updateCmd;
        int n = 1;
        Object localObject4 = null;
        int i1 = 1;
        int i2 = 1;
        this.crsResolve.moveToInsertRow();
        Object localObject5;
        for (i = 1; i <= this.callerColumnCount; i++)
        {
          Object localObject1 = localResultSet1.getObject(i);
          Object localObject2 = paramCachedRowSet.getObject(i);
          Object localObject3 = localResultSet2.getObject(i);
          localObject5 = paramCachedRowSet.getTypeMap() == null ? this.con.getTypeMap() : paramCachedRowSet.getTypeMap();
          if ((localObject3 instanceof Struct))
          {
            Struct localStruct = (Struct)localObject3;
            Class localClass = null;
            localClass = (Class)((Map)localObject5).get(localStruct.getSQLTypeName());
            if (localClass != null)
            {
              SQLData localSQLData = null;
              try
              {
                localSQLData = (SQLData)ReflectUtil.newInstance(localClass);
              }
              catch (Exception localException2)
              {
                throw new SQLException("Unable to Instantiate: ", localException2);
              }
              Object[] arrayOfObject = localStruct.getAttributes((Map)localObject5);
              SQLInputImpl localSQLInputImpl = new SQLInputImpl(arrayOfObject, (Map)localObject5);
              localSQLData.readSQL(localSQLInputImpl, localStruct.getSQLTypeName());
              localObject3 = localSQLData;
            }
          }
          else if ((localObject3 instanceof SQLData))
          {
            localObject3 = new SerialStruct((SQLData)localObject3, (Map)localObject5);
          }
          else if ((localObject3 instanceof Blob))
          {
            localObject3 = new SerialBlob((Blob)localObject3);
          }
          else if ((localObject3 instanceof Clob))
          {
            localObject3 = new SerialClob((Clob)localObject3);
          }
          else if ((localObject3 instanceof Array))
          {
            localObject3 = new SerialArray((Array)localObject3, (Map)localObject5);
          }
          n = 1;
          if ((localObject3 == null) && (localObject1 != null))
          {
            this.iChangedValsinDbOnly += 1;
            n = 0;
            localObject4 = localObject3;
          }
          else if ((localObject3 != null) && (!localObject3.equals(localObject1)))
          {
            this.iChangedValsinDbOnly += 1;
            n = 0;
            localObject4 = localObject3;
          }
          else if ((localObject1 == null) || (localObject2 == null))
          {
            if ((i1 == 0) || (i2 == 0)) {
              str3 = str3 + ", ";
            }
            str3 = str3 + paramCachedRowSet.getMetaData().getColumnName(i);
            localVector.add(Integer.valueOf(i));
            str3 = str3 + " = ? ";
            i1 = 0;
          }
          else if (localObject1.equals(localObject2))
          {
            m++;
          }
          else if ((!localObject1.equals(localObject2)) && (paramCachedRowSet.columnUpdated(i)))
          {
            if (localObject3.equals(localObject1))
            {
              if ((i2 == 0) || (i1 == 0)) {
                str3 = str3 + ", ";
              }
              str3 = str3 + paramCachedRowSet.getMetaData().getColumnName(i);
              localVector.add(Integer.valueOf(i));
              str3 = str3 + " = ? ";
              i2 = 0;
            }
            else
            {
              n = 0;
              localObject4 = localObject3;
              this.iChangedValsInDbAndCRS += 1;
            }
          }
          if (n == 0) {
            this.crsResolve.updateObject(i, localObject4);
          } else {
            this.crsResolve.updateNull(i);
          }
        }
        localResultSet2.close();
        localPreparedStatement.close();
        this.crsResolve.insertRow();
        this.crsResolve.moveToCurrentRow();
        if (((i1 == 0) && (localVector.size() == 0)) || (m == this.callerColumnCount)) {
          return false;
        }
        if ((this.iChangedValsInDbAndCRS != 0) || (this.iChangedValsinDbOnly != 0)) {
          return true;
        }
        str3 = str3 + this.updateWhere;
        localPreparedStatement = this.con.prepareStatement(str3);
        for (i = 0; i < localVector.size(); i++)
        {
          localObject5 = paramCachedRowSet.getObject(((Integer)localVector.get(i)).intValue());
          if (localObject5 != null) {
            localPreparedStatement.setObject(i + 1, localObject5);
          } else {
            localPreparedStatement.setNull(i + 1, paramCachedRowSet.getMetaData().getColumnType(i + 1));
          }
        }
        j = i;
        for (i = 0; i < this.keyCols.length; i++) {
          if (this.params[i] != null) {
            localPreparedStatement.setObject(++j, this.params[i]);
          }
        }
        i = localPreparedStatement.executeUpdate();
        return false;
      }
      return true;
    }
    catch (SQLException localSQLException)
    {
      localSQLException.printStackTrace();
      this.crsResolve.moveToInsertRow();
      for (i = 1; i <= this.callerColumnCount; i++) {
        this.crsResolve.updateNull(i);
      }
      this.crsResolve.insertRow();
      this.crsResolve.moveToCurrentRow();
    }
    return true;
  }
  
  /* Error */
  private boolean insertNewRow(CachedRowSet paramCachedRowSet, PreparedStatement paramPreparedStatement, CachedRowSetImpl paramCachedRowSetImpl)
    throws SQLException
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 4
    //   3: aload_0
    //   4: getfield 476	com/sun/rowset/internal/CachedRowSetWriter:con	Ljava/sql/Connection;
    //   7: aload_0
    //   8: getfield 473	com/sun/rowset/internal/CachedRowSetWriter:selectCmd	Ljava/lang/String;
    //   11: sipush 1005
    //   14: sipush 1007
    //   17: invokeinterface 568 4 0
    //   22: astore 5
    //   24: aconst_null
    //   25: astore 6
    //   27: aload 5
    //   29: invokeinterface 580 1 0
    //   34: astore 7
    //   36: aconst_null
    //   37: astore 8
    //   39: aload_0
    //   40: getfield 476	com/sun/rowset/internal/CachedRowSetWriter:con	Ljava/sql/Connection;
    //   43: invokeinterface 564 1 0
    //   48: aconst_null
    //   49: aconst_null
    //   50: aload_1
    //   51: invokeinterface 608 1 0
    //   56: invokeinterface 571 4 0
    //   61: astore 9
    //   63: aconst_null
    //   64: astore 10
    //   66: aload_1
    //   67: invokeinterface 610 1 0
    //   72: astore 11
    //   74: aload 11
    //   76: invokeinterface 589 1 0
    //   81: istore 12
    //   83: iload 12
    //   85: anewarray 258	java/lang/String
    //   88: astore 13
    //   90: iconst_0
    //   91: istore 14
    //   93: aload 9
    //   95: invokeinterface 583 1 0
    //   100: ifeq +23 -> 123
    //   103: aload 13
    //   105: iload 14
    //   107: aload 9
    //   109: ldc 12
    //   111: invokeinterface 588 2 0
    //   116: aastore
    //   117: iinc 14 1
    //   120: goto -27 -> 93
    //   123: aload 7
    //   125: invokeinterface 583 1 0
    //   130: ifeq +183 -> 313
    //   133: aload 13
    //   135: astore 15
    //   137: aload 15
    //   139: arraylength
    //   140: istore 16
    //   142: iconst_0
    //   143: istore 17
    //   145: iload 17
    //   147: iload 16
    //   149: if_icmpge +164 -> 313
    //   152: aload 15
    //   154: iload 17
    //   156: aaload
    //   157: astore 18
    //   159: aload_0
    //   160: aload 18
    //   162: aload 11
    //   164: invokespecial 505	com/sun/rowset/internal/CachedRowSetWriter:isPKNameValid	(Ljava/lang/String;Ljava/sql/ResultSetMetaData;)Z
    //   167: ifne +6 -> 173
    //   170: goto +137 -> 307
    //   173: aload_1
    //   174: aload 18
    //   176: invokeinterface 612 2 0
    //   181: astore 19
    //   183: aload 19
    //   185: ifnonnull +6 -> 191
    //   188: goto +125 -> 313
    //   191: aload 7
    //   193: aload 18
    //   195: invokeinterface 587 2 0
    //   200: invokevirtual 518	java/lang/Object:toString	()Ljava/lang/String;
    //   203: astore 20
    //   205: aload 19
    //   207: invokevirtual 518	java/lang/Object:toString	()Ljava/lang/String;
    //   210: aload 20
    //   212: invokevirtual 521	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   215: ifeq +92 -> 307
    //   218: iconst_1
    //   219: istore 4
    //   221: aload_0
    //   222: getfield 466	com/sun/rowset/internal/CachedRowSetWriter:crsResolve	Lcom/sun/rowset/CachedRowSetImpl;
    //   225: invokevirtual 484	com/sun/rowset/CachedRowSetImpl:moveToInsertRow	()V
    //   228: iconst_1
    //   229: istore 21
    //   231: iload 21
    //   233: iload 12
    //   235: if_icmpgt +58 -> 293
    //   238: aload 7
    //   240: invokeinterface 586 1 0
    //   245: iload 21
    //   247: invokeinterface 593 2 0
    //   252: astore 22
    //   254: aload 22
    //   256: aload 18
    //   258: invokevirtual 521	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   261: ifeq +17 -> 278
    //   264: aload_0
    //   265: getfield 466	com/sun/rowset/internal/CachedRowSetWriter:crsResolve	Lcom/sun/rowset/CachedRowSetImpl;
    //   268: iload 21
    //   270: aload 20
    //   272: invokevirtual 492	com/sun/rowset/CachedRowSetImpl:updateObject	(ILjava/lang/Object;)V
    //   275: goto +12 -> 287
    //   278: aload_0
    //   279: getfield 466	com/sun/rowset/internal/CachedRowSetWriter:crsResolve	Lcom/sun/rowset/CachedRowSetImpl;
    //   282: iload 21
    //   284: invokevirtual 490	com/sun/rowset/CachedRowSetImpl:updateNull	(I)V
    //   287: iinc 21 1
    //   290: goto -59 -> 231
    //   293: aload_0
    //   294: getfield 466	com/sun/rowset/internal/CachedRowSetWriter:crsResolve	Lcom/sun/rowset/CachedRowSetImpl;
    //   297: invokevirtual 482	com/sun/rowset/CachedRowSetImpl:insertRow	()V
    //   300: aload_0
    //   301: getfield 466	com/sun/rowset/internal/CachedRowSetWriter:crsResolve	Lcom/sun/rowset/CachedRowSetImpl;
    //   304: invokevirtual 483	com/sun/rowset/CachedRowSetImpl:moveToCurrentRow	()V
    //   307: iinc 17 1
    //   310: goto -165 -> 145
    //   313: iload 4
    //   315: ifeq +127 -> 442
    //   318: iload 4
    //   320: istore 15
    //   322: aload 9
    //   324: ifnull +37 -> 361
    //   327: aload 10
    //   329: ifnull +25 -> 354
    //   332: aload 9
    //   334: invokeinterface 581 1 0
    //   339: goto +22 -> 361
    //   342: astore 16
    //   344: aload 10
    //   346: aload 16
    //   348: invokevirtual 531	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   351: goto +10 -> 361
    //   354: aload 9
    //   356: invokeinterface 581 1 0
    //   361: aload 7
    //   363: ifnull +37 -> 400
    //   366: aload 8
    //   368: ifnull +25 -> 393
    //   371: aload 7
    //   373: invokeinterface 581 1 0
    //   378: goto +22 -> 400
    //   381: astore 16
    //   383: aload 8
    //   385: aload 16
    //   387: invokevirtual 531	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   390: goto +10 -> 400
    //   393: aload 7
    //   395: invokeinterface 581 1 0
    //   400: aload 5
    //   402: ifnull +37 -> 439
    //   405: aload 6
    //   407: ifnull +25 -> 432
    //   410: aload 5
    //   412: invokeinterface 573 1 0
    //   417: goto +22 -> 439
    //   420: astore 16
    //   422: aload 6
    //   424: aload 16
    //   426: invokevirtual 531	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   429: goto +10 -> 439
    //   432: aload 5
    //   434: invokeinterface 573 1 0
    //   439: iload 15
    //   441: ireturn
    //   442: iconst_1
    //   443: istore 15
    //   445: iload 15
    //   447: iload 12
    //   449: if_icmpgt +58 -> 507
    //   452: aload_1
    //   453: iload 15
    //   455: invokeinterface 607 2 0
    //   460: astore 16
    //   462: aload 16
    //   464: ifnull +16 -> 480
    //   467: aload_2
    //   468: iload 15
    //   470: aload 16
    //   472: invokeinterface 579 3 0
    //   477: goto +24 -> 501
    //   480: aload_2
    //   481: iload 15
    //   483: aload_1
    //   484: invokeinterface 610 1 0
    //   489: iload 15
    //   491: invokeinterface 590 2 0
    //   496: invokeinterface 577 3 0
    //   501: iinc 15 1
    //   504: goto -59 -> 445
    //   507: aload_2
    //   508: invokeinterface 572 1 0
    //   513: pop
    //   514: iconst_0
    //   515: istore 15
    //   517: aload 9
    //   519: ifnull +37 -> 556
    //   522: aload 10
    //   524: ifnull +25 -> 549
    //   527: aload 9
    //   529: invokeinterface 581 1 0
    //   534: goto +22 -> 556
    //   537: astore 16
    //   539: aload 10
    //   541: aload 16
    //   543: invokevirtual 531	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   546: goto +10 -> 556
    //   549: aload 9
    //   551: invokeinterface 581 1 0
    //   556: aload 7
    //   558: ifnull +37 -> 595
    //   561: aload 8
    //   563: ifnull +25 -> 588
    //   566: aload 7
    //   568: invokeinterface 581 1 0
    //   573: goto +22 -> 595
    //   576: astore 16
    //   578: aload 8
    //   580: aload 16
    //   582: invokevirtual 531	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   585: goto +10 -> 595
    //   588: aload 7
    //   590: invokeinterface 581 1 0
    //   595: aload 5
    //   597: ifnull +37 -> 634
    //   600: aload 6
    //   602: ifnull +25 -> 627
    //   605: aload 5
    //   607: invokeinterface 573 1 0
    //   612: goto +22 -> 634
    //   615: astore 16
    //   617: aload 6
    //   619: aload 16
    //   621: invokevirtual 531	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   624: goto +10 -> 634
    //   627: aload 5
    //   629: invokeinterface 573 1 0
    //   634: iload 15
    //   636: ireturn
    //   637: astore 15
    //   639: aload_0
    //   640: getfield 466	com/sun/rowset/internal/CachedRowSetWriter:crsResolve	Lcom/sun/rowset/CachedRowSetImpl;
    //   643: invokevirtual 484	com/sun/rowset/CachedRowSetImpl:moveToInsertRow	()V
    //   646: iconst_1
    //   647: istore 16
    //   649: iload 16
    //   651: iload 12
    //   653: if_icmpgt +18 -> 671
    //   656: aload_0
    //   657: getfield 466	com/sun/rowset/internal/CachedRowSetWriter:crsResolve	Lcom/sun/rowset/CachedRowSetImpl;
    //   660: iload 16
    //   662: invokevirtual 490	com/sun/rowset/CachedRowSetImpl:updateNull	(I)V
    //   665: iinc 16 1
    //   668: goto -19 -> 649
    //   671: aload_0
    //   672: getfield 466	com/sun/rowset/internal/CachedRowSetWriter:crsResolve	Lcom/sun/rowset/CachedRowSetImpl;
    //   675: invokevirtual 482	com/sun/rowset/CachedRowSetImpl:insertRow	()V
    //   678: aload_0
    //   679: getfield 466	com/sun/rowset/internal/CachedRowSetWriter:crsResolve	Lcom/sun/rowset/CachedRowSetImpl;
    //   682: invokevirtual 483	com/sun/rowset/CachedRowSetImpl:moveToCurrentRow	()V
    //   685: iconst_1
    //   686: istore 16
    //   688: aload 9
    //   690: ifnull +37 -> 727
    //   693: aload 10
    //   695: ifnull +25 -> 720
    //   698: aload 9
    //   700: invokeinterface 581 1 0
    //   705: goto +22 -> 727
    //   708: astore 17
    //   710: aload 10
    //   712: aload 17
    //   714: invokevirtual 531	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   717: goto +10 -> 727
    //   720: aload 9
    //   722: invokeinterface 581 1 0
    //   727: aload 7
    //   729: ifnull +37 -> 766
    //   732: aload 8
    //   734: ifnull +25 -> 759
    //   737: aload 7
    //   739: invokeinterface 581 1 0
    //   744: goto +22 -> 766
    //   747: astore 17
    //   749: aload 8
    //   751: aload 17
    //   753: invokevirtual 531	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   756: goto +10 -> 766
    //   759: aload 7
    //   761: invokeinterface 581 1 0
    //   766: aload 5
    //   768: ifnull +37 -> 805
    //   771: aload 6
    //   773: ifnull +25 -> 798
    //   776: aload 5
    //   778: invokeinterface 573 1 0
    //   783: goto +22 -> 805
    //   786: astore 17
    //   788: aload 6
    //   790: aload 17
    //   792: invokevirtual 531	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   795: goto +10 -> 805
    //   798: aload 5
    //   800: invokeinterface 573 1 0
    //   805: iload 16
    //   807: ireturn
    //   808: astore 11
    //   810: aload 11
    //   812: astore 10
    //   814: aload 11
    //   816: athrow
    //   817: astore 23
    //   819: aload 9
    //   821: ifnull +37 -> 858
    //   824: aload 10
    //   826: ifnull +25 -> 851
    //   829: aload 9
    //   831: invokeinterface 581 1 0
    //   836: goto +22 -> 858
    //   839: astore 24
    //   841: aload 10
    //   843: aload 24
    //   845: invokevirtual 531	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   848: goto +10 -> 858
    //   851: aload 9
    //   853: invokeinterface 581 1 0
    //   858: aload 23
    //   860: athrow
    //   861: astore 9
    //   863: aload 9
    //   865: astore 8
    //   867: aload 9
    //   869: athrow
    //   870: astore 25
    //   872: aload 7
    //   874: ifnull +37 -> 911
    //   877: aload 8
    //   879: ifnull +25 -> 904
    //   882: aload 7
    //   884: invokeinterface 581 1 0
    //   889: goto +22 -> 911
    //   892: astore 26
    //   894: aload 8
    //   896: aload 26
    //   898: invokevirtual 531	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   901: goto +10 -> 911
    //   904: aload 7
    //   906: invokeinterface 581 1 0
    //   911: aload 25
    //   913: athrow
    //   914: astore 7
    //   916: aload 7
    //   918: astore 6
    //   920: aload 7
    //   922: athrow
    //   923: astore 27
    //   925: aload 5
    //   927: ifnull +37 -> 964
    //   930: aload 6
    //   932: ifnull +25 -> 957
    //   935: aload 5
    //   937: invokeinterface 573 1 0
    //   942: goto +22 -> 964
    //   945: astore 28
    //   947: aload 6
    //   949: aload 28
    //   951: invokevirtual 531	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   954: goto +10 -> 964
    //   957: aload 5
    //   959: invokeinterface 573 1 0
    //   964: aload 27
    //   966: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	967	0	this	CachedRowSetWriter
    //   0	967	1	paramCachedRowSet	CachedRowSet
    //   0	967	2	paramPreparedStatement	PreparedStatement
    //   0	967	3	paramCachedRowSetImpl	CachedRowSetImpl
    //   1	318	4	arrayOfString1	String[]
    //   22	936	5	localPreparedStatement	PreparedStatement
    //   25	923	6	localObject1	Object
    //   34	871	7	localResultSet1	ResultSet
    //   914	7	7	localThrowable1	Throwable
    //   37	858	8	localObject2	Object
    //   61	791	9	localResultSet2	ResultSet
    //   861	7	9	localThrowable2	Throwable
    //   64	778	10	localObject3	Object
    //   72	91	11	localResultSetMetaData	ResultSetMetaData
    //   808	7	11	localThrowable3	Throwable
    //   81	573	12	i	int
    //   88	46	13	arrayOfString2	String[]
    //   91	27	14	j	int
    //   135	305	15	arrayOfString3	String[]
    //   443	192	15	k	int
    //   637	1	15	localSQLException	SQLException
    //   140	10	16	m	int
    //   342	5	16	localThrowable4	Throwable
    //   381	5	16	localThrowable5	Throwable
    //   420	5	16	localThrowable6	Throwable
    //   460	11	16	localObject4	Object
    //   537	5	16	localThrowable7	Throwable
    //   576	5	16	localThrowable8	Throwable
    //   615	5	16	localThrowable9	Throwable
    //   647	159	16	n	int
    //   143	165	17	i1	int
    //   708	5	17	localThrowable10	Throwable
    //   747	5	17	localThrowable11	Throwable
    //   786	5	17	localThrowable12	Throwable
    //   157	100	18	str1	String
    //   181	25	19	localObject5	Object
    //   203	68	20	str2	String
    //   229	59	21	i2	int
    //   252	3	22	str3	String
    //   817	42	23	localObject6	Object
    //   839	5	24	localThrowable13	Throwable
    //   870	42	25	localObject7	Object
    //   892	5	26	localThrowable14	Throwable
    //   923	42	27	localObject8	Object
    //   945	5	28	localThrowable15	Throwable
    // Exception table:
    //   from	to	target	type
    //   332	339	342	java/lang/Throwable
    //   371	378	381	java/lang/Throwable
    //   410	417	420	java/lang/Throwable
    //   527	534	537	java/lang/Throwable
    //   566	573	576	java/lang/Throwable
    //   605	612	615	java/lang/Throwable
    //   442	517	637	java/sql/SQLException
    //   698	705	708	java/lang/Throwable
    //   737	744	747	java/lang/Throwable
    //   776	783	786	java/lang/Throwable
    //   66	322	808	java/lang/Throwable
    //   442	517	808	java/lang/Throwable
    //   637	688	808	java/lang/Throwable
    //   66	322	817	finally
    //   442	517	817	finally
    //   637	688	817	finally
    //   808	819	817	finally
    //   829	836	839	java/lang/Throwable
    //   39	361	861	java/lang/Throwable
    //   442	556	861	java/lang/Throwable
    //   637	727	861	java/lang/Throwable
    //   808	861	861	java/lang/Throwable
    //   39	361	870	finally
    //   442	556	870	finally
    //   637	727	870	finally
    //   808	872	870	finally
    //   882	889	892	java/lang/Throwable
    //   27	400	914	java/lang/Throwable
    //   442	595	914	java/lang/Throwable
    //   637	766	914	java/lang/Throwable
    //   808	914	914	java/lang/Throwable
    //   27	400	923	finally
    //   442	595	923	finally
    //   637	766	923	finally
    //   808	925	923	finally
    //   935	942	945	java/lang/Throwable
  }
  
  private boolean deleteOriginalRow(CachedRowSet paramCachedRowSet, CachedRowSetImpl paramCachedRowSetImpl)
    throws SQLException
  {
    int j = 0;
    ResultSet localResultSet1 = paramCachedRowSet.getOriginalRow();
    localResultSet1.next();
    this.deleteWhere = buildWhereClause(this.deleteWhere, localResultSet1);
    PreparedStatement localPreparedStatement = this.con.prepareStatement(this.selectCmd + this.deleteWhere, 1005, 1007);
    for (int i = 0; i < this.keyCols.length; i++) {
      if (this.params[i] != null) {
        localPreparedStatement.setObject(++j, this.params[i]);
      }
    }
    try
    {
      localPreparedStatement.setMaxRows(paramCachedRowSet.getMaxRows());
      localPreparedStatement.setMaxFieldSize(paramCachedRowSet.getMaxFieldSize());
      localPreparedStatement.setEscapeProcessing(paramCachedRowSet.getEscapeProcessing());
      localPreparedStatement.setQueryTimeout(paramCachedRowSet.getQueryTimeout());
    }
    catch (Exception localException) {}
    ResultSet localResultSet2 = localPreparedStatement.executeQuery();
    if (localResultSet2.next() == true)
    {
      if (localResultSet2.next()) {
        return true;
      }
      localResultSet2.first();
      int k = 0;
      paramCachedRowSetImpl.moveToInsertRow();
      for (i = 1; i <= paramCachedRowSet.getMetaData().getColumnCount(); i++)
      {
        localObject1 = localResultSet1.getObject(i);
        Object localObject2 = localResultSet2.getObject(i);
        if ((localObject1 != null) && (localObject2 != null))
        {
          if (!localObject1.toString().equals(localObject2.toString()))
          {
            k = 1;
            paramCachedRowSetImpl.updateObject(i, localResultSet1.getObject(i));
          }
        }
        else {
          paramCachedRowSetImpl.updateNull(i);
        }
      }
      paramCachedRowSetImpl.insertRow();
      paramCachedRowSetImpl.moveToCurrentRow();
      if (k != 0) {
        return true;
      }
      Object localObject1 = this.deleteCmd + this.deleteWhere;
      localPreparedStatement = this.con.prepareStatement((String)localObject1);
      j = 0;
      for (i = 0; i < this.keyCols.length; i++) {
        if (this.params[i] != null) {
          localPreparedStatement.setObject(++j, this.params[i]);
        }
      }
      if (localPreparedStatement.executeUpdate() != 1) {
        return true;
      }
      localPreparedStatement.close();
    }
    else
    {
      return true;
    }
    return false;
  }
  
  public void setReader(CachedRowSetReader paramCachedRowSetReader)
    throws SQLException
  {
    this.reader = paramCachedRowSetReader;
  }
  
  public CachedRowSetReader getReader()
    throws SQLException
  {
    return this.reader;
  }
  
  private void initSQLStatements(CachedRowSet paramCachedRowSet)
    throws SQLException
  {
    this.callerMd = paramCachedRowSet.getMetaData();
    this.callerColumnCount = this.callerMd.getColumnCount();
    if (this.callerColumnCount < 1) {
      return;
    }
    String str1 = paramCachedRowSet.getTableName();
    if (str1 == null)
    {
      str1 = this.callerMd.getTableName(1);
      if ((str1 == null) || (str1.length() == 0)) {
        throw new SQLException(this.resBundle.handleGetObject("crswriter.tname").toString());
      }
    }
    String str2 = this.callerMd.getCatalogName(1);
    String str3 = this.callerMd.getSchemaName(1);
    DatabaseMetaData localDatabaseMetaData = this.con.getMetaData();
    this.selectCmd = "SELECT ";
    for (int i = 1; i <= this.callerColumnCount; i++)
    {
      this.selectCmd += this.callerMd.getColumnName(i);
      if (i < this.callerMd.getColumnCount()) {
        this.selectCmd += ", ";
      } else {
        this.selectCmd += " ";
      }
    }
    this.selectCmd = (this.selectCmd + "FROM " + buildTableName(localDatabaseMetaData, str2, str3, str1));
    this.updateCmd = ("UPDATE " + buildTableName(localDatabaseMetaData, str2, str3, str1));
    String str4 = this.updateCmd.toLowerCase();
    int j = str4.indexOf("where");
    if (j != -1) {
      this.updateCmd = this.updateCmd.substring(0, j);
    }
    this.updateCmd += "SET ";
    this.insertCmd = ("INSERT INTO " + buildTableName(localDatabaseMetaData, str2, str3, str1));
    this.insertCmd += "(";
    for (i = 1; i <= this.callerColumnCount; i++)
    {
      this.insertCmd += this.callerMd.getColumnName(i);
      if (i < this.callerMd.getColumnCount()) {
        this.insertCmd += ", ";
      } else {
        this.insertCmd += ") VALUES (";
      }
    }
    for (i = 1; i <= this.callerColumnCount; i++)
    {
      this.insertCmd += "?";
      if (i < this.callerColumnCount) {
        this.insertCmd += ", ";
      } else {
        this.insertCmd += ")";
      }
    }
    this.deleteCmd = ("DELETE FROM " + buildTableName(localDatabaseMetaData, str2, str3, str1));
    buildKeyDesc(paramCachedRowSet);
  }
  
  private String buildTableName(DatabaseMetaData paramDatabaseMetaData, String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    String str = "";
    paramString1 = paramString1.trim();
    paramString2 = paramString2.trim();
    paramString3 = paramString3.trim();
    if (paramDatabaseMetaData.isCatalogAtStart() == true)
    {
      if ((paramString1 != null) && (paramString1.length() > 0)) {
        str = str + paramString1 + paramDatabaseMetaData.getCatalogSeparator();
      }
      if ((paramString2 != null) && (paramString2.length() > 0)) {
        str = str + paramString2 + ".";
      }
      str = str + paramString3;
    }
    else
    {
      if ((paramString2 != null) && (paramString2.length() > 0)) {
        str = str + paramString2 + ".";
      }
      str = str + paramString3;
      if ((paramString1 != null) && (paramString1.length() > 0)) {
        str = str + paramDatabaseMetaData.getCatalogSeparator() + paramString1;
      }
    }
    str = str + " ";
    return str;
  }
  
  private void buildKeyDesc(CachedRowSet paramCachedRowSet)
    throws SQLException
  {
    this.keyCols = paramCachedRowSet.getKeyColumns();
    ResultSetMetaData localResultSetMetaData = paramCachedRowSet.getMetaData();
    if ((this.keyCols == null) || (this.keyCols.length == 0))
    {
      ArrayList localArrayList = new ArrayList();
      for (int i = 0; i < this.callerColumnCount; i++) {
        if ((localResultSetMetaData.getColumnType(i + 1) != 2005) && (localResultSetMetaData.getColumnType(i + 1) != 2002) && (localResultSetMetaData.getColumnType(i + 1) != 2009) && (localResultSetMetaData.getColumnType(i + 1) != 2004) && (localResultSetMetaData.getColumnType(i + 1) != 2003) && (localResultSetMetaData.getColumnType(i + 1) != 1111)) {
          localArrayList.add(Integer.valueOf(i + 1));
        }
      }
      this.keyCols = new int[localArrayList.size()];
      for (i = 0; i < localArrayList.size(); i++) {
        this.keyCols[i] = ((Integer)localArrayList.get(i)).intValue();
      }
    }
    this.params = new Object[this.keyCols.length];
  }
  
  private String buildWhereClause(String paramString, ResultSet paramResultSet)
    throws SQLException
  {
    paramString = "WHERE ";
    for (int i = 0; i < this.keyCols.length; i++)
    {
      if (i > 0) {
        paramString = paramString + "AND ";
      }
      paramString = paramString + this.callerMd.getColumnName(this.keyCols[i]);
      this.params[i] = paramResultSet.getObject(this.keyCols[i]);
      if (paramResultSet.wasNull() == true) {
        paramString = paramString + " IS NULL ";
      } else {
        paramString = paramString + " = ? ";
      }
    }
    return paramString;
  }
  
  void updateResolvedConflictToDB(CachedRowSet paramCachedRowSet, Connection paramConnection)
    throws SQLException
  {
    String str1 = "WHERE ";
    String str2 = " ";
    String str3 = "UPDATE ";
    int i = paramCachedRowSet.getMetaData().getColumnCount();
    int[] arrayOfInt = paramCachedRowSet.getKeyColumns();
    String str4 = "";
    str1 = buildWhereClause(str1, paramCachedRowSet);
    if ((arrayOfInt == null) || (arrayOfInt.length == 0))
    {
      arrayOfInt = new int[i];
      j = 0;
      while (j < arrayOfInt.length) {
        arrayOfInt[(j++)] = j;
      }
    }
    Object[] arrayOfObject = new Object[arrayOfInt.length];
    str3 = "UPDATE " + buildTableName(paramConnection.getMetaData(), paramCachedRowSet.getMetaData().getCatalogName(1), paramCachedRowSet.getMetaData().getSchemaName(1), paramCachedRowSet.getTableName());
    str3 = str3 + "SET ";
    int j = 1;
    for (int k = 1; k <= i; k++) {
      if (paramCachedRowSet.columnUpdated(k))
      {
        if (j == 0) {
          str4 = str4 + ", ";
        }
        str4 = str4 + paramCachedRowSet.getMetaData().getColumnName(k);
        str4 = str4 + " = ? ";
        j = 0;
      }
    }
    str3 = str3 + str4;
    str1 = "WHERE ";
    for (k = 0; k < arrayOfInt.length; k++)
    {
      if (k > 0) {
        str1 = str1 + "AND ";
      }
      str1 = str1 + paramCachedRowSet.getMetaData().getColumnName(arrayOfInt[k]);
      arrayOfObject[k] = paramCachedRowSet.getObject(arrayOfInt[k]);
      if (paramCachedRowSet.wasNull() == true) {
        str1 = str1 + " IS NULL ";
      } else {
        str1 = str1 + " = ? ";
      }
    }
    str3 = str3 + str1;
    PreparedStatement localPreparedStatement = paramConnection.prepareStatement(str3);
    k = 0;
    for (int m = 0; m < i; m++) {
      if (paramCachedRowSet.columnUpdated(m + 1))
      {
        Object localObject = paramCachedRowSet.getObject(m + 1);
        if (localObject != null) {
          localPreparedStatement.setObject(++k, localObject);
        } else {
          localPreparedStatement.setNull(m + 1, paramCachedRowSet.getMetaData().getColumnType(m + 1));
        }
      }
    }
    for (m = 0; m < arrayOfInt.length; m++) {
      if (arrayOfObject[m] != null) {
        localPreparedStatement.setObject(++k, arrayOfObject[m]);
      }
    }
    m = localPreparedStatement.executeUpdate();
  }
  
  public void commit()
    throws SQLException
  {
    this.con.commit();
    if (this.reader.getCloseConnection() == true) {
      this.con.close();
    }
  }
  
  public void commit(CachedRowSetImpl paramCachedRowSetImpl, boolean paramBoolean)
    throws SQLException
  {
    this.con.commit();
    if ((paramBoolean) && (paramCachedRowSetImpl.getCommand() != null)) {
      paramCachedRowSetImpl.execute(this.con);
    }
    if (this.reader.getCloseConnection() == true) {
      this.con.close();
    }
  }
  
  public void rollback()
    throws SQLException
  {
    this.con.rollback();
    if (this.reader.getCloseConnection() == true) {
      this.con.close();
    }
  }
  
  public void rollback(Savepoint paramSavepoint)
    throws SQLException
  {
    this.con.rollback(paramSavepoint);
    if (this.reader.getCloseConnection() == true) {
      this.con.close();
    }
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
  
  private boolean isPKNameValid(String paramString, ResultSetMetaData paramResultSetMetaData)
    throws SQLException
  {
    boolean bool = false;
    int i = paramResultSetMetaData.getColumnCount();
    for (int j = 1; j <= i; j++)
    {
      String str = paramResultSetMetaData.getColumnClassName(j);
      if (str.equalsIgnoreCase(paramString))
      {
        bool = true;
        break;
      }
    }
    return bool;
  }
}
