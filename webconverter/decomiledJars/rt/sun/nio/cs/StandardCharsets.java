package sun.nio.cs;

import java.nio.charset.Charset;
import sun.util.PreHashedMap;

public class StandardCharsets
  extends FastCharsetProvider
{
  static final String[] aliases_US_ASCII = { "iso-ir-6", "ANSI_X3.4-1986", "ISO_646.irv:1991", "ASCII", "ISO646-US", "us", "IBM367", "cp367", "csASCII", "default", "646", "iso_646.irv:1983", "ANSI_X3.4-1968", "ascii7" };
  static final String[] aliases_UTF_8 = { "UTF8", "unicode-1-1-utf-8" };
  static final String[] aliases_CESU_8 = { "CESU8", "csCESU-8" };
  static final String[] aliases_UTF_16 = { "UTF_16", "utf16", "unicode", "UnicodeBig" };
  static final String[] aliases_UTF_16BE = { "UTF_16BE", "ISO-10646-UCS-2", "X-UTF-16BE", "UnicodeBigUnmarked" };
  static final String[] aliases_UTF_16LE = { "UTF_16LE", "X-UTF-16LE", "UnicodeLittleUnmarked" };
  static final String[] aliases_UTF_16LE_BOM = { "UnicodeLittle" };
  static final String[] aliases_UTF_32 = { "UTF_32", "UTF32" };
  static final String[] aliases_UTF_32LE = { "UTF_32LE", "X-UTF-32LE" };
  static final String[] aliases_UTF_32BE = { "UTF_32BE", "X-UTF-32BE" };
  static final String[] aliases_UTF_32LE_BOM = { "UTF_32LE_BOM", "UTF-32LE-BOM" };
  static final String[] aliases_UTF_32BE_BOM = { "UTF_32BE_BOM", "UTF-32BE-BOM" };
  static final String[] aliases_ISO_8859_1 = { "iso-ir-100", "ISO_8859-1", "latin1", "l1", "IBM819", "cp819", "csISOLatin1", "819", "IBM-819", "ISO8859_1", "ISO_8859-1:1987", "ISO_8859_1", "8859_1", "ISO8859-1" };
  static final String[] aliases_ISO_8859_2 = { "iso8859_2", "8859_2", "iso-ir-101", "ISO_8859-2", "ISO_8859-2:1987", "ISO8859-2", "latin2", "l2", "ibm912", "ibm-912", "cp912", "912", "csISOLatin2" };
  static final String[] aliases_ISO_8859_4 = { "iso8859_4", "iso8859-4", "8859_4", "iso-ir-110", "ISO_8859-4", "ISO_8859-4:1988", "latin4", "l4", "ibm914", "ibm-914", "cp914", "914", "csISOLatin4" };
  static final String[] aliases_ISO_8859_5 = { "iso8859_5", "8859_5", "iso-ir-144", "ISO_8859-5", "ISO_8859-5:1988", "ISO8859-5", "cyrillic", "ibm915", "ibm-915", "cp915", "915", "csISOLatinCyrillic" };
  static final String[] aliases_ISO_8859_7 = { "iso8859_7", "8859_7", "iso-ir-126", "ISO_8859-7", "ISO_8859-7:1987", "ELOT_928", "ECMA-118", "greek", "greek8", "csISOLatinGreek", "sun_eu_greek", "ibm813", "ibm-813", "813", "cp813", "iso8859-7" };
  static final String[] aliases_ISO_8859_9 = { "iso8859_9", "8859_9", "iso-ir-148", "ISO_8859-9", "ISO_8859-9:1989", "ISO8859-9", "latin5", "l5", "ibm920", "ibm-920", "920", "cp920", "csISOLatin5" };
  static final String[] aliases_ISO_8859_13 = { "iso8859_13", "8859_13", "iso_8859-13", "ISO8859-13" };
  static final String[] aliases_ISO_8859_15 = { "ISO_8859-15", "8859_15", "ISO-8859-15", "ISO8859_15", "ISO8859-15", "IBM923", "IBM-923", "cp923", "923", "LATIN0", "LATIN9", "L9", "csISOlatin0", "csISOlatin9", "ISO8859_15_FDIS" };
  static final String[] aliases_KOI8_R = { "koi8_r", "koi8", "cskoi8r" };
  static final String[] aliases_KOI8_U = { "koi8_u" };
  static final String[] aliases_MS1250 = { "cp1250", "cp5346" };
  static final String[] aliases_MS1251 = { "cp1251", "cp5347", "ansi-1251" };
  static final String[] aliases_MS1252 = { "cp1252", "cp5348" };
  static final String[] aliases_MS1253 = { "cp1253", "cp5349" };
  static final String[] aliases_MS1254 = { "cp1254", "cp5350" };
  static final String[] aliases_MS1257 = { "cp1257", "cp5353" };
  static final String[] aliases_IBM437 = { "cp437", "ibm437", "ibm-437", "437", "cspc8codepage437", "windows-437" };
  static final String[] aliases_IBM737 = { "cp737", "ibm737", "ibm-737", "737" };
  static final String[] aliases_IBM775 = { "cp775", "ibm775", "ibm-775", "775" };
  static final String[] aliases_IBM850 = { "cp850", "ibm-850", "ibm850", "850", "cspc850multilingual" };
  static final String[] aliases_IBM852 = { "cp852", "ibm852", "ibm-852", "852", "csPCp852" };
  static final String[] aliases_IBM855 = { "cp855", "ibm-855", "ibm855", "855", "cspcp855" };
  static final String[] aliases_IBM857 = { "cp857", "ibm857", "ibm-857", "857", "csIBM857" };
  static final String[] aliases_IBM858 = { "cp858", "ccsid00858", "cp00858", "858", "PC-Multilingual-850+euro" };
  static final String[] aliases_IBM862 = { "cp862", "ibm862", "ibm-862", "862", "csIBM862", "cspc862latinhebrew" };
  static final String[] aliases_IBM866 = { "cp866", "ibm866", "ibm-866", "866", "csIBM866" };
  static final String[] aliases_IBM874 = { "cp874", "ibm874", "ibm-874", "874" };
  
  public StandardCharsets()
  {
    super("sun.nio.cs", new Aliases(null), new Classes(null), new Cache(null));
  }
  
  private static final class Aliases
    extends PreHashedMap<String>
  {
    private static final int ROWS = 1024;
    private static final int SIZE = 211;
    private static final int SHIFT = 0;
    private static final int MASK = 1023;
    
    private Aliases()
    {
      super(211, 0, 1023);
    }
    
    protected void init(Object[] paramArrayOfObject)
    {
      paramArrayOfObject[1] = { "csisolatin0", "iso-8859-15" };
      paramArrayOfObject[2] = { "csisolatin1", "iso-8859-1" };
      paramArrayOfObject[3] = { "csisolatin2", "iso-8859-2" };
      paramArrayOfObject[5] = { "csisolatin4", "iso-8859-4" };
      paramArrayOfObject[6] = { "csisolatin5", "iso-8859-9" };
      paramArrayOfObject[10] = { "csisolatin9", "iso-8859-15" };
      paramArrayOfObject[19] = { "unicodelittle", "x-utf-16le-bom" };
      paramArrayOfObject[24] = { "iso646-us", "us-ascii" };
      paramArrayOfObject[25] = { "iso_8859-7:1987", "iso-8859-7" };
      paramArrayOfObject[26] = { "912", "iso-8859-2" };
      paramArrayOfObject[28] = { "914", "iso-8859-4" };
      paramArrayOfObject[29] = { "915", "iso-8859-5" };
      paramArrayOfObject[55] = { "920", "iso-8859-9" };
      paramArrayOfObject[58] = { "923", "iso-8859-15" };
      paramArrayOfObject[86] = { "csisolatincyrillic", "iso-8859-5", { "8859_1", "iso-8859-1" } };
      paramArrayOfObject[87] = { "8859_2", "iso-8859-2" };
      paramArrayOfObject[89] = { "8859_4", "iso-8859-4" };
      paramArrayOfObject[90] = { "813", "iso-8859-7", { "8859_5", "iso-8859-5" } };
      paramArrayOfObject[92] = { "8859_7", "iso-8859-7" };
      paramArrayOfObject[94] = { "8859_9", "iso-8859-9" };
      paramArrayOfObject[95] = { "iso_8859-1:1987", "iso-8859-1" };
      paramArrayOfObject[96] = { "819", "iso-8859-1" };
      paramArrayOfObject[106] = { "unicode-1-1-utf-8", "utf-8" };
      paramArrayOfObject[121] = { "x-utf-16le", "utf-16le" };
      paramArrayOfObject[125] = { "ecma-118", "iso-8859-7" };
      paramArrayOfObject[''] = { "koi8_r", "koi8-r" };
      paramArrayOfObject[''] = { "koi8_u", "koi8-u" };
      paramArrayOfObject[''] = { "cp912", "iso-8859-2" };
      paramArrayOfObject[''] = { "cp914", "iso-8859-4" };
      paramArrayOfObject[''] = { "cp915", "iso-8859-5" };
      paramArrayOfObject['ª'] = { "cp920", "iso-8859-9" };
      paramArrayOfObject['­'] = { "cp923", "iso-8859-15" };
      paramArrayOfObject['±'] = { "utf_32le_bom", "x-utf-32le-bom" };
      paramArrayOfObject['À'] = { "utf_16be", "utf-16be" };
      paramArrayOfObject['Ç'] = { "cspc8codepage437", "ibm437", { "ansi-1251", "windows-1251" } };
      paramArrayOfObject['Í'] = { "cp813", "iso-8859-7" };
      paramArrayOfObject['Ó'] = { "850", "ibm850", { "cp819", "iso-8859-1" } };
      paramArrayOfObject['Õ'] = { "852", "ibm852" };
      paramArrayOfObject['Ø'] = { "855", "ibm855" };
      paramArrayOfObject['Ú'] = { "857", "ibm857", { "iso-ir-6", "us-ascii" } };
      paramArrayOfObject['Û'] = { "858", "ibm00858", { "737", "x-ibm737" } };
      paramArrayOfObject['á'] = { "csascii", "us-ascii" };
      paramArrayOfObject['ô'] = { "862", "ibm862" };
      paramArrayOfObject['ø'] = { "866", "ibm866" };
      paramArrayOfObject['ý'] = { "x-utf-32be", "utf-32be" };
      paramArrayOfObject['þ'] = { "iso_8859-2:1987", "iso-8859-2" };
      paramArrayOfObject['ă'] = { "unicodebig", "utf-16" };
      paramArrayOfObject['č'] = { "iso8859_15_fdis", "iso-8859-15" };
      paramArrayOfObject['ĕ'] = { "874", "x-ibm874" };
      paramArrayOfObject['Ę'] = { "unicodelittleunmarked", "utf-16le" };
      paramArrayOfObject['ě'] = { "iso8859_1", "iso-8859-1" };
      paramArrayOfObject['Ĝ'] = { "iso8859_2", "iso-8859-2" };
      paramArrayOfObject['Ğ'] = { "iso8859_4", "iso-8859-4" };
      paramArrayOfObject['ğ'] = { "iso8859_5", "iso-8859-5" };
      paramArrayOfObject['ġ'] = { "iso8859_7", "iso-8859-7" };
      paramArrayOfObject['ģ'] = { "iso8859_9", "iso-8859-9" };
      paramArrayOfObject['Ħ'] = { "ibm912", "iso-8859-2" };
      paramArrayOfObject['Ĩ'] = { "ibm914", "iso-8859-4" };
      paramArrayOfObject['ĩ'] = { "ibm915", "iso-8859-5" };
      paramArrayOfObject['ı'] = { "iso_8859-13", "iso-8859-13" };
      paramArrayOfObject['ĳ'] = { "iso_8859-15", "iso-8859-15" };
      paramArrayOfObject['ĸ'] = { "greek8", "iso-8859-7", { "646", "us-ascii" } };
      paramArrayOfObject['Ł'] = { "ibm-912", "iso-8859-2" };
      paramArrayOfObject['Ń'] = { "ibm920", "iso-8859-9", { "ibm-914", "iso-8859-4" } };
      paramArrayOfObject['ń'] = { "ibm-915", "iso-8859-5" };
      paramArrayOfObject['Ņ'] = { "l1", "iso-8859-1" };
      paramArrayOfObject['ņ'] = { "cp850", "ibm850", { "ibm923", "iso-8859-15", { "l2", "iso-8859-2" } } };
      paramArrayOfObject['Ň'] = { "cyrillic", "iso-8859-5" };
      paramArrayOfObject['ň'] = { "cp852", "ibm852", { "l4", "iso-8859-4" } };
      paramArrayOfObject['ŉ'] = { "l5", "iso-8859-9" };
      paramArrayOfObject['ŋ'] = { "cp855", "ibm855" };
      paramArrayOfObject['ō'] = { "cp857", "ibm857", { "l9", "iso-8859-15" } };
      paramArrayOfObject['Ŏ'] = { "cp858", "ibm00858", { "cp737", "x-ibm737" } };
      paramArrayOfObject['Ő'] = { "iso_8859_1", "iso-8859-1" };
      paramArrayOfObject['œ'] = { "koi8", "koi8-r" };
      paramArrayOfObject['ŕ'] = { "775", "ibm775" };
      paramArrayOfObject['ř'] = { "iso_8859-9:1989", "iso-8859-9" };
      paramArrayOfObject['Ş'] = { "ibm-920", "iso-8859-9" };
      paramArrayOfObject['š'] = { "ibm-923", "iso-8859-15" };
      paramArrayOfObject['Ŧ'] = { "ibm813", "iso-8859-7" };
      paramArrayOfObject['ŧ'] = { "cp862", "ibm862" };
      paramArrayOfObject['ū'] = { "cp866", "ibm866" };
      paramArrayOfObject['Ŭ'] = { "ibm819", "iso-8859-1" };
      paramArrayOfObject['ź'] = { "ansi_x3.4-1968", "us-ascii" };
      paramArrayOfObject['Ɓ'] = { "ibm-813", "iso-8859-7" };
      paramArrayOfObject['Ƈ'] = { "ibm-819", "iso-8859-1" };
      paramArrayOfObject['ƈ'] = { "cp874", "x-ibm874" };
      paramArrayOfObject['ƕ'] = { "iso-ir-100", "iso-8859-1" };
      paramArrayOfObject['Ɩ'] = { "iso-ir-101", "iso-8859-2" };
      paramArrayOfObject['Ƙ'] = { "437", "ibm437" };
      paramArrayOfObject['ƥ'] = { "iso-8859-15", "iso-8859-15" };
      paramArrayOfObject['Ƭ'] = { "latin0", "iso-8859-15" };
      paramArrayOfObject['ƭ'] = { "latin1", "iso-8859-1" };
      paramArrayOfObject['Ʈ'] = { "latin2", "iso-8859-2" };
      paramArrayOfObject['ư'] = { "latin4", "iso-8859-4" };
      paramArrayOfObject['Ʊ'] = { "latin5", "iso-8859-9" };
      paramArrayOfObject['ƴ'] = { "iso-ir-110", "iso-8859-4" };
      paramArrayOfObject['Ƶ'] = { "latin9", "iso-8859-15" };
      paramArrayOfObject['ƶ'] = { "ansi_x3.4-1986", "us-ascii" };
      paramArrayOfObject['ƻ'] = { "utf-32be-bom", "x-utf-32be-bom" };
      paramArrayOfObject['ǈ'] = { "cp775", "ibm775" };
      paramArrayOfObject['Ǚ'] = { "iso-ir-126", "iso-8859-7" };
      paramArrayOfObject['ǟ'] = { "ibm850", "ibm850" };
      paramArrayOfObject['ǡ'] = { "ibm852", "ibm852" };
      paramArrayOfObject['Ǥ'] = { "ibm855", "ibm855" };
      paramArrayOfObject['Ǧ'] = { "ibm857", "ibm857" };
      paramArrayOfObject['ǧ'] = { "ibm737", "x-ibm737" };
      paramArrayOfObject['Ƕ'] = { "utf_16le", "utf-16le" };
      paramArrayOfObject['Ǻ'] = { "ibm-850", "ibm850" };
      paramArrayOfObject['Ǽ'] = { "ibm-852", "ibm852" };
      paramArrayOfObject['ǿ'] = { "ibm-855", "ibm855" };
      paramArrayOfObject['Ȁ'] = { "ibm862", "ibm862" };
      paramArrayOfObject['ȁ'] = { "ibm-857", "ibm857" };
      paramArrayOfObject['Ȃ'] = { "ibm-737", "x-ibm737" };
      paramArrayOfObject['Ȅ'] = { "ibm866", "ibm866" };
      paramArrayOfObject['Ȉ'] = { "unicodebigunmarked", "utf-16be" };
      paramArrayOfObject['ȋ'] = { "cp437", "ibm437" };
      paramArrayOfObject['Ȍ'] = { "utf16", "utf-16" };
      paramArrayOfObject['ȕ'] = { "iso-ir-144", "iso-8859-5" };
      paramArrayOfObject['ș'] = { "iso-ir-148", "iso-8859-9" };
      paramArrayOfObject['ț'] = { "ibm-862", "ibm862" };
      paramArrayOfObject['ȟ'] = { "ibm-866", "ibm866" };
      paramArrayOfObject['ȡ'] = { "ibm874", "x-ibm874" };
      paramArrayOfObject['ȳ'] = { "x-utf-32le", "utf-32le" };
      paramArrayOfObject['ȼ'] = { "ibm-874", "x-ibm874" };
      paramArrayOfObject['Ƚ'] = { "iso_8859-4:1988", "iso-8859-4" };
      paramArrayOfObject['Ɂ'] = { "default", "us-ascii" };
      paramArrayOfObject['Ɇ'] = { "utf32", "utf-32" };
      paramArrayOfObject['ɇ'] = { "pc-multilingual-850+euro", "ibm00858" };
      paramArrayOfObject['Ɍ'] = { "elot_928", "iso-8859-7" };
      paramArrayOfObject['ɑ'] = { "csisolatingreek", "iso-8859-7" };
      paramArrayOfObject['ɖ'] = { "csibm857", "ibm857" };
      paramArrayOfObject['ɡ'] = { "ibm775", "ibm775" };
      paramArrayOfObject['ɩ'] = { "cp1250", "windows-1250" };
      paramArrayOfObject['ɪ'] = { "cp1251", "windows-1251" };
      paramArrayOfObject['ɫ'] = { "cp1252", "windows-1252" };
      paramArrayOfObject['ɬ'] = { "cp1253", "windows-1253" };
      paramArrayOfObject['ɭ'] = { "cp1254", "windows-1254" };
      paramArrayOfObject['ɰ'] = { "csibm862", "ibm862", { "cp1257", "windows-1257" } };
      paramArrayOfObject['ɴ'] = { "csibm866", "ibm866", { "cesu8", "cesu-8" } };
      paramArrayOfObject['ɸ'] = { "iso8859_13", "iso-8859-13" };
      paramArrayOfObject['ɺ'] = { "iso8859_15", "iso-8859-15", { "utf_32be", "utf-32be" } };
      paramArrayOfObject['ɻ'] = { "utf_32be_bom", "x-utf-32be-bom" };
      paramArrayOfObject['ɼ'] = { "ibm-775", "ibm775" };
      paramArrayOfObject['ʎ'] = { "cp00858", "ibm00858" };
      paramArrayOfObject['ʝ'] = { "8859_13", "iso-8859-13" };
      paramArrayOfObject['ʞ'] = { "us", "us-ascii" };
      paramArrayOfObject['ʟ'] = { "8859_15", "iso-8859-15" };
      paramArrayOfObject['ʤ'] = { "ibm437", "ibm437" };
      paramArrayOfObject['ʧ'] = { "cp367", "us-ascii" };
      paramArrayOfObject['ʮ'] = { "iso-10646-ucs-2", "utf-16be" };
      paramArrayOfObject['ʿ'] = { "ibm-437", "ibm437" };
      paramArrayOfObject['ˆ'] = { "iso8859-13", "iso-8859-13" };
      paramArrayOfObject['ˈ'] = { "iso8859-15", "iso-8859-15" };
      paramArrayOfObject['˜'] = { "iso_8859-5:1988", "iso-8859-5" };
      paramArrayOfObject['˝'] = { "unicode", "utf-16" };
      paramArrayOfObject['̀'] = { "greek", "iso-8859-7" };
      paramArrayOfObject['̆'] = { "ascii7", "us-ascii" };
      paramArrayOfObject['̍'] = { "iso8859-1", "iso-8859-1" };
      paramArrayOfObject['̎'] = { "iso8859-2", "iso-8859-2" };
      paramArrayOfObject['̏'] = { "cskoi8r", "koi8-r" };
      paramArrayOfObject['̐'] = { "iso8859-4", "iso-8859-4" };
      paramArrayOfObject['̑'] = { "iso8859-5", "iso-8859-5" };
      paramArrayOfObject['̓'] = { "iso8859-7", "iso-8859-7" };
      paramArrayOfObject['̕'] = { "iso8859-9", "iso-8859-9" };
      paramArrayOfObject['̭'] = { "ccsid00858", "ibm00858" };
      paramArrayOfObject['̲'] = { "cspc862latinhebrew", "ibm862" };
      paramArrayOfObject['̀'] = { "ibm367", "us-ascii" };
      paramArrayOfObject['͂'] = { "iso_8859-1", "iso-8859-1" };
      paramArrayOfObject['̓'] = { "iso_8859-2", "iso-8859-2", { "x-utf-16be", "utf-16be" } };
      paramArrayOfObject['̈́'] = { "sun_eu_greek", "iso-8859-7" };
      paramArrayOfObject['ͅ'] = { "iso_8859-4", "iso-8859-4" };
      paramArrayOfObject['͆'] = { "iso_8859-5", "iso-8859-5" };
      paramArrayOfObject['͈'] = { "cspcp852", "ibm852", { "iso_8859-7", "iso-8859-7" } };
      paramArrayOfObject['͊'] = { "iso_8859-9", "iso-8859-9" };
      paramArrayOfObject['͋'] = { "cspcp855", "ibm855" };
      paramArrayOfObject['͎'] = { "windows-437", "ibm437" };
      paramArrayOfObject['͑'] = { "ascii", "us-ascii" };
      paramArrayOfObject['͟'] = { "cscesu-8", "cesu-8" };
      paramArrayOfObject['ͱ'] = { "utf8", "utf-8" };
      paramArrayOfObject['΀'] = { "iso_646.irv:1983", "us-ascii" };
      paramArrayOfObject['΍'] = { "cp5346", "windows-1250" };
      paramArrayOfObject['Ύ'] = { "cp5347", "windows-1251" };
      paramArrayOfObject['Ώ'] = { "cp5348", "windows-1252" };
      paramArrayOfObject['ΐ'] = { "cp5349", "windows-1253" };
      paramArrayOfObject['Ν'] = { "iso_646.irv:1991", "us-ascii" };
      paramArrayOfObject['Φ'] = { "cp5350", "windows-1254" };
      paramArrayOfObject['Ω'] = { "cp5353", "windows-1257" };
      paramArrayOfObject['ΰ'] = { "utf_32le", "utf-32le" };
      paramArrayOfObject['ν'] = { "utf_16", "utf-16" };
      paramArrayOfObject['ϡ'] = { "cspc850multilingual", "ibm850" };
      paramArrayOfObject['ϱ'] = { "utf-32le-bom", "x-utf-32le-bom" };
      paramArrayOfObject['Ϸ'] = { "utf_32", "utf-32" };
    }
  }
  
  private static final class Cache
    extends PreHashedMap<Charset>
  {
    private static final int ROWS = 32;
    private static final int SIZE = 39;
    private static final int SHIFT = 1;
    private static final int MASK = 31;
    
    private Cache()
    {
      super(39, 1, 31);
    }
    
    protected void init(Object[] paramArrayOfObject)
    {
      paramArrayOfObject[0] = { "ibm862", null };
      paramArrayOfObject[2] = { "ibm866", null, { "utf-32", null, { "utf-16le", null } } };
      paramArrayOfObject[3] = { "windows-1251", null, { "windows-1250", null } };
      paramArrayOfObject[4] = { "windows-1253", null, { "windows-1252", null, { "utf-32be", null } } };
      paramArrayOfObject[5] = { "windows-1254", null, { "utf-16", null } };
      paramArrayOfObject[6] = { "windows-1257", null };
      paramArrayOfObject[7] = { "utf-16be", null };
      paramArrayOfObject[8] = { "iso-8859-2", null, { "iso-8859-1", null } };
      paramArrayOfObject[9] = { "iso-8859-4", null, { "utf-8", null } };
      paramArrayOfObject[10] = { "iso-8859-5", null };
      paramArrayOfObject[11] = { "x-ibm874", null, { "iso-8859-7", null } };
      paramArrayOfObject[12] = { "iso-8859-9", null };
      paramArrayOfObject[14] = { "x-ibm737", null };
      paramArrayOfObject[15] = { "ibm850", null };
      paramArrayOfObject[16] = { "ibm852", null, { "ibm775", null } };
      paramArrayOfObject[17] = { "iso-8859-13", null, { "us-ascii", null } };
      paramArrayOfObject[18] = { "ibm855", null, { "ibm437", null, { "iso-8859-15", null } } };
      paramArrayOfObject[19] = { "ibm00858", null, { "ibm857", null, { "x-utf-32le-bom", null } } };
      paramArrayOfObject[22] = { "x-utf-16le-bom", null };
      paramArrayOfObject[23] = { "cesu-8", null };
      paramArrayOfObject[24] = { "x-utf-32be-bom", null };
      paramArrayOfObject[28] = { "koi8-r", null };
      paramArrayOfObject[29] = { "koi8-u", null };
      paramArrayOfObject[31] = { "utf-32le", null };
    }
  }
  
  private static final class Classes
    extends PreHashedMap<String>
  {
    private static final int ROWS = 32;
    private static final int SIZE = 39;
    private static final int SHIFT = 1;
    private static final int MASK = 31;
    
    private Classes()
    {
      super(39, 1, 31);
    }
    
    protected void init(Object[] paramArrayOfObject)
    {
      paramArrayOfObject[0] = { "ibm862", "IBM862" };
      paramArrayOfObject[2] = { "ibm866", "IBM866", { "utf-32", "UTF_32", { "utf-16le", "UTF_16LE" } } };
      paramArrayOfObject[3] = { "windows-1251", "MS1251", { "windows-1250", "MS1250" } };
      paramArrayOfObject[4] = { "windows-1253", "MS1253", { "windows-1252", "MS1252", { "utf-32be", "UTF_32BE" } } };
      paramArrayOfObject[5] = { "windows-1254", "MS1254", { "utf-16", "UTF_16" } };
      paramArrayOfObject[6] = { "windows-1257", "MS1257" };
      paramArrayOfObject[7] = { "utf-16be", "UTF_16BE" };
      paramArrayOfObject[8] = { "iso-8859-2", "ISO_8859_2", { "iso-8859-1", "ISO_8859_1" } };
      paramArrayOfObject[9] = { "iso-8859-4", "ISO_8859_4", { "utf-8", "UTF_8" } };
      paramArrayOfObject[10] = { "iso-8859-5", "ISO_8859_5" };
      paramArrayOfObject[11] = { "x-ibm874", "IBM874", { "iso-8859-7", "ISO_8859_7" } };
      paramArrayOfObject[12] = { "iso-8859-9", "ISO_8859_9" };
      paramArrayOfObject[14] = { "x-ibm737", "IBM737" };
      paramArrayOfObject[15] = { "ibm850", "IBM850" };
      paramArrayOfObject[16] = { "ibm852", "IBM852", { "ibm775", "IBM775" } };
      paramArrayOfObject[17] = { "iso-8859-13", "ISO_8859_13", { "us-ascii", "US_ASCII" } };
      paramArrayOfObject[18] = { "ibm855", "IBM855", { "ibm437", "IBM437", { "iso-8859-15", "ISO_8859_15" } } };
      paramArrayOfObject[19] = { "ibm00858", "IBM858", { "ibm857", "IBM857", { "x-utf-32le-bom", "UTF_32LE_BOM" } } };
      paramArrayOfObject[22] = { "x-utf-16le-bom", "UTF_16LE_BOM" };
      paramArrayOfObject[23] = { "cesu-8", "CESU_8" };
      paramArrayOfObject[24] = { "x-utf-32be-bom", "UTF_32BE_BOM" };
      paramArrayOfObject[28] = { "koi8-r", "KOI8_R" };
      paramArrayOfObject[29] = { "koi8-u", "KOI8_U" };
      paramArrayOfObject[31] = { "utf-32le", "UTF_32LE" };
    }
  }
}
