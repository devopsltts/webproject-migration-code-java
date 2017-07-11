package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.model.ParameterImpl;
import javax.jws.WebParam.Mode;

abstract class ValueGetterFactory
{
  static final ValueGetterFactory SYNC = new ValueGetterFactory()
  {
    ValueGetter get(ParameterImpl paramAnonymousParameterImpl)
    {
      return (paramAnonymousParameterImpl.getMode() == WebParam.Mode.IN) || (paramAnonymousParameterImpl.getIndex() == -1) ? ValueGetter.PLAIN : ValueGetter.HOLDER;
    }
  };
  static final ValueGetterFactory ASYNC = new ValueGetterFactory()
  {
    ValueGetter get(ParameterImpl paramAnonymousParameterImpl)
    {
      return ValueGetter.PLAIN;
    }
  };
  
  ValueGetterFactory() {}
  
  abstract ValueGetter get(ParameterImpl paramParameterImpl);
}
