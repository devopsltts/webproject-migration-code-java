package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.util.ArrayList;
import java.util.List;

public class SlotTableStack
{
  private List tableContainer;
  private int currentIndex;
  private SlotTablePool tablePool;
  private ORB orb;
  private InterceptorsSystemException wrapper;
  
  SlotTableStack(ORB paramORB, SlotTable paramSlotTable)
  {
    this.orb = paramORB;
    this.wrapper = InterceptorsSystemException.get(paramORB, "rpc.protocol");
    this.currentIndex = 0;
    this.tableContainer = new ArrayList();
    this.tablePool = new SlotTablePool();
    this.tableContainer.add(this.currentIndex, paramSlotTable);
    this.currentIndex += 1;
  }
  
  void pushSlotTable()
  {
    SlotTable localSlotTable1 = this.tablePool.getSlotTable();
    if (localSlotTable1 == null)
    {
      SlotTable localSlotTable2 = peekSlotTable();
      localSlotTable1 = new SlotTable(this.orb, localSlotTable2.getSize());
    }
    if (this.currentIndex == this.tableContainer.size())
    {
      this.tableContainer.add(this.currentIndex, localSlotTable1);
    }
    else
    {
      if (this.currentIndex > this.tableContainer.size()) {
        throw this.wrapper.slotTableInvariant(new Integer(this.currentIndex), new Integer(this.tableContainer.size()));
      }
      this.tableContainer.set(this.currentIndex, localSlotTable1);
    }
    this.currentIndex += 1;
  }
  
  void popSlotTable()
  {
    if (this.currentIndex <= 1) {
      throw this.wrapper.cantPopOnlyPicurrent();
    }
    this.currentIndex -= 1;
    SlotTable localSlotTable = (SlotTable)this.tableContainer.get(this.currentIndex);
    this.tableContainer.set(this.currentIndex, null);
    localSlotTable.resetSlots();
    this.tablePool.putSlotTable(localSlotTable);
  }
  
  SlotTable peekSlotTable()
  {
    return (SlotTable)this.tableContainer.get(this.currentIndex - 1);
  }
  
  private class SlotTablePool
  {
    private SlotTable[] pool = new SlotTable[5];
    private final int HIGH_WATER_MARK = 5;
    private int currentIndex = 0;
    
    SlotTablePool() {}
    
    void putSlotTable(SlotTable paramSlotTable)
    {
      if (this.currentIndex >= 5) {
        return;
      }
      this.pool[this.currentIndex] = paramSlotTable;
      this.currentIndex += 1;
    }
    
    SlotTable getSlotTable()
    {
      if (this.currentIndex == 0) {
        return null;
      }
      this.currentIndex -= 1;
      return this.pool[this.currentIndex];
    }
  }
}
