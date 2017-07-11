package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xml.internal.serializer.utils.Messages;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import java.util.Properties;
import org.xml.sax.ContentHandler;

public final class SerializerFactory
{
  private SerializerFactory() {}
  
  public static Serializer getSerializer(Properties paramProperties)
  {
    Object localObject1;
    try
    {
      String str1 = paramProperties.getProperty("method");
      if (str1 == null)
      {
        str2 = Utils.messages.createMessage("ER_FACTORY_PROPERTY_MISSING", new Object[] { "method" });
        throw new IllegalArgumentException(str2);
      }
      String str2 = paramProperties.getProperty("{http://xml.apache.org/xalan}content-handler");
      if (null == str2)
      {
        localObject2 = OutputPropertiesFactory.getDefaultMethodProperties(str1);
        str2 = ((Properties)localObject2).getProperty("{http://xml.apache.org/xalan}content-handler");
        if (null == str2)
        {
          localObject3 = Utils.messages.createMessage("ER_FACTORY_PROPERTY_MISSING", new Object[] { "{http://xml.apache.org/xalan}content-handler" });
          throw new IllegalArgumentException((String)localObject3);
        }
      }
      Object localObject2 = ObjectFactory.findProviderClass(str2, true);
      Object localObject3 = ((Class)localObject2).newInstance();
      if ((localObject3 instanceof SerializationHandler))
      {
        localObject1 = (Serializer)((Class)localObject2).newInstance();
        ((Serializer)localObject1).setOutputFormat(paramProperties);
      }
      else if ((localObject3 instanceof ContentHandler))
      {
        str2 = "com.sun.org.apache.xml.internal.serializer.ToXMLSAXHandler";
        localObject2 = ObjectFactory.findProviderClass(str2, true);
        SerializationHandler localSerializationHandler = (SerializationHandler)((Class)localObject2).newInstance();
        localSerializationHandler.setContentHandler((ContentHandler)localObject3);
        localSerializationHandler.setOutputFormat(paramProperties);
        localObject1 = localSerializationHandler;
      }
      else
      {
        throw new Exception(Utils.messages.createMessage("ER_SERIALIZER_NOT_CONTENTHANDLER", new Object[] { str2 }));
      }
    }
    catch (Exception localException)
    {
      throw new WrappedRuntimeException(localException);
    }
    return localObject1;
  }
}
