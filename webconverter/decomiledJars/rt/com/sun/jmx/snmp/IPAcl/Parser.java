package com.sun.jmx.snmp.IPAcl;

import java.io.InputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Vector;

class Parser
  implements ParserTreeConstants, ParserConstants
{
  protected JJTParserState jjtree = new JJTParserState();
  public ParserTokenManager token_source;
  ASCII_CharStream jj_input_stream;
  public Token token;
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos;
  private Token jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  private final int[] jj_la1 = new int[22];
  private final int[] jj_la1_0 = { 256, 524288, 1048576, 8192, 0, 393216, 0, Integer.MIN_VALUE, 285212672, 0, 0, 0, 0, 8192, 8192, 0, -1862270976, 0, 32768, 8192, 0, -1862270976 };
  private final int[] jj_la1_1 = { 0, 0, 0, 0, 16, 0, 16, 0, 0, 32, 32, 64, 32, 0, 0, 16, 0, 16, 0, 0, 16, 0 };
  private final JJCalls[] jj_2_rtns = new JJCalls[3];
  private boolean jj_rescan = false;
  private int jj_gc = 0;
  private Vector<int[]> jj_expentries = new Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;
  
  public final JDMSecurityDefs SecurityDefs()
    throws ParseException
  {
    JDMSecurityDefs localJDMSecurityDefs1 = new JDMSecurityDefs(0);
    int i = 1;
    this.jjtree.openNodeScope(localJDMSecurityDefs1);
    try
    {
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 8: 
        AclBlock();
        break;
      default: 
        this.jj_la1[0] = this.jj_gen;
      }
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 19: 
        TrapBlock();
        break;
      default: 
        this.jj_la1[1] = this.jj_gen;
      }
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 20: 
        InformBlock();
        break;
      default: 
        this.jj_la1[2] = this.jj_gen;
      }
      jj_consume_token(0);
      this.jjtree.closeNodeScope(localJDMSecurityDefs1, true);
      i = 0;
      JDMSecurityDefs localJDMSecurityDefs2 = localJDMSecurityDefs1;
      return localJDMSecurityDefs2;
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        this.jjtree.clearNodeScope(localJDMSecurityDefs1);
        i = 0;
      }
      else
      {
        this.jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMSecurityDefs1, true);
      }
    }
  }
  
  public final void AclBlock()
    throws ParseException
  {
    JDMAclBlock localJDMAclBlock = new JDMAclBlock(1);
    int i = 1;
    this.jjtree.openNodeScope(localJDMAclBlock);
    try
    {
      jj_consume_token(8);
      jj_consume_token(9);
      jj_consume_token(13);
      for (;;)
      {
        AclItem();
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        }
      }
      this.jj_la1[3] = this.jj_gen;
      jj_consume_token(16);
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        this.jjtree.clearNodeScope(localJDMAclBlock);
        i = 0;
      }
      else
      {
        this.jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMAclBlock, true);
      }
    }
  }
  
  public final void AclItem()
    throws ParseException
  {
    JDMAclItem localJDMAclItem = new JDMAclItem(2);
    int i = 1;
    this.jjtree.openNodeScope(localJDMAclItem);
    try
    {
      jj_consume_token(13);
      localJDMAclItem.com = Communities();
      localJDMAclItem.access = Access();
      Managers();
      jj_consume_token(16);
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        this.jjtree.clearNodeScope(localJDMAclItem);
        i = 0;
      }
      else
      {
        this.jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMAclItem, true);
      }
    }
  }
  
  public final JDMCommunities Communities()
    throws ParseException
  {
    JDMCommunities localJDMCommunities1 = new JDMCommunities(3);
    int i = 1;
    this.jjtree.openNodeScope(localJDMCommunities1);
    try
    {
      jj_consume_token(10);
      jj_consume_token(9);
      Community();
      for (;;)
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 36: 
          break;
        default: 
          this.jj_la1[4] = this.jj_gen;
          break;
        }
        jj_consume_token(36);
        Community();
      }
      this.jjtree.closeNodeScope(localJDMCommunities1, true);
      i = 0;
      JDMCommunities localJDMCommunities2 = localJDMCommunities1;
      return localJDMCommunities2;
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        this.jjtree.clearNodeScope(localJDMCommunities1);
        i = 0;
      }
      else
      {
        this.jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMCommunities1, true);
      }
    }
  }
  
  public final void Community()
    throws ParseException
  {
    JDMCommunity localJDMCommunity = new JDMCommunity(4);
    int i = 1;
    this.jjtree.openNodeScope(localJDMCommunity);
    try
    {
      Token localToken = jj_consume_token(31);
      this.jjtree.closeNodeScope(localJDMCommunity, true);
      i = 0;
      localJDMCommunity.communityString = localToken.image;
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMCommunity, true);
      }
    }
  }
  
  public final JDMAccess Access()
    throws ParseException
  {
    JDMAccess localJDMAccess1 = new JDMAccess(5);
    int i = 1;
    this.jjtree.openNodeScope(localJDMAccess1);
    try
    {
      jj_consume_token(7);
      jj_consume_token(9);
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 17: 
        jj_consume_token(17);
        localJDMAccess1.access = 17;
        break;
      case 18: 
        jj_consume_token(18);
        localJDMAccess1.access = 18;
        break;
      default: 
        this.jj_la1[5] = this.jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      this.jjtree.closeNodeScope(localJDMAccess1, true);
      i = 0;
      JDMAccess localJDMAccess2 = localJDMAccess1;
      return localJDMAccess2;
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMAccess1, true);
      }
    }
  }
  
  public final void Managers()
    throws ParseException
  {
    JDMManagers localJDMManagers = new JDMManagers(6);
    int i = 1;
    this.jjtree.openNodeScope(localJDMManagers);
    try
    {
      jj_consume_token(14);
      jj_consume_token(9);
      Host();
      for (;;)
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 36: 
          break;
        default: 
          this.jj_la1[6] = this.jj_gen;
          break;
        }
        jj_consume_token(36);
        Host();
      }
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        this.jjtree.clearNodeScope(localJDMManagers);
        i = 0;
      }
      else
      {
        this.jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMManagers, true);
      }
    }
  }
  
  public final void Host()
    throws ParseException
  {
    JDMHost localJDMHost = new JDMHost(7);
    int i = 1;
    this.jjtree.openNodeScope(localJDMHost);
    try
    {
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 31: 
        HostName();
        break;
      default: 
        this.jj_la1[7] = this.jj_gen;
        if (jj_2_1(Integer.MAX_VALUE)) {
          NetMask();
        } else if (jj_2_2(Integer.MAX_VALUE)) {
          NetMaskV6();
        } else if (jj_2_3(Integer.MAX_VALUE)) {
          IpAddress();
        } else {
          switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
          {
          case 28: 
            IpV6Address();
            break;
          case 24: 
            IpMask();
            break;
          default: 
            this.jj_la1[8] = this.jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
        }
        break;
      }
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        this.jjtree.clearNodeScope(localJDMHost);
        i = 0;
      }
      else
      {
        this.jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMHost, true);
      }
    }
  }
  
  public final void HostName()
    throws ParseException
  {
    JDMHostName localJDMHostName = new JDMHostName(8);
    int i = 1;
    this.jjtree.openNodeScope(localJDMHostName);
    try
    {
      Token localToken = jj_consume_token(31);
      localJDMHostName.name.append(localToken.image);
      for (;;)
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 37: 
          break;
        default: 
          this.jj_la1[9] = this.jj_gen;
          break;
        }
        jj_consume_token(37);
        localToken = jj_consume_token(31);
        localJDMHostName.name.append("." + localToken.image);
      }
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMHostName, true);
      }
    }
  }
  
  public final void IpAddress()
    throws ParseException
  {
    JDMIpAddress localJDMIpAddress = new JDMIpAddress(9);
    int i = 1;
    this.jjtree.openNodeScope(localJDMIpAddress);
    try
    {
      Token localToken = jj_consume_token(24);
      localJDMIpAddress.address.append(localToken.image);
      for (;;)
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 37: 
          break;
        default: 
          this.jj_la1[10] = this.jj_gen;
          break;
        }
        jj_consume_token(37);
        localToken = jj_consume_token(24);
        localJDMIpAddress.address.append("." + localToken.image);
      }
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMIpAddress, true);
      }
    }
  }
  
  public final void IpV6Address()
    throws ParseException
  {
    JDMIpV6Address localJDMIpV6Address = new JDMIpV6Address(10);
    int i = 1;
    this.jjtree.openNodeScope(localJDMIpV6Address);
    try
    {
      Token localToken = jj_consume_token(28);
      this.jjtree.closeNodeScope(localJDMIpV6Address, true);
      i = 0;
      localJDMIpV6Address.address.append(localToken.image);
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMIpV6Address, true);
      }
    }
  }
  
  public final void IpMask()
    throws ParseException
  {
    JDMIpMask localJDMIpMask = new JDMIpMask(11);
    int i = 1;
    this.jjtree.openNodeScope(localJDMIpMask);
    try
    {
      Token localToken = jj_consume_token(24);
      localJDMIpMask.address.append(localToken.image);
      for (;;)
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 38: 
          break;
        default: 
          this.jj_la1[11] = this.jj_gen;
          break;
        }
        jj_consume_token(38);
        localToken = jj_consume_token(24);
        localJDMIpMask.address.append("." + localToken.image);
      }
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMIpMask, true);
      }
    }
  }
  
  public final void NetMask()
    throws ParseException
  {
    JDMNetMask localJDMNetMask = new JDMNetMask(12);
    int i = 1;
    this.jjtree.openNodeScope(localJDMNetMask);
    try
    {
      Token localToken = jj_consume_token(24);
      localJDMNetMask.address.append(localToken.image);
      for (;;)
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 37: 
          break;
        default: 
          this.jj_la1[12] = this.jj_gen;
          break;
        }
        jj_consume_token(37);
        localToken = jj_consume_token(24);
        localJDMNetMask.address.append("." + localToken.image);
      }
      jj_consume_token(39);
      localToken = jj_consume_token(24);
      this.jjtree.closeNodeScope(localJDMNetMask, true);
      i = 0;
      localJDMNetMask.mask = localToken.image;
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMNetMask, true);
      }
    }
  }
  
  public final void NetMaskV6()
    throws ParseException
  {
    JDMNetMaskV6 localJDMNetMaskV6 = new JDMNetMaskV6(13);
    int i = 1;
    this.jjtree.openNodeScope(localJDMNetMaskV6);
    try
    {
      Token localToken = jj_consume_token(28);
      localJDMNetMaskV6.address.append(localToken.image);
      jj_consume_token(39);
      localToken = jj_consume_token(24);
      this.jjtree.closeNodeScope(localJDMNetMaskV6, true);
      i = 0;
      localJDMNetMaskV6.mask = localToken.image;
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMNetMaskV6, true);
      }
    }
  }
  
  public final void TrapBlock()
    throws ParseException
  {
    JDMTrapBlock localJDMTrapBlock = new JDMTrapBlock(14);
    int i = 1;
    this.jjtree.openNodeScope(localJDMTrapBlock);
    try
    {
      jj_consume_token(19);
      jj_consume_token(9);
      jj_consume_token(13);
      for (;;)
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 13: 
          break;
        default: 
          this.jj_la1[13] = this.jj_gen;
          break;
        }
        TrapItem();
      }
      jj_consume_token(16);
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        this.jjtree.clearNodeScope(localJDMTrapBlock);
        i = 0;
      }
      else
      {
        this.jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMTrapBlock, true);
      }
    }
  }
  
  public final void TrapItem()
    throws ParseException
  {
    JDMTrapItem localJDMTrapItem = new JDMTrapItem(15);
    int i = 1;
    this.jjtree.openNodeScope(localJDMTrapItem);
    try
    {
      jj_consume_token(13);
      localJDMTrapItem.comm = TrapCommunity();
      TrapInterestedHost();
      for (;;)
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 13: 
          break;
        default: 
          this.jj_la1[14] = this.jj_gen;
          break;
        }
        Enterprise();
      }
      jj_consume_token(16);
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        this.jjtree.clearNodeScope(localJDMTrapItem);
        i = 0;
      }
      else
      {
        this.jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMTrapItem, true);
      }
    }
  }
  
  public final JDMTrapCommunity TrapCommunity()
    throws ParseException
  {
    JDMTrapCommunity localJDMTrapCommunity1 = new JDMTrapCommunity(16);
    int i = 1;
    this.jjtree.openNodeScope(localJDMTrapCommunity1);
    try
    {
      jj_consume_token(21);
      jj_consume_token(9);
      Token localToken = jj_consume_token(31);
      this.jjtree.closeNodeScope(localJDMTrapCommunity1, true);
      i = 0;
      localJDMTrapCommunity1.community = localToken.image;
      JDMTrapCommunity localJDMTrapCommunity2 = localJDMTrapCommunity1;
      return localJDMTrapCommunity2;
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMTrapCommunity1, true);
      }
    }
  }
  
  public final void TrapInterestedHost()
    throws ParseException
  {
    JDMTrapInterestedHost localJDMTrapInterestedHost = new JDMTrapInterestedHost(17);
    int i = 1;
    this.jjtree.openNodeScope(localJDMTrapInterestedHost);
    try
    {
      jj_consume_token(12);
      jj_consume_token(9);
      HostTrap();
      for (;;)
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 36: 
          break;
        default: 
          this.jj_la1[15] = this.jj_gen;
          break;
        }
        jj_consume_token(36);
        HostTrap();
      }
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        this.jjtree.clearNodeScope(localJDMTrapInterestedHost);
        i = 0;
      }
      else
      {
        this.jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMTrapInterestedHost, true);
      }
    }
  }
  
  public final void HostTrap()
    throws ParseException
  {
    JDMHostTrap localJDMHostTrap = new JDMHostTrap(18);
    int i = 1;
    this.jjtree.openNodeScope(localJDMHostTrap);
    try
    {
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 31: 
        HostName();
        break;
      case 24: 
        IpAddress();
        break;
      case 28: 
        IpV6Address();
        break;
      default: 
        this.jj_la1[16] = this.jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        this.jjtree.clearNodeScope(localJDMHostTrap);
        i = 0;
      }
      else
      {
        this.jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMHostTrap, true);
      }
    }
  }
  
  public final void Enterprise()
    throws ParseException
  {
    JDMEnterprise localJDMEnterprise = new JDMEnterprise(19);
    int i = 1;
    this.jjtree.openNodeScope(localJDMEnterprise);
    try
    {
      jj_consume_token(13);
      jj_consume_token(11);
      jj_consume_token(9);
      Token localToken = jj_consume_token(35);
      localJDMEnterprise.enterprise = localToken.image;
      jj_consume_token(23);
      jj_consume_token(9);
      TrapNum();
      for (;;)
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 36: 
          break;
        default: 
          this.jj_la1[17] = this.jj_gen;
          break;
        }
        jj_consume_token(36);
        TrapNum();
      }
      jj_consume_token(16);
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        this.jjtree.clearNodeScope(localJDMEnterprise);
        i = 0;
      }
      else
      {
        this.jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMEnterprise, true);
      }
    }
  }
  
  public final void TrapNum()
    throws ParseException
  {
    JDMTrapNum localJDMTrapNum = new JDMTrapNum(20);
    int i = 1;
    this.jjtree.openNodeScope(localJDMTrapNum);
    try
    {
      Token localToken = jj_consume_token(24);
      localJDMTrapNum.low = Integer.parseInt(localToken.image);
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 15: 
        jj_consume_token(15);
        localToken = jj_consume_token(24);
        localJDMTrapNum.high = Integer.parseInt(localToken.image);
        break;
      default: 
        this.jj_la1[18] = this.jj_gen;
      }
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMTrapNum, true);
      }
    }
  }
  
  public final void InformBlock()
    throws ParseException
  {
    JDMInformBlock localJDMInformBlock = new JDMInformBlock(21);
    int i = 1;
    this.jjtree.openNodeScope(localJDMInformBlock);
    try
    {
      jj_consume_token(20);
      jj_consume_token(9);
      jj_consume_token(13);
      for (;;)
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 13: 
          break;
        default: 
          this.jj_la1[19] = this.jj_gen;
          break;
        }
        InformItem();
      }
      jj_consume_token(16);
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        this.jjtree.clearNodeScope(localJDMInformBlock);
        i = 0;
      }
      else
      {
        this.jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMInformBlock, true);
      }
    }
  }
  
  public final void InformItem()
    throws ParseException
  {
    JDMInformItem localJDMInformItem = new JDMInformItem(22);
    int i = 1;
    this.jjtree.openNodeScope(localJDMInformItem);
    try
    {
      jj_consume_token(13);
      localJDMInformItem.comm = InformCommunity();
      InformInterestedHost();
      jj_consume_token(16);
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        this.jjtree.clearNodeScope(localJDMInformItem);
        i = 0;
      }
      else
      {
        this.jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMInformItem, true);
      }
    }
  }
  
  public final JDMInformCommunity InformCommunity()
    throws ParseException
  {
    JDMInformCommunity localJDMInformCommunity1 = new JDMInformCommunity(23);
    int i = 1;
    this.jjtree.openNodeScope(localJDMInformCommunity1);
    try
    {
      jj_consume_token(22);
      jj_consume_token(9);
      Token localToken = jj_consume_token(31);
      this.jjtree.closeNodeScope(localJDMInformCommunity1, true);
      i = 0;
      localJDMInformCommunity1.community = localToken.image;
      JDMInformCommunity localJDMInformCommunity2 = localJDMInformCommunity1;
      return localJDMInformCommunity2;
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMInformCommunity1, true);
      }
    }
  }
  
  public final void InformInterestedHost()
    throws ParseException
  {
    JDMInformInterestedHost localJDMInformInterestedHost = new JDMInformInterestedHost(24);
    int i = 1;
    this.jjtree.openNodeScope(localJDMInformInterestedHost);
    try
    {
      jj_consume_token(12);
      jj_consume_token(9);
      HostInform();
      for (;;)
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 36: 
          break;
        default: 
          this.jj_la1[20] = this.jj_gen;
          break;
        }
        jj_consume_token(36);
        HostInform();
      }
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        this.jjtree.clearNodeScope(localJDMInformInterestedHost);
        i = 0;
      }
      else
      {
        this.jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMInformInterestedHost, true);
      }
    }
  }
  
  public final void HostInform()
    throws ParseException
  {
    JDMHostInform localJDMHostInform = new JDMHostInform(25);
    int i = 1;
    this.jjtree.openNodeScope(localJDMHostInform);
    try
    {
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 31: 
        HostName();
        break;
      case 24: 
        IpAddress();
        break;
      case 28: 
        IpV6Address();
        break;
      default: 
        this.jj_la1[21] = this.jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        this.jjtree.clearNodeScope(localJDMHostInform);
        i = 0;
      }
      else
      {
        this.jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        this.jjtree.closeNodeScope(localJDMHostInform, true);
      }
    }
  }
  
  private final boolean jj_2_1(int paramInt)
  {
    this.jj_la = paramInt;
    this.jj_lastpos = (this.jj_scanpos = this.token);
    boolean bool = !jj_3_1();
    jj_save(0, paramInt);
    return bool;
  }
  
  private final boolean jj_2_2(int paramInt)
  {
    this.jj_la = paramInt;
    this.jj_lastpos = (this.jj_scanpos = this.token);
    boolean bool = !jj_3_2();
    jj_save(1, paramInt);
    return bool;
  }
  
  private final boolean jj_2_3(int paramInt)
  {
    this.jj_la = paramInt;
    this.jj_lastpos = (this.jj_scanpos = this.token);
    boolean bool = !jj_3_3();
    jj_save(2, paramInt);
    return bool;
  }
  
  private final boolean jj_3_3()
  {
    if (jj_scan_token(24)) {
      return true;
    }
    if ((this.jj_la == 0) && (this.jj_scanpos == this.jj_lastpos)) {
      return false;
    }
    if (jj_scan_token(37)) {
      return true;
    }
    return (this.jj_la != 0) || (this.jj_scanpos != this.jj_lastpos);
  }
  
  private final boolean jj_3_2()
  {
    if (jj_scan_token(28)) {
      return true;
    }
    if ((this.jj_la == 0) && (this.jj_scanpos == this.jj_lastpos)) {
      return false;
    }
    if (jj_scan_token(39)) {
      return true;
    }
    if ((this.jj_la == 0) && (this.jj_scanpos == this.jj_lastpos)) {
      return false;
    }
    if (jj_scan_token(24)) {
      return true;
    }
    return (this.jj_la != 0) || (this.jj_scanpos != this.jj_lastpos);
  }
  
  private final boolean jj_3_1()
  {
    if (jj_scan_token(24)) {
      return true;
    }
    if ((this.jj_la == 0) && (this.jj_scanpos == this.jj_lastpos)) {
      return false;
    }
    do
    {
      Token localToken = this.jj_scanpos;
      if (jj_3R_14())
      {
        this.jj_scanpos = localToken;
        break;
      }
    } while ((this.jj_la != 0) || (this.jj_scanpos != this.jj_lastpos));
    return false;
    if (jj_scan_token(39)) {
      return true;
    }
    if ((this.jj_la == 0) && (this.jj_scanpos == this.jj_lastpos)) {
      return false;
    }
    if (jj_scan_token(24)) {
      return true;
    }
    return (this.jj_la != 0) || (this.jj_scanpos != this.jj_lastpos);
  }
  
  private final boolean jj_3R_14()
  {
    if (jj_scan_token(37)) {
      return true;
    }
    if ((this.jj_la == 0) && (this.jj_scanpos == this.jj_lastpos)) {
      return false;
    }
    if (jj_scan_token(24)) {
      return true;
    }
    return (this.jj_la != 0) || (this.jj_scanpos != this.jj_lastpos);
  }
  
  public Parser(InputStream paramInputStream)
  {
    this.jj_input_stream = new ASCII_CharStream(paramInputStream, 1, 1);
    this.token_source = new ParserTokenManager(this.jj_input_stream);
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0;
    for (int i = 0; i < 22; i++) {
      this.jj_la1[i] = -1;
    }
    for (i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }
  
  public void ReInit(InputStream paramInputStream)
  {
    this.jj_input_stream.ReInit(paramInputStream, 1, 1);
    this.token_source.ReInit(this.jj_input_stream);
    this.token = new Token();
    this.jj_ntk = -1;
    this.jjtree.reset();
    this.jj_gen = 0;
    for (int i = 0; i < 22; i++) {
      this.jj_la1[i] = -1;
    }
    for (i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }
  
  public Parser(Reader paramReader)
  {
    this.jj_input_stream = new ASCII_CharStream(paramReader, 1, 1);
    this.token_source = new ParserTokenManager(this.jj_input_stream);
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0;
    for (int i = 0; i < 22; i++) {
      this.jj_la1[i] = -1;
    }
    for (i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }
  
  public void ReInit(Reader paramReader)
  {
    this.jj_input_stream.ReInit(paramReader, 1, 1);
    this.token_source.ReInit(this.jj_input_stream);
    this.token = new Token();
    this.jj_ntk = -1;
    this.jjtree.reset();
    this.jj_gen = 0;
    for (int i = 0; i < 22; i++) {
      this.jj_la1[i] = -1;
    }
    for (i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }
  
  public Parser(ParserTokenManager paramParserTokenManager)
  {
    this.token_source = paramParserTokenManager;
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0;
    for (int i = 0; i < 22; i++) {
      this.jj_la1[i] = -1;
    }
    for (i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }
  
  public void ReInit(ParserTokenManager paramParserTokenManager)
  {
    this.token_source = paramParserTokenManager;
    this.token = new Token();
    this.jj_ntk = -1;
    this.jjtree.reset();
    this.jj_gen = 0;
    for (int i = 0; i < 22; i++) {
      this.jj_la1[i] = -1;
    }
    for (i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }
  
  private final Token jj_consume_token(int paramInt)
    throws ParseException
  {
    Token localToken;
    if ((localToken = this.token).next != null) {
      this.token = this.token.next;
    } else {
      this.token = (this.token.next = this.token_source.getNextToken());
    }
    this.jj_ntk = -1;
    if (this.token.kind == paramInt)
    {
      this.jj_gen += 1;
      if (++this.jj_gc > 100)
      {
        this.jj_gc = 0;
        for (int i = 0; i < this.jj_2_rtns.length; i++) {
          for (JJCalls localJJCalls = this.jj_2_rtns[i]; localJJCalls != null; localJJCalls = localJJCalls.next) {
            if (localJJCalls.gen < this.jj_gen) {
              localJJCalls.first = null;
            }
          }
        }
      }
      return this.token;
    }
    this.token = localToken;
    this.jj_kind = paramInt;
    throw generateParseException();
  }
  
  private final boolean jj_scan_token(int paramInt)
  {
    if (this.jj_scanpos == this.jj_lastpos)
    {
      this.jj_la -= 1;
      if (this.jj_scanpos.next == null) {
        this.jj_lastpos = (this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken());
      } else {
        this.jj_lastpos = (this.jj_scanpos = this.jj_scanpos.next);
      }
    }
    else
    {
      this.jj_scanpos = this.jj_scanpos.next;
    }
    if (this.jj_rescan)
    {
      int i = 0;
      for (Token localToken = this.token; (localToken != null) && (localToken != this.jj_scanpos); localToken = localToken.next) {
        i++;
      }
      if (localToken != null) {
        jj_add_error_token(paramInt, i);
      }
    }
    return this.jj_scanpos.kind != paramInt;
  }
  
  public final Token getNextToken()
  {
    if (this.token.next != null) {
      this.token = this.token.next;
    } else {
      this.token = (this.token.next = this.token_source.getNextToken());
    }
    this.jj_ntk = -1;
    this.jj_gen += 1;
    return this.token;
  }
  
  public final Token getToken(int paramInt)
  {
    Token localToken = this.lookingAhead ? this.jj_scanpos : this.token;
    for (int i = 0; i < paramInt; i++) {
      if (localToken.next != null) {
        localToken = localToken.next;
      } else {
        localToken = localToken.next = this.token_source.getNextToken();
      }
    }
    return localToken;
  }
  
  private final int jj_ntk()
  {
    if ((this.jj_nt = this.token.next) == null) {
      return this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind;
    }
    return this.jj_ntk = this.jj_nt.kind;
  }
  
  private void jj_add_error_token(int paramInt1, int paramInt2)
  {
    if (paramInt2 >= 100) {
      return;
    }
    if (paramInt2 == this.jj_endpos + 1)
    {
      this.jj_lasttokens[(this.jj_endpos++)] = paramInt1;
    }
    else if (this.jj_endpos != 0)
    {
      this.jj_expentry = new int[this.jj_endpos];
      for (int i = 0; i < this.jj_endpos; i++) {
        this.jj_expentry[i] = this.jj_lasttokens[i];
      }
      i = 0;
      Enumeration localEnumeration = this.jj_expentries.elements();
      while (localEnumeration.hasMoreElements())
      {
        int[] arrayOfInt = (int[])localEnumeration.nextElement();
        if (arrayOfInt.length == this.jj_expentry.length)
        {
          i = 1;
          for (int j = 0; j < this.jj_expentry.length; j++) {
            if (arrayOfInt[j] != this.jj_expentry[j])
            {
              i = 0;
              break;
            }
          }
          if (i != 0) {
            break;
          }
        }
      }
      if (i == 0) {
        this.jj_expentries.addElement(this.jj_expentry);
      }
      if (paramInt2 != 0)
      {
        int tmp202_201 = paramInt2;
        this.jj_endpos = tmp202_201;
        this.jj_lasttokens[(tmp202_201 - 1)] = paramInt1;
      }
    }
  }
  
  public final ParseException generateParseException()
  {
    this.jj_expentries.removeAllElements();
    boolean[] arrayOfBoolean = new boolean[40];
    for (int i = 0; i < 40; i++) {
      arrayOfBoolean[i] = false;
    }
    if (this.jj_kind >= 0)
    {
      arrayOfBoolean[this.jj_kind] = true;
      this.jj_kind = -1;
    }
    for (i = 0; i < 22; i++) {
      if (this.jj_la1[i] == this.jj_gen) {
        for (j = 0; j < 32; j++)
        {
          if ((this.jj_la1_0[i] & 1 << j) != 0) {
            arrayOfBoolean[j] = true;
          }
          if ((this.jj_la1_1[i] & 1 << j) != 0) {
            arrayOfBoolean[(32 + j)] = true;
          }
        }
      }
    }
    for (i = 0; i < 40; i++) {
      if (arrayOfBoolean[i] != 0)
      {
        this.jj_expentry = new int[1];
        this.jj_expentry[0] = i;
        this.jj_expentries.addElement(this.jj_expentry);
      }
    }
    this.jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] arrayOfInt = new int[this.jj_expentries.size()][];
    for (int j = 0; j < this.jj_expentries.size(); j++) {
      arrayOfInt[j] = ((int[])this.jj_expentries.elementAt(j));
    }
    return new ParseException(this.token, arrayOfInt, tokenImage);
  }
  
  public final void enable_tracing() {}
  
  public final void disable_tracing() {}
  
  private final void jj_rescan_token()
  {
    this.jj_rescan = true;
    for (int i = 0; i < 3; i++)
    {
      JJCalls localJJCalls = this.jj_2_rtns[i];
      do
      {
        if (localJJCalls.gen > this.jj_gen)
        {
          this.jj_la = localJJCalls.arg;
          this.jj_lastpos = (this.jj_scanpos = localJJCalls.first);
          switch (i)
          {
          case 0: 
            jj_3_1();
            break;
          case 1: 
            jj_3_2();
            break;
          case 2: 
            jj_3_3();
          }
        }
        localJJCalls = localJJCalls.next;
      } while (localJJCalls != null);
    }
    this.jj_rescan = false;
  }
  
  private final void jj_save(int paramInt1, int paramInt2)
  {
    for (JJCalls localJJCalls = this.jj_2_rtns[paramInt1]; localJJCalls.gen > this.jj_gen; localJJCalls = localJJCalls.next) {
      if (localJJCalls.next == null)
      {
        localJJCalls = localJJCalls.next = new JJCalls();
        break;
      }
    }
    localJJCalls.gen = (this.jj_gen + paramInt2 - this.jj_la);
    localJJCalls.first = this.token;
    localJJCalls.arg = paramInt2;
  }
  
  static final class JJCalls
  {
    int gen;
    Token first;
    int arg;
    JJCalls next;
    
    JJCalls() {}
  }
}
