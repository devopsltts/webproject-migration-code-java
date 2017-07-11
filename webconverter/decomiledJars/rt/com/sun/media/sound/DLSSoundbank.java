package com.sun.media.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public final class DLSSoundbank
  implements Soundbank
{
  private static final int DLS_CDL_AND = 1;
  private static final int DLS_CDL_OR = 2;
  private static final int DLS_CDL_XOR = 3;
  private static final int DLS_CDL_ADD = 4;
  private static final int DLS_CDL_SUBTRACT = 5;
  private static final int DLS_CDL_MULTIPLY = 6;
  private static final int DLS_CDL_DIVIDE = 7;
  private static final int DLS_CDL_LOGICAL_AND = 8;
  private static final int DLS_CDL_LOGICAL_OR = 9;
  private static final int DLS_CDL_LT = 10;
  private static final int DLS_CDL_LE = 11;
  private static final int DLS_CDL_GT = 12;
  private static final int DLS_CDL_GE = 13;
  private static final int DLS_CDL_EQ = 14;
  private static final int DLS_CDL_NOT = 15;
  private static final int DLS_CDL_CONST = 16;
  private static final int DLS_CDL_QUERY = 17;
  private static final int DLS_CDL_QUERYSUPPORTED = 18;
  private static final DLSID DLSID_GMInHardware = new DLSID(395259684L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
  private static final DLSID DLSID_GSInHardware = new DLSID(395259685L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
  private static final DLSID DLSID_XGInHardware = new DLSID(395259686L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
  private static final DLSID DLSID_SupportsDLS1 = new DLSID(395259687L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
  private static final DLSID DLSID_SupportsDLS2 = new DLSID(-247096859L, 18057, 4562, 175, 166, 0, 170, 0, 36, 216, 182);
  private static final DLSID DLSID_SampleMemorySize = new DLSID(395259688L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
  private static final DLSID DLSID_ManufacturersID = new DLSID(-1338109567L, 32917, 4562, 161, 239, 0, 96, 8, 51, 219, 216);
  private static final DLSID DLSID_ProductID = new DLSID(-1338109566L, 32917, 4562, 161, 239, 0, 96, 8, 51, 219, 216);
  private static final DLSID DLSID_SamplePlaybackRate = new DLSID(714209043L, 42175, 4562, 187, 223, 0, 96, 8, 51, 219, 216);
  private long major = -1L;
  private long minor = -1L;
  private final DLSInfo info = new DLSInfo();
  private final List<DLSInstrument> instruments = new ArrayList();
  private final List<DLSSample> samples = new ArrayList();
  private boolean largeFormat = false;
  private File sampleFile;
  private Map<DLSRegion, Long> temp_rgnassign = new HashMap();
  
  public DLSSoundbank() {}
  
  public DLSSoundbank(URL paramURL)
    throws IOException
  {
    InputStream localInputStream = paramURL.openStream();
    try
    {
      readSoundbank(localInputStream);
      localInputStream.close();
    }
    finally
    {
      localInputStream.close();
    }
  }
  
  public DLSSoundbank(File paramFile)
    throws IOException
  {
    this.largeFormat = true;
    this.sampleFile = paramFile;
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    try
    {
      readSoundbank(localFileInputStream);
      localFileInputStream.close();
    }
    finally
    {
      localFileInputStream.close();
    }
  }
  
  public DLSSoundbank(InputStream paramInputStream)
    throws IOException
  {
    readSoundbank(paramInputStream);
  }
  
  private void readSoundbank(InputStream paramInputStream)
    throws IOException
  {
    RIFFReader localRIFFReader = new RIFFReader(paramInputStream);
    if (!localRIFFReader.getFormat().equals("RIFF")) {
      throw new RIFFInvalidFormatException("Input stream is not a valid RIFF stream!");
    }
    if (!localRIFFReader.getType().equals("DLS ")) {
      throw new RIFFInvalidFormatException("Input stream is not a valid DLS soundbank!");
    }
    while (localRIFFReader.hasNextChunk())
    {
      localObject = localRIFFReader.nextChunk();
      if (((RIFFReader)localObject).getFormat().equals("LIST"))
      {
        if (((RIFFReader)localObject).getType().equals("INFO")) {
          readInfoChunk((RIFFReader)localObject);
        }
        if (((RIFFReader)localObject).getType().equals("lins")) {
          readLinsChunk((RIFFReader)localObject);
        }
        if (((RIFFReader)localObject).getType().equals("wvpl")) {
          readWvplChunk((RIFFReader)localObject);
        }
      }
      else
      {
        if ((((RIFFReader)localObject).getFormat().equals("cdl ")) && (!readCdlChunk((RIFFReader)localObject))) {
          throw new RIFFInvalidFormatException("DLS file isn't supported!");
        }
        if ((!((RIFFReader)localObject).getFormat().equals("colh")) || ((!((RIFFReader)localObject).getFormat().equals("ptbl")) || (((RIFFReader)localObject).getFormat().equals("vers"))))
        {
          this.major = ((RIFFReader)localObject).readUnsignedInt();
          this.minor = ((RIFFReader)localObject).readUnsignedInt();
        }
      }
    }
    Object localObject = this.temp_rgnassign.entrySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
      ((DLSRegion)localEntry.getKey()).sample = ((DLSSample)this.samples.get((int)((Long)localEntry.getValue()).longValue()));
    }
    this.temp_rgnassign = null;
  }
  
  private boolean cdlIsQuerySupported(DLSID paramDLSID)
  {
    return (paramDLSID.equals(DLSID_GMInHardware)) || (paramDLSID.equals(DLSID_GSInHardware)) || (paramDLSID.equals(DLSID_XGInHardware)) || (paramDLSID.equals(DLSID_SupportsDLS1)) || (paramDLSID.equals(DLSID_SupportsDLS2)) || (paramDLSID.equals(DLSID_SampleMemorySize)) || (paramDLSID.equals(DLSID_ManufacturersID)) || (paramDLSID.equals(DLSID_ProductID)) || (paramDLSID.equals(DLSID_SamplePlaybackRate));
  }
  
  private long cdlQuery(DLSID paramDLSID)
  {
    if (paramDLSID.equals(DLSID_GMInHardware)) {
      return 1L;
    }
    if (paramDLSID.equals(DLSID_GSInHardware)) {
      return 0L;
    }
    if (paramDLSID.equals(DLSID_XGInHardware)) {
      return 0L;
    }
    if (paramDLSID.equals(DLSID_SupportsDLS1)) {
      return 1L;
    }
    if (paramDLSID.equals(DLSID_SupportsDLS2)) {
      return 1L;
    }
    if (paramDLSID.equals(DLSID_SampleMemorySize)) {
      return Runtime.getRuntime().totalMemory();
    }
    if (paramDLSID.equals(DLSID_ManufacturersID)) {
      return 0L;
    }
    if (paramDLSID.equals(DLSID_ProductID)) {
      return 0L;
    }
    if (paramDLSID.equals(DLSID_SamplePlaybackRate)) {
      return 44100L;
    }
    return 0L;
  }
  
  private boolean readCdlChunk(RIFFReader paramRIFFReader)
    throws IOException
  {
    Stack localStack = new Stack();
    while (paramRIFFReader.available() != 0)
    {
      int i = paramRIFFReader.readUnsignedShort();
      long l1;
      long l2;
      DLSID localDLSID;
      switch (i)
      {
      case 1: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf((l1 != 0L) && (l2 != 0L) ? 1L : 0L));
        break;
      case 2: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf((l1 != 0L) || (l2 != 0L) ? 1L : 0L));
        break;
      case 3: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(((l1 != 0L ? 1 : 0) ^ (l2 != 0L ? 1 : 0)) != 0 ? 1L : 0L));
        break;
      case 4: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 + l2));
        break;
      case 5: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 - l2));
        break;
      case 6: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 * l2));
        break;
      case 7: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 / l2));
        break;
      case 8: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf((l1 != 0L) && (l2 != 0L) ? 1L : 0L));
        break;
      case 9: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf((l1 != 0L) || (l2 != 0L) ? 1L : 0L));
        break;
      case 10: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 < l2 ? 1L : 0L));
        break;
      case 11: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 <= l2 ? 1L : 0L));
        break;
      case 12: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 > l2 ? 1L : 0L));
        break;
      case 13: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 >= l2 ? 1L : 0L));
        break;
      case 14: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 == l2 ? 1L : 0L));
        break;
      case 15: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 == 0L ? 1L : 0L));
        break;
      case 16: 
        localStack.push(Long.valueOf(paramRIFFReader.readUnsignedInt()));
        break;
      case 17: 
        localDLSID = DLSID.read(paramRIFFReader);
        localStack.push(Long.valueOf(cdlQuery(localDLSID)));
        break;
      case 18: 
        localDLSID = DLSID.read(paramRIFFReader);
        localStack.push(Long.valueOf(cdlIsQuerySupported(localDLSID) ? 1L : 0L));
      }
    }
    if (localStack.isEmpty()) {
      return false;
    }
    return ((Long)localStack.pop()).longValue() == 1L;
  }
  
  private void readInfoChunk(RIFFReader paramRIFFReader)
    throws IOException
  {
    this.info.name = null;
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader = paramRIFFReader.nextChunk();
      String str = localRIFFReader.getFormat();
      if (str.equals("INAM")) {
        this.info.name = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICRD")) {
        this.info.creationDate = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IENG")) {
        this.info.engineers = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IPRD")) {
        this.info.product = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICOP")) {
        this.info.copyright = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICMT")) {
        this.info.comments = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISFT")) {
        this.info.tools = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IARL")) {
        this.info.archival_location = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IART")) {
        this.info.artist = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICMS")) {
        this.info.commissioned = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IGNR")) {
        this.info.genre = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IKEY")) {
        this.info.keywords = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IMED")) {
        this.info.medium = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISBJ")) {
        this.info.subject = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISRC")) {
        this.info.source = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISRF")) {
        this.info.source_form = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ITCH")) {
        this.info.technician = localRIFFReader.readString(localRIFFReader.available());
      }
    }
  }
  
  private void readLinsChunk(RIFFReader paramRIFFReader)
    throws IOException
  {
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader = paramRIFFReader.nextChunk();
      if ((localRIFFReader.getFormat().equals("LIST")) && (localRIFFReader.getType().equals("ins "))) {
        readInsChunk(localRIFFReader);
      }
    }
  }
  
  private void readInsChunk(RIFFReader paramRIFFReader)
    throws IOException
  {
    DLSInstrument localDLSInstrument = new DLSInstrument(this);
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader = paramRIFFReader.nextChunk();
      String str = localRIFFReader.getFormat();
      if (str.equals("LIST"))
      {
        if (localRIFFReader.getType().equals("INFO")) {
          readInsInfoChunk(localDLSInstrument, localRIFFReader);
        }
        Object localObject1;
        Object localObject2;
        if (localRIFFReader.getType().equals("lrgn")) {
          while (localRIFFReader.hasNextChunk())
          {
            localObject1 = localRIFFReader.nextChunk();
            if (((RIFFReader)localObject1).getFormat().equals("LIST"))
            {
              if (((RIFFReader)localObject1).getType().equals("rgn "))
              {
                localObject2 = new DLSRegion();
                if (readRgnChunk((DLSRegion)localObject2, (RIFFReader)localObject1)) {
                  localDLSInstrument.getRegions().add(localObject2);
                }
              }
              if (((RIFFReader)localObject1).getType().equals("rgn2"))
              {
                localObject2 = new DLSRegion();
                if (readRgnChunk((DLSRegion)localObject2, (RIFFReader)localObject1)) {
                  localDLSInstrument.getRegions().add(localObject2);
                }
              }
            }
          }
        }
        if (localRIFFReader.getType().equals("lart"))
        {
          localObject1 = new ArrayList();
          while (localRIFFReader.hasNextChunk())
          {
            localObject2 = localRIFFReader.nextChunk();
            if ((localRIFFReader.getFormat().equals("cdl ")) && (!readCdlChunk(localRIFFReader)))
            {
              ((List)localObject1).clear();
              break;
            }
            if (((RIFFReader)localObject2).getFormat().equals("art1")) {
              readArt1Chunk((List)localObject1, (RIFFReader)localObject2);
            }
          }
          localDLSInstrument.getModulators().addAll((Collection)localObject1);
        }
        if (localRIFFReader.getType().equals("lar2"))
        {
          localObject1 = new ArrayList();
          while (localRIFFReader.hasNextChunk())
          {
            localObject2 = localRIFFReader.nextChunk();
            if ((localRIFFReader.getFormat().equals("cdl ")) && (!readCdlChunk(localRIFFReader)))
            {
              ((List)localObject1).clear();
              break;
            }
            if (((RIFFReader)localObject2).getFormat().equals("art2")) {
              readArt2Chunk((List)localObject1, (RIFFReader)localObject2);
            }
          }
          localDLSInstrument.getModulators().addAll((Collection)localObject1);
        }
      }
      else
      {
        if (str.equals("dlid"))
        {
          localDLSInstrument.guid = new byte[16];
          localRIFFReader.readFully(localDLSInstrument.guid);
        }
        if (str.equals("insh"))
        {
          localRIFFReader.readUnsignedInt();
          int i = localRIFFReader.read();
          i += ((localRIFFReader.read() & 0x7F) << 7);
          localRIFFReader.read();
          int j = localRIFFReader.read();
          int k = localRIFFReader.read() & 0x7F;
          localRIFFReader.read();
          localRIFFReader.read();
          localRIFFReader.read();
          localDLSInstrument.bank = i;
          localDLSInstrument.preset = k;
          localDLSInstrument.druminstrument = ((j & 0x80) > 0);
        }
      }
    }
    this.instruments.add(localDLSInstrument);
  }
  
  private void readArt1Chunk(List<DLSModulator> paramList, RIFFReader paramRIFFReader)
    throws IOException
  {
    long l1 = paramRIFFReader.readUnsignedInt();
    long l2 = paramRIFFReader.readUnsignedInt();
    if (l1 - 8L != 0L) {
      paramRIFFReader.skipBytes(l1 - 8L);
    }
    for (int i = 0; i < l2; i++)
    {
      DLSModulator localDLSModulator = new DLSModulator();
      localDLSModulator.version = 1;
      localDLSModulator.source = paramRIFFReader.readUnsignedShort();
      localDLSModulator.control = paramRIFFReader.readUnsignedShort();
      localDLSModulator.destination = paramRIFFReader.readUnsignedShort();
      localDLSModulator.transform = paramRIFFReader.readUnsignedShort();
      localDLSModulator.scale = paramRIFFReader.readInt();
      paramList.add(localDLSModulator);
    }
  }
  
  private void readArt2Chunk(List<DLSModulator> paramList, RIFFReader paramRIFFReader)
    throws IOException
  {
    long l1 = paramRIFFReader.readUnsignedInt();
    long l2 = paramRIFFReader.readUnsignedInt();
    if (l1 - 8L != 0L) {
      paramRIFFReader.skipBytes(l1 - 8L);
    }
    for (int i = 0; i < l2; i++)
    {
      DLSModulator localDLSModulator = new DLSModulator();
      localDLSModulator.version = 2;
      localDLSModulator.source = paramRIFFReader.readUnsignedShort();
      localDLSModulator.control = paramRIFFReader.readUnsignedShort();
      localDLSModulator.destination = paramRIFFReader.readUnsignedShort();
      localDLSModulator.transform = paramRIFFReader.readUnsignedShort();
      localDLSModulator.scale = paramRIFFReader.readInt();
      paramList.add(localDLSModulator);
    }
  }
  
  private boolean readRgnChunk(DLSRegion paramDLSRegion, RIFFReader paramRIFFReader)
    throws IOException
  {
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader1 = paramRIFFReader.nextChunk();
      String str = localRIFFReader1.getFormat();
      if (str.equals("LIST"))
      {
        ArrayList localArrayList;
        RIFFReader localRIFFReader2;
        if (localRIFFReader1.getType().equals("lart"))
        {
          localArrayList = new ArrayList();
          while (localRIFFReader1.hasNextChunk())
          {
            localRIFFReader2 = localRIFFReader1.nextChunk();
            if ((localRIFFReader1.getFormat().equals("cdl ")) && (!readCdlChunk(localRIFFReader1)))
            {
              localArrayList.clear();
              break;
            }
            if (localRIFFReader2.getFormat().equals("art1")) {
              readArt1Chunk(localArrayList, localRIFFReader2);
            }
          }
          paramDLSRegion.getModulators().addAll(localArrayList);
        }
        if (localRIFFReader1.getType().equals("lar2"))
        {
          localArrayList = new ArrayList();
          while (localRIFFReader1.hasNextChunk())
          {
            localRIFFReader2 = localRIFFReader1.nextChunk();
            if ((localRIFFReader1.getFormat().equals("cdl ")) && (!readCdlChunk(localRIFFReader1)))
            {
              localArrayList.clear();
              break;
            }
            if (localRIFFReader2.getFormat().equals("art2")) {
              readArt2Chunk(localArrayList, localRIFFReader2);
            }
          }
          paramDLSRegion.getModulators().addAll(localArrayList);
        }
      }
      else
      {
        if ((str.equals("cdl ")) && (!readCdlChunk(localRIFFReader1))) {
          return false;
        }
        if (str.equals("rgnh"))
        {
          paramDLSRegion.keyfrom = localRIFFReader1.readUnsignedShort();
          paramDLSRegion.keyto = localRIFFReader1.readUnsignedShort();
          paramDLSRegion.velfrom = localRIFFReader1.readUnsignedShort();
          paramDLSRegion.velto = localRIFFReader1.readUnsignedShort();
          paramDLSRegion.options = localRIFFReader1.readUnsignedShort();
          paramDLSRegion.exclusiveClass = localRIFFReader1.readUnsignedShort();
        }
        if (str.equals("wlnk"))
        {
          paramDLSRegion.fusoptions = localRIFFReader1.readUnsignedShort();
          paramDLSRegion.phasegroup = localRIFFReader1.readUnsignedShort();
          paramDLSRegion.channel = localRIFFReader1.readUnsignedInt();
          long l = localRIFFReader1.readUnsignedInt();
          this.temp_rgnassign.put(paramDLSRegion, Long.valueOf(l));
        }
        if (str.equals("wsmp"))
        {
          paramDLSRegion.sampleoptions = new DLSSampleOptions();
          readWsmpChunk(paramDLSRegion.sampleoptions, localRIFFReader1);
        }
      }
    }
    return true;
  }
  
  private void readWsmpChunk(DLSSampleOptions paramDLSSampleOptions, RIFFReader paramRIFFReader)
    throws IOException
  {
    long l1 = paramRIFFReader.readUnsignedInt();
    paramDLSSampleOptions.unitynote = paramRIFFReader.readUnsignedShort();
    paramDLSSampleOptions.finetune = paramRIFFReader.readShort();
    paramDLSSampleOptions.attenuation = paramRIFFReader.readInt();
    paramDLSSampleOptions.options = paramRIFFReader.readUnsignedInt();
    long l2 = paramRIFFReader.readInt();
    if (l1 > 20L) {
      paramRIFFReader.skipBytes(l1 - 20L);
    }
    for (int i = 0; i < l2; i++)
    {
      DLSSampleLoop localDLSSampleLoop = new DLSSampleLoop();
      long l3 = paramRIFFReader.readUnsignedInt();
      localDLSSampleLoop.type = paramRIFFReader.readUnsignedInt();
      localDLSSampleLoop.start = paramRIFFReader.readUnsignedInt();
      localDLSSampleLoop.length = paramRIFFReader.readUnsignedInt();
      paramDLSSampleOptions.loops.add(localDLSSampleLoop);
      if (l3 > 16L) {
        paramRIFFReader.skipBytes(l3 - 16L);
      }
    }
  }
  
  private void readInsInfoChunk(DLSInstrument paramDLSInstrument, RIFFReader paramRIFFReader)
    throws IOException
  {
    paramDLSInstrument.info.name = null;
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader = paramRIFFReader.nextChunk();
      String str = localRIFFReader.getFormat();
      if (str.equals("INAM")) {
        paramDLSInstrument.info.name = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICRD")) {
        paramDLSInstrument.info.creationDate = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IENG")) {
        paramDLSInstrument.info.engineers = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IPRD")) {
        paramDLSInstrument.info.product = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICOP")) {
        paramDLSInstrument.info.copyright = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICMT")) {
        paramDLSInstrument.info.comments = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISFT")) {
        paramDLSInstrument.info.tools = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IARL")) {
        paramDLSInstrument.info.archival_location = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IART")) {
        paramDLSInstrument.info.artist = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICMS")) {
        paramDLSInstrument.info.commissioned = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IGNR")) {
        paramDLSInstrument.info.genre = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IKEY")) {
        paramDLSInstrument.info.keywords = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IMED")) {
        paramDLSInstrument.info.medium = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISBJ")) {
        paramDLSInstrument.info.subject = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISRC")) {
        paramDLSInstrument.info.source = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISRF")) {
        paramDLSInstrument.info.source_form = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ITCH")) {
        paramDLSInstrument.info.technician = localRIFFReader.readString(localRIFFReader.available());
      }
    }
  }
  
  private void readWvplChunk(RIFFReader paramRIFFReader)
    throws IOException
  {
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader = paramRIFFReader.nextChunk();
      if ((localRIFFReader.getFormat().equals("LIST")) && (localRIFFReader.getType().equals("wave"))) {
        readWaveChunk(localRIFFReader);
      }
    }
  }
  
  private void readWaveChunk(RIFFReader paramRIFFReader)
    throws IOException
  {
    DLSSample localDLSSample = new DLSSample(this);
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader = paramRIFFReader.nextChunk();
      String str = localRIFFReader.getFormat();
      if (str.equals("LIST"))
      {
        if (localRIFFReader.getType().equals("INFO")) {
          readWaveInfoChunk(localDLSSample, localRIFFReader);
        }
      }
      else
      {
        if (str.equals("dlid"))
        {
          localDLSSample.guid = new byte[16];
          localRIFFReader.readFully(localDLSSample.guid);
        }
        int j;
        if (str.equals("fmt "))
        {
          int i = localRIFFReader.readUnsignedShort();
          if ((i != 1) && (i != 3)) {
            throw new RIFFInvalidDataException("Only PCM samples are supported!");
          }
          j = localRIFFReader.readUnsignedShort();
          long l = localRIFFReader.readUnsignedInt();
          localRIFFReader.readUnsignedInt();
          int m = localRIFFReader.readUnsignedShort();
          int n = localRIFFReader.readUnsignedShort();
          AudioFormat localAudioFormat = null;
          if (i == 1) {
            if (n == 8) {
              localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, (float)l, n, j, m, (float)l, false);
            } else {
              localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float)l, n, j, m, (float)l, false);
            }
          }
          if (i == 3) {
            localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, (float)l, n, j, m, (float)l, false);
          }
          localDLSSample.format = localAudioFormat;
        }
        if (str.equals("data")) {
          if (this.largeFormat)
          {
            localDLSSample.setData(new ModelByteBuffer(this.sampleFile, localRIFFReader.getFilePointer(), localRIFFReader.available()));
          }
          else
          {
            byte[] arrayOfByte = new byte[localRIFFReader.available()];
            localDLSSample.setData(arrayOfByte);
            j = 0;
            int k = localRIFFReader.available();
            while (j != k) {
              if (k - j > 65536)
              {
                localRIFFReader.readFully(arrayOfByte, j, 65536);
                j += 65536;
              }
              else
              {
                localRIFFReader.readFully(arrayOfByte, j, k - j);
                j = k;
              }
            }
          }
        }
        if (str.equals("wsmp"))
        {
          localDLSSample.sampleoptions = new DLSSampleOptions();
          readWsmpChunk(localDLSSample.sampleoptions, localRIFFReader);
        }
      }
    }
    this.samples.add(localDLSSample);
  }
  
  private void readWaveInfoChunk(DLSSample paramDLSSample, RIFFReader paramRIFFReader)
    throws IOException
  {
    paramDLSSample.info.name = null;
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader = paramRIFFReader.nextChunk();
      String str = localRIFFReader.getFormat();
      if (str.equals("INAM")) {
        paramDLSSample.info.name = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICRD")) {
        paramDLSSample.info.creationDate = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IENG")) {
        paramDLSSample.info.engineers = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IPRD")) {
        paramDLSSample.info.product = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICOP")) {
        paramDLSSample.info.copyright = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICMT")) {
        paramDLSSample.info.comments = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISFT")) {
        paramDLSSample.info.tools = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IARL")) {
        paramDLSSample.info.archival_location = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IART")) {
        paramDLSSample.info.artist = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICMS")) {
        paramDLSSample.info.commissioned = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IGNR")) {
        paramDLSSample.info.genre = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IKEY")) {
        paramDLSSample.info.keywords = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IMED")) {
        paramDLSSample.info.medium = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISBJ")) {
        paramDLSSample.info.subject = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISRC")) {
        paramDLSSample.info.source = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISRF")) {
        paramDLSSample.info.source_form = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ITCH")) {
        paramDLSSample.info.technician = localRIFFReader.readString(localRIFFReader.available());
      }
    }
  }
  
  public void save(String paramString)
    throws IOException
  {
    writeSoundbank(new RIFFWriter(paramString, "DLS "));
  }
  
  public void save(File paramFile)
    throws IOException
  {
    writeSoundbank(new RIFFWriter(paramFile, "DLS "));
  }
  
  public void save(OutputStream paramOutputStream)
    throws IOException
  {
    writeSoundbank(new RIFFWriter(paramOutputStream, "DLS "));
  }
  
  private void writeSoundbank(RIFFWriter paramRIFFWriter)
    throws IOException
  {
    RIFFWriter localRIFFWriter1 = paramRIFFWriter.writeChunk("colh");
    localRIFFWriter1.writeUnsignedInt(this.instruments.size());
    if ((this.major != -1L) && (this.minor != -1L))
    {
      localRIFFWriter2 = paramRIFFWriter.writeChunk("vers");
      localRIFFWriter2.writeUnsignedInt(this.major);
      localRIFFWriter2.writeUnsignedInt(this.minor);
    }
    writeInstruments(paramRIFFWriter.writeList("lins"));
    RIFFWriter localRIFFWriter2 = paramRIFFWriter.writeChunk("ptbl");
    localRIFFWriter2.writeUnsignedInt(8L);
    localRIFFWriter2.writeUnsignedInt(this.samples.size());
    long l1 = paramRIFFWriter.getFilePointer();
    for (int i = 0; i < this.samples.size(); i++) {
      localRIFFWriter2.writeUnsignedInt(0L);
    }
    RIFFWriter localRIFFWriter3 = paramRIFFWriter.writeList("wvpl");
    long l2 = localRIFFWriter3.getFilePointer();
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator1 = this.samples.iterator();
    while (localIterator1.hasNext())
    {
      DLSSample localDLSSample = (DLSSample)localIterator1.next();
      localArrayList.add(Long.valueOf(localRIFFWriter3.getFilePointer() - l2));
      writeSample(localRIFFWriter3.writeList("wave"), localDLSSample);
    }
    long l3 = paramRIFFWriter.getFilePointer();
    paramRIFFWriter.seek(l1);
    paramRIFFWriter.setWriteOverride(true);
    Iterator localIterator2 = localArrayList.iterator();
    while (localIterator2.hasNext())
    {
      Long localLong = (Long)localIterator2.next();
      paramRIFFWriter.writeUnsignedInt(localLong.longValue());
    }
    paramRIFFWriter.setWriteOverride(false);
    paramRIFFWriter.seek(l3);
    writeInfo(paramRIFFWriter.writeList("INFO"), this.info);
    paramRIFFWriter.close();
  }
  
  private void writeSample(RIFFWriter paramRIFFWriter, DLSSample paramDLSSample)
    throws IOException
  {
    AudioFormat localAudioFormat = paramDLSSample.getFormat();
    AudioFormat.Encoding localEncoding = localAudioFormat.getEncoding();
    float f1 = localAudioFormat.getSampleRate();
    int i = localAudioFormat.getSampleSizeInBits();
    int j = localAudioFormat.getChannels();
    int k = localAudioFormat.getFrameSize();
    float f2 = localAudioFormat.getFrameRate();
    boolean bool = localAudioFormat.isBigEndian();
    int m = 0;
    if (localAudioFormat.getSampleSizeInBits() == 8)
    {
      if (!localEncoding.equals(AudioFormat.Encoding.PCM_UNSIGNED))
      {
        localEncoding = AudioFormat.Encoding.PCM_UNSIGNED;
        m = 1;
      }
    }
    else
    {
      if (!localEncoding.equals(AudioFormat.Encoding.PCM_SIGNED))
      {
        localEncoding = AudioFormat.Encoding.PCM_SIGNED;
        m = 1;
      }
      if (bool)
      {
        bool = false;
        m = 1;
      }
    }
    if (m != 0) {
      localAudioFormat = new AudioFormat(localEncoding, f1, i, j, k, f2, bool);
    }
    RIFFWriter localRIFFWriter1 = paramRIFFWriter.writeChunk("fmt ");
    int n = 0;
    if (localAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
      n = 1;
    } else if (localAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
      n = 1;
    } else if (localAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT)) {
      n = 3;
    }
    localRIFFWriter1.writeUnsignedShort(n);
    localRIFFWriter1.writeUnsignedShort(localAudioFormat.getChannels());
    localRIFFWriter1.writeUnsignedInt(localAudioFormat.getSampleRate());
    long l = localAudioFormat.getFrameRate() * localAudioFormat.getFrameSize();
    localRIFFWriter1.writeUnsignedInt(l);
    localRIFFWriter1.writeUnsignedShort(localAudioFormat.getFrameSize());
    localRIFFWriter1.writeUnsignedShort(localAudioFormat.getSampleSizeInBits());
    localRIFFWriter1.write(0);
    localRIFFWriter1.write(0);
    writeSampleOptions(paramRIFFWriter.writeChunk("wsmp"), paramDLSSample.sampleoptions);
    RIFFWriter localRIFFWriter2;
    Object localObject;
    if (m != 0)
    {
      localRIFFWriter2 = paramRIFFWriter.writeChunk("data");
      localObject = AudioSystem.getAudioInputStream(localAudioFormat, (AudioInputStream)paramDLSSample.getData());
      byte[] arrayOfByte = new byte['Ѐ'];
      int i1;
      while ((i1 = ((AudioInputStream)localObject).read(arrayOfByte)) != -1) {
        localRIFFWriter2.write(arrayOfByte, 0, i1);
      }
    }
    else
    {
      localRIFFWriter2 = paramRIFFWriter.writeChunk("data");
      localObject = paramDLSSample.getDataBuffer();
      ((ModelByteBuffer)localObject).writeTo(localRIFFWriter2);
    }
    writeInfo(paramRIFFWriter.writeList("INFO"), paramDLSSample.info);
  }
  
  private void writeInstruments(RIFFWriter paramRIFFWriter)
    throws IOException
  {
    Iterator localIterator = this.instruments.iterator();
    while (localIterator.hasNext())
    {
      DLSInstrument localDLSInstrument = (DLSInstrument)localIterator.next();
      writeInstrument(paramRIFFWriter.writeList("ins "), localDLSInstrument);
    }
  }
  
  private void writeInstrument(RIFFWriter paramRIFFWriter, DLSInstrument paramDLSInstrument)
    throws IOException
  {
    int i = 0;
    int j = 0;
    Iterator localIterator = paramDLSInstrument.getModulators().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (DLSModulator)localIterator.next();
      if (((DLSModulator)localObject1).version == 1) {
        i++;
      }
      if (((DLSModulator)localObject1).version == 2) {
        j++;
      }
    }
    localIterator = paramDLSInstrument.regions.iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (DLSRegion)localIterator.next();
      localObject2 = ((DLSRegion)localObject1).getModulators().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (DLSModulator)((Iterator)localObject2).next();
        if (((DLSModulator)localObject3).version == 1) {
          i++;
        }
        if (((DLSModulator)localObject3).version == 2) {
          j++;
        }
      }
    }
    int k = 1;
    if (j > 0) {
      k = 2;
    }
    Object localObject1 = paramRIFFWriter.writeChunk("insh");
    ((RIFFWriter)localObject1).writeUnsignedInt(paramDLSInstrument.getRegions().size());
    ((RIFFWriter)localObject1).writeUnsignedInt(paramDLSInstrument.bank + (paramDLSInstrument.druminstrument ? 2147483648L : 0L));
    ((RIFFWriter)localObject1).writeUnsignedInt(paramDLSInstrument.preset);
    Object localObject2 = paramRIFFWriter.writeList("lrgn");
    Object localObject3 = paramDLSInstrument.regions.iterator();
    while (((Iterator)localObject3).hasNext())
    {
      DLSRegion localDLSRegion = (DLSRegion)((Iterator)localObject3).next();
      writeRegion((RIFFWriter)localObject2, localDLSRegion, k);
    }
    writeArticulators(paramRIFFWriter, paramDLSInstrument.getModulators());
    writeInfo(paramRIFFWriter.writeList("INFO"), paramDLSInstrument.info);
  }
  
  private void writeArticulators(RIFFWriter paramRIFFWriter, List<DLSModulator> paramList)
    throws IOException
  {
    int i = 0;
    int j = 0;
    Object localObject1 = paramList.iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (DLSModulator)((Iterator)localObject1).next();
      if (((DLSModulator)localObject2).version == 1) {
        i++;
      }
      if (((DLSModulator)localObject2).version == 2) {
        j++;
      }
    }
    Iterator localIterator;
    DLSModulator localDLSModulator;
    if (i > 0)
    {
      localObject1 = paramRIFFWriter.writeList("lart");
      localObject2 = ((RIFFWriter)localObject1).writeChunk("art1");
      ((RIFFWriter)localObject2).writeUnsignedInt(8L);
      ((RIFFWriter)localObject2).writeUnsignedInt(i);
      localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        localDLSModulator = (DLSModulator)localIterator.next();
        if (localDLSModulator.version == 1)
        {
          ((RIFFWriter)localObject2).writeUnsignedShort(localDLSModulator.source);
          ((RIFFWriter)localObject2).writeUnsignedShort(localDLSModulator.control);
          ((RIFFWriter)localObject2).writeUnsignedShort(localDLSModulator.destination);
          ((RIFFWriter)localObject2).writeUnsignedShort(localDLSModulator.transform);
          ((RIFFWriter)localObject2).writeInt(localDLSModulator.scale);
        }
      }
    }
    if (j > 0)
    {
      localObject1 = paramRIFFWriter.writeList("lar2");
      localObject2 = ((RIFFWriter)localObject1).writeChunk("art2");
      ((RIFFWriter)localObject2).writeUnsignedInt(8L);
      ((RIFFWriter)localObject2).writeUnsignedInt(j);
      localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        localDLSModulator = (DLSModulator)localIterator.next();
        if (localDLSModulator.version == 2)
        {
          ((RIFFWriter)localObject2).writeUnsignedShort(localDLSModulator.source);
          ((RIFFWriter)localObject2).writeUnsignedShort(localDLSModulator.control);
          ((RIFFWriter)localObject2).writeUnsignedShort(localDLSModulator.destination);
          ((RIFFWriter)localObject2).writeUnsignedShort(localDLSModulator.transform);
          ((RIFFWriter)localObject2).writeInt(localDLSModulator.scale);
        }
      }
    }
  }
  
  private void writeRegion(RIFFWriter paramRIFFWriter, DLSRegion paramDLSRegion, int paramInt)
    throws IOException
  {
    RIFFWriter localRIFFWriter1 = null;
    if (paramInt == 1) {
      localRIFFWriter1 = paramRIFFWriter.writeList("rgn ");
    }
    if (paramInt == 2) {
      localRIFFWriter1 = paramRIFFWriter.writeList("rgn2");
    }
    if (localRIFFWriter1 == null) {
      return;
    }
    RIFFWriter localRIFFWriter2 = localRIFFWriter1.writeChunk("rgnh");
    localRIFFWriter2.writeUnsignedShort(paramDLSRegion.keyfrom);
    localRIFFWriter2.writeUnsignedShort(paramDLSRegion.keyto);
    localRIFFWriter2.writeUnsignedShort(paramDLSRegion.velfrom);
    localRIFFWriter2.writeUnsignedShort(paramDLSRegion.velto);
    localRIFFWriter2.writeUnsignedShort(paramDLSRegion.options);
    localRIFFWriter2.writeUnsignedShort(paramDLSRegion.exclusiveClass);
    if (paramDLSRegion.sampleoptions != null) {
      writeSampleOptions(localRIFFWriter1.writeChunk("wsmp"), paramDLSRegion.sampleoptions);
    }
    if ((paramDLSRegion.sample != null) && (this.samples.indexOf(paramDLSRegion.sample) != -1))
    {
      RIFFWriter localRIFFWriter3 = localRIFFWriter1.writeChunk("wlnk");
      localRIFFWriter3.writeUnsignedShort(paramDLSRegion.fusoptions);
      localRIFFWriter3.writeUnsignedShort(paramDLSRegion.phasegroup);
      localRIFFWriter3.writeUnsignedInt(paramDLSRegion.channel);
      localRIFFWriter3.writeUnsignedInt(this.samples.indexOf(paramDLSRegion.sample));
    }
    writeArticulators(localRIFFWriter1, paramDLSRegion.getModulators());
    localRIFFWriter1.close();
  }
  
  private void writeSampleOptions(RIFFWriter paramRIFFWriter, DLSSampleOptions paramDLSSampleOptions)
    throws IOException
  {
    paramRIFFWriter.writeUnsignedInt(20L);
    paramRIFFWriter.writeUnsignedShort(paramDLSSampleOptions.unitynote);
    paramRIFFWriter.writeShort(paramDLSSampleOptions.finetune);
    paramRIFFWriter.writeInt(paramDLSSampleOptions.attenuation);
    paramRIFFWriter.writeUnsignedInt(paramDLSSampleOptions.options);
    paramRIFFWriter.writeInt(paramDLSSampleOptions.loops.size());
    Iterator localIterator = paramDLSSampleOptions.loops.iterator();
    while (localIterator.hasNext())
    {
      DLSSampleLoop localDLSSampleLoop = (DLSSampleLoop)localIterator.next();
      paramRIFFWriter.writeUnsignedInt(16L);
      paramRIFFWriter.writeUnsignedInt(localDLSSampleLoop.type);
      paramRIFFWriter.writeUnsignedInt(localDLSSampleLoop.start);
      paramRIFFWriter.writeUnsignedInt(localDLSSampleLoop.length);
    }
  }
  
  private void writeInfoStringChunk(RIFFWriter paramRIFFWriter, String paramString1, String paramString2)
    throws IOException
  {
    if (paramString2 == null) {
      return;
    }
    RIFFWriter localRIFFWriter = paramRIFFWriter.writeChunk(paramString1);
    localRIFFWriter.writeString(paramString2);
    int i = paramString2.getBytes("ascii").length;
    localRIFFWriter.write(0);
    i++;
    if (i % 2 != 0) {
      localRIFFWriter.write(0);
    }
  }
  
  private void writeInfo(RIFFWriter paramRIFFWriter, DLSInfo paramDLSInfo)
    throws IOException
  {
    writeInfoStringChunk(paramRIFFWriter, "INAM", paramDLSInfo.name);
    writeInfoStringChunk(paramRIFFWriter, "ICRD", paramDLSInfo.creationDate);
    writeInfoStringChunk(paramRIFFWriter, "IENG", paramDLSInfo.engineers);
    writeInfoStringChunk(paramRIFFWriter, "IPRD", paramDLSInfo.product);
    writeInfoStringChunk(paramRIFFWriter, "ICOP", paramDLSInfo.copyright);
    writeInfoStringChunk(paramRIFFWriter, "ICMT", paramDLSInfo.comments);
    writeInfoStringChunk(paramRIFFWriter, "ISFT", paramDLSInfo.tools);
    writeInfoStringChunk(paramRIFFWriter, "IARL", paramDLSInfo.archival_location);
    writeInfoStringChunk(paramRIFFWriter, "IART", paramDLSInfo.artist);
    writeInfoStringChunk(paramRIFFWriter, "ICMS", paramDLSInfo.commissioned);
    writeInfoStringChunk(paramRIFFWriter, "IGNR", paramDLSInfo.genre);
    writeInfoStringChunk(paramRIFFWriter, "IKEY", paramDLSInfo.keywords);
    writeInfoStringChunk(paramRIFFWriter, "IMED", paramDLSInfo.medium);
    writeInfoStringChunk(paramRIFFWriter, "ISBJ", paramDLSInfo.subject);
    writeInfoStringChunk(paramRIFFWriter, "ISRC", paramDLSInfo.source);
    writeInfoStringChunk(paramRIFFWriter, "ISRF", paramDLSInfo.source_form);
    writeInfoStringChunk(paramRIFFWriter, "ITCH", paramDLSInfo.technician);
  }
  
  public DLSInfo getInfo()
  {
    return this.info;
  }
  
  public String getName()
  {
    return this.info.name;
  }
  
  public String getVersion()
  {
    return this.major + "." + this.minor;
  }
  
  public String getVendor()
  {
    return this.info.engineers;
  }
  
  public String getDescription()
  {
    return this.info.comments;
  }
  
  public void setName(String paramString)
  {
    this.info.name = paramString;
  }
  
  public void setVendor(String paramString)
  {
    this.info.engineers = paramString;
  }
  
  public void setDescription(String paramString)
  {
    this.info.comments = paramString;
  }
  
  public SoundbankResource[] getResources()
  {
    SoundbankResource[] arrayOfSoundbankResource = new SoundbankResource[this.samples.size()];
    int i = 0;
    for (int j = 0; j < this.samples.size(); j++) {
      arrayOfSoundbankResource[(i++)] = ((SoundbankResource)this.samples.get(j));
    }
    return arrayOfSoundbankResource;
  }
  
  public DLSInstrument[] getInstruments()
  {
    DLSInstrument[] arrayOfDLSInstrument = (DLSInstrument[])this.instruments.toArray(new DLSInstrument[this.instruments.size()]);
    Arrays.sort(arrayOfDLSInstrument, new ModelInstrumentComparator());
    return arrayOfDLSInstrument;
  }
  
  public DLSSample[] getSamples()
  {
    return (DLSSample[])this.samples.toArray(new DLSSample[this.samples.size()]);
  }
  
  public Instrument getInstrument(Patch paramPatch)
  {
    int i = paramPatch.getProgram();
    int j = paramPatch.getBank();
    boolean bool1 = false;
    if ((paramPatch instanceof ModelPatch)) {
      bool1 = ((ModelPatch)paramPatch).isPercussion();
    }
    Iterator localIterator = this.instruments.iterator();
    while (localIterator.hasNext())
    {
      Instrument localInstrument = (Instrument)localIterator.next();
      Patch localPatch = localInstrument.getPatch();
      int k = localPatch.getProgram();
      int m = localPatch.getBank();
      if ((i == k) && (j == m))
      {
        boolean bool2 = false;
        if ((localPatch instanceof ModelPatch)) {
          bool2 = ((ModelPatch)localPatch).isPercussion();
        }
        if (bool1 == bool2) {
          return localInstrument;
        }
      }
    }
    return null;
  }
  
  public void addResource(SoundbankResource paramSoundbankResource)
  {
    if ((paramSoundbankResource instanceof DLSInstrument)) {
      this.instruments.add((DLSInstrument)paramSoundbankResource);
    }
    if ((paramSoundbankResource instanceof DLSSample)) {
      this.samples.add((DLSSample)paramSoundbankResource);
    }
  }
  
  public void removeResource(SoundbankResource paramSoundbankResource)
  {
    if ((paramSoundbankResource instanceof DLSInstrument)) {
      this.instruments.remove((DLSInstrument)paramSoundbankResource);
    }
    if ((paramSoundbankResource instanceof DLSSample)) {
      this.samples.remove((DLSSample)paramSoundbankResource);
    }
  }
  
  public void addInstrument(DLSInstrument paramDLSInstrument)
  {
    this.instruments.add(paramDLSInstrument);
  }
  
  public void removeInstrument(DLSInstrument paramDLSInstrument)
  {
    this.instruments.remove(paramDLSInstrument);
  }
  
  public long getMajor()
  {
    return this.major;
  }
  
  public void setMajor(long paramLong)
  {
    this.major = paramLong;
  }
  
  public long getMinor()
  {
    return this.minor;
  }
  
  public void setMinor(long paramLong)
  {
    this.minor = paramLong;
  }
  
  private static class DLSID
  {
    long i1;
    int s1;
    int s2;
    int x1;
    int x2;
    int x3;
    int x4;
    int x5;
    int x6;
    int x7;
    int x8;
    
    private DLSID() {}
    
    DLSID(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10)
    {
      this.i1 = paramLong;
      this.s1 = paramInt1;
      this.s2 = paramInt2;
      this.x1 = paramInt3;
      this.x2 = paramInt4;
      this.x3 = paramInt5;
      this.x4 = paramInt6;
      this.x5 = paramInt7;
      this.x6 = paramInt8;
      this.x7 = paramInt9;
      this.x8 = paramInt10;
    }
    
    public static DLSID read(RIFFReader paramRIFFReader)
      throws IOException
    {
      DLSID localDLSID = new DLSID();
      localDLSID.i1 = paramRIFFReader.readUnsignedInt();
      localDLSID.s1 = paramRIFFReader.readUnsignedShort();
      localDLSID.s2 = paramRIFFReader.readUnsignedShort();
      localDLSID.x1 = paramRIFFReader.readUnsignedByte();
      localDLSID.x2 = paramRIFFReader.readUnsignedByte();
      localDLSID.x3 = paramRIFFReader.readUnsignedByte();
      localDLSID.x4 = paramRIFFReader.readUnsignedByte();
      localDLSID.x5 = paramRIFFReader.readUnsignedByte();
      localDLSID.x6 = paramRIFFReader.readUnsignedByte();
      localDLSID.x7 = paramRIFFReader.readUnsignedByte();
      localDLSID.x8 = paramRIFFReader.readUnsignedByte();
      return localDLSID;
    }
    
    public int hashCode()
    {
      return (int)this.i1;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof DLSID)) {
        return false;
      }
      DLSID localDLSID = (DLSID)paramObject;
      return (this.i1 == localDLSID.i1) && (this.s1 == localDLSID.s1) && (this.s2 == localDLSID.s2) && (this.x1 == localDLSID.x1) && (this.x2 == localDLSID.x2) && (this.x3 == localDLSID.x3) && (this.x4 == localDLSID.x4) && (this.x5 == localDLSID.x5) && (this.x6 == localDLSID.x6) && (this.x7 == localDLSID.x7) && (this.x8 == localDLSID.x8);
    }
  }
}
