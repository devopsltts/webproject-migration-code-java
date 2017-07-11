package javax.security.auth.callback;

import java.io.IOException;

public abstract interface CallbackHandler
{
  public abstract void handle(Callback[] paramArrayOfCallback)
    throws IOException, UnsupportedCallbackException;
}
