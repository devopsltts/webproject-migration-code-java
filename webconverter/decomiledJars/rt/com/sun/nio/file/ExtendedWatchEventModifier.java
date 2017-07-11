package com.sun.nio.file;

import java.nio.file.WatchEvent.Modifier;

public enum ExtendedWatchEventModifier
  implements WatchEvent.Modifier
{
  FILE_TREE;
  
  private ExtendedWatchEventModifier() {}
}
