package javax.swing.text.rtf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

public class RTFEditorKit
  extends StyledEditorKit
{
  public RTFEditorKit() {}
  
  public String getContentType()
  {
    return "text/rtf";
  }
  
  public void read(InputStream paramInputStream, Document paramDocument, int paramInt)
    throws IOException, BadLocationException
  {
    if ((paramDocument instanceof StyledDocument))
    {
      RTFReader localRTFReader = new RTFReader((StyledDocument)paramDocument);
      localRTFReader.readFromStream(paramInputStream);
      localRTFReader.close();
    }
    else
    {
      super.read(paramInputStream, paramDocument, paramInt);
    }
  }
  
  public void write(OutputStream paramOutputStream, Document paramDocument, int paramInt1, int paramInt2)
    throws IOException, BadLocationException
  {
    RTFGenerator.writeDocument(paramDocument, paramOutputStream);
  }
  
  public void read(Reader paramReader, Document paramDocument, int paramInt)
    throws IOException, BadLocationException
  {
    if ((paramDocument instanceof StyledDocument))
    {
      RTFReader localRTFReader = new RTFReader((StyledDocument)paramDocument);
      localRTFReader.readFromReader(paramReader);
      localRTFReader.close();
    }
    else
    {
      super.read(paramReader, paramDocument, paramInt);
    }
  }
  
  public void write(Writer paramWriter, Document paramDocument, int paramInt1, int paramInt2)
    throws IOException, BadLocationException
  {
    throw new IOException("RTF is an 8-bit format");
  }
}
