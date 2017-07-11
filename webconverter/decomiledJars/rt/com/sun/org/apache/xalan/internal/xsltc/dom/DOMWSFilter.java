package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import java.util.HashMap;
import java.util.Map;

public class DOMWSFilter
  implements DTMWSFilter
{
  private AbstractTranslet m_translet;
  private StripFilter m_filter;
  private Map<DTM, short[]> m_mappings;
  private DTM m_currentDTM;
  private short[] m_currentMapping;
  
  public DOMWSFilter(AbstractTranslet paramAbstractTranslet)
  {
    this.m_translet = paramAbstractTranslet;
    this.m_mappings = new HashMap();
    if ((paramAbstractTranslet instanceof StripFilter)) {
      this.m_filter = ((StripFilter)paramAbstractTranslet);
    }
  }
  
  public short getShouldStripSpace(int paramInt, DTM paramDTM)
  {
    if ((this.m_filter != null) && ((paramDTM instanceof DOM)))
    {
      DOM localDOM = (DOM)paramDTM;
      int i = 0;
      if ((paramDTM instanceof DOMEnhancedForDTM))
      {
        DOMEnhancedForDTM localDOMEnhancedForDTM = (DOMEnhancedForDTM)paramDTM;
        short[] arrayOfShort;
        if (paramDTM == this.m_currentDTM)
        {
          arrayOfShort = this.m_currentMapping;
        }
        else
        {
          arrayOfShort = (short[])this.m_mappings.get(paramDTM);
          if (arrayOfShort == null)
          {
            arrayOfShort = localDOMEnhancedForDTM.getMapping(this.m_translet.getNamesArray(), this.m_translet.getUrisArray(), this.m_translet.getTypesArray());
            this.m_mappings.put(paramDTM, arrayOfShort);
            this.m_currentDTM = paramDTM;
            this.m_currentMapping = arrayOfShort;
          }
        }
        int j = localDOMEnhancedForDTM.getExpandedTypeID(paramInt);
        if ((j >= 0) && (j < arrayOfShort.length)) {
          i = arrayOfShort[j];
        } else {
          i = -1;
        }
      }
      else
      {
        return 3;
      }
      if (this.m_filter.stripSpace(localDOM, paramInt, i)) {
        return 2;
      }
      return 1;
    }
    return 1;
  }
}
