package DcrsApp;

/**
* DcrsApp/DcrsHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from E:/Suhel/workspace/DcrsUsingCorba/src/dcrs.idl
* Wednesday, 24 October, 2018 4:11:04 PM EDT
*/

public final class DcrsHolder implements org.omg.CORBA.portable.Streamable
{
  public DcrsApp.Dcrs value = null;

  public DcrsHolder ()
  {
  }

  public DcrsHolder (DcrsApp.Dcrs initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = DcrsApp.DcrsHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    DcrsApp.DcrsHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return DcrsApp.DcrsHelper.type ();
  }

}
