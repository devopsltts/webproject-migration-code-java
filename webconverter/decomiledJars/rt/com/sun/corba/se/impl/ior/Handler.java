package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;

abstract interface Handler
{
  public abstract ObjectKeyTemplate handle(int paramInt1, int paramInt2, InputStream paramInputStream, OctetSeqHolder paramOctetSeqHolder);
}
