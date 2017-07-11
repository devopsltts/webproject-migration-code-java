package com.sun.jmx.mbeanserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class Util
{
  public Util() {}
  
  public static ObjectName newObjectName(String paramString)
  {
    try
    {
      return new ObjectName(paramString);
    }
    catch (MalformedObjectNameException localMalformedObjectNameException)
    {
      throw new IllegalArgumentException(localMalformedObjectNameException);
    }
  }
  
  static <K, V> Map<K, V> newMap()
  {
    return new HashMap();
  }
  
  static <K, V> Map<K, V> newSynchronizedMap()
  {
    return Collections.synchronizedMap(newMap());
  }
  
  static <K, V> IdentityHashMap<K, V> newIdentityHashMap()
  {
    return new IdentityHashMap();
  }
  
  static <K, V> Map<K, V> newSynchronizedIdentityHashMap()
  {
    IdentityHashMap localIdentityHashMap = newIdentityHashMap();
    return Collections.synchronizedMap(localIdentityHashMap);
  }
  
  static <K, V> SortedMap<K, V> newSortedMap()
  {
    return new TreeMap();
  }
  
  static <K, V> SortedMap<K, V> newSortedMap(Comparator<? super K> paramComparator)
  {
    return new TreeMap(paramComparator);
  }
  
  static <K, V> Map<K, V> newInsertionOrderMap()
  {
    return new LinkedHashMap();
  }
  
  static <E> Set<E> newSet()
  {
    return new HashSet();
  }
  
  static <E> Set<E> newSet(Collection<E> paramCollection)
  {
    return new HashSet(paramCollection);
  }
  
  static <E> List<E> newList()
  {
    return new ArrayList();
  }
  
  static <E> List<E> newList(Collection<E> paramCollection)
  {
    return new ArrayList(paramCollection);
  }
  
  public static <T> T cast(Object paramObject)
  {
    return paramObject;
  }
  
  public static int hashCode(String[] paramArrayOfString, Object[] paramArrayOfObject)
  {
    int i = 0;
    for (int j = 0; j < paramArrayOfString.length; j++)
    {
      Object localObject = paramArrayOfObject[j];
      int k;
      if (localObject == null) {
        k = 0;
      } else if ((localObject instanceof Object[])) {
        k = Arrays.deepHashCode((Object[])localObject);
      } else if (localObject.getClass().isArray()) {
        k = Arrays.deepHashCode(new Object[] { localObject }) - 31;
      } else {
        k = localObject.hashCode();
      }
      i += (paramArrayOfString[j].toLowerCase().hashCode() ^ k);
    }
    return i;
  }
  
  private static boolean wildmatch(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int j;
    int i = j = -1;
    for (;;)
    {
      if (paramInt3 < paramInt4)
      {
        int k = paramString2.charAt(paramInt3);
        switch (k)
        {
        case 63: 
          if (paramInt1 != paramInt2)
          {
            paramInt1++;
            paramInt3++;
          }
          break;
        case 42: 
          paramInt3++;
          j = paramInt3;
          i = paramInt1;
          break;
        default: 
          if ((paramInt1 < paramInt2) && (paramString1.charAt(paramInt1) == k))
          {
            paramInt1++;
            paramInt3++;
            continue;
          }
          break;
        }
      }
      else
      {
        if (paramInt1 == paramInt2) {
          return true;
        }
        if ((j < 0) || (i == paramInt2)) {
          return false;
        }
        paramInt3 = j;
        i++;
        paramInt1 = i;
      }
    }
  }
  
  public static boolean wildmatch(String paramString1, String paramString2)
  {
    return wildmatch(paramString1, paramString2, 0, paramString1.length(), 0, paramString2.length());
  }
}
