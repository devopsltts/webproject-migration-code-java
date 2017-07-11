package com.sun.corba.se.spi.logging;

import java.util.logging.Logger;

public abstract interface LogWrapperFactory
{
  public abstract LogWrapperBase create(Logger paramLogger);
}
