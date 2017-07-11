package com.sun.org.apache.xerces.internal.impl.dtd.models;

import com.sun.org.apache.xerces.internal.xni.QName;

public class MixedContentModel
  implements ContentModelValidator
{
  private int fCount;
  private QName[] fChildren;
  private int[] fChildrenType;
  private boolean fOrdered;
  
  public MixedContentModel(QName[] paramArrayOfQName, int[] paramArrayOfInt, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.fCount = paramInt2;
    this.fChildren = new QName[this.fCount];
    this.fChildrenType = new int[this.fCount];
    for (int i = 0; i < this.fCount; i++)
    {
      this.fChildren[i] = new QName(paramArrayOfQName[(paramInt1 + i)]);
      this.fChildrenType[i] = paramArrayOfInt[(paramInt1 + i)];
    }
    this.fOrdered = paramBoolean;
  }
  
  public int validate(QName[] paramArrayOfQName, int paramInt1, int paramInt2)
  {
    int i;
    int m;
    String str;
    if (this.fOrdered)
    {
      i = 0;
      for (int j = 0; j < paramInt2; j++)
      {
        QName localQName2 = paramArrayOfQName[(paramInt1 + j)];
        if (localQName2.localpart != null)
        {
          m = this.fChildrenType[i];
          if (m == 0)
          {
            if (this.fChildren[i].rawname != paramArrayOfQName[(paramInt1 + j)].rawname) {
              return j;
            }
          }
          else if (m == 6)
          {
            str = this.fChildren[i].uri;
            if ((str != null) && (str != paramArrayOfQName[j].uri)) {
              return j;
            }
          }
          else if (m == 8)
          {
            if (paramArrayOfQName[j].uri != null) {
              return j;
            }
          }
          else if ((m == 7) && (this.fChildren[i].uri == paramArrayOfQName[j].uri))
          {
            return j;
          }
          i++;
        }
      }
    }
    else
    {
      for (i = 0; i < paramInt2; i++)
      {
        QName localQName1 = paramArrayOfQName[(paramInt1 + i)];
        if (localQName1.localpart != null)
        {
          for (int k = 0; k < this.fCount; k++)
          {
            m = this.fChildrenType[k];
            if (m == 0)
            {
              if (localQName1.rawname == this.fChildren[k].rawname) {
                break;
              }
            }
            else if (m == 6)
            {
              str = this.fChildren[k].uri;
              if ((str == null) || (str == paramArrayOfQName[i].uri)) {
                break;
              }
            }
            else
            {
              if (m == 8 ? paramArrayOfQName[i].uri != null : (m == 7) && (this.fChildren[k].uri != paramArrayOfQName[i].uri)) {
                break;
              }
            }
          }
          if (k == this.fCount) {
            return i;
          }
        }
      }
    }
    return -1;
  }
}
