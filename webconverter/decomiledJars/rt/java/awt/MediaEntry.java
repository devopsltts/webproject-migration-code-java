package java.awt;

abstract class MediaEntry
{
  MediaTracker tracker;
  int ID;
  MediaEntry next;
  int status;
  boolean cancelled;
  static final int LOADING = 1;
  static final int ABORTED = 2;
  static final int ERRORED = 4;
  static final int COMPLETE = 8;
  static final int LOADSTARTED = 13;
  static final int DONE = 14;
  
  MediaEntry(MediaTracker paramMediaTracker, int paramInt)
  {
    this.tracker = paramMediaTracker;
    this.ID = paramInt;
  }
  
  abstract Object getMedia();
  
  static MediaEntry insert(MediaEntry paramMediaEntry1, MediaEntry paramMediaEntry2)
  {
    MediaEntry localMediaEntry1 = paramMediaEntry1;
    MediaEntry localMediaEntry2 = null;
    while ((localMediaEntry1 != null) && (localMediaEntry1.ID <= paramMediaEntry2.ID))
    {
      localMediaEntry2 = localMediaEntry1;
      localMediaEntry1 = localMediaEntry1.next;
    }
    paramMediaEntry2.next = localMediaEntry1;
    if (localMediaEntry2 == null) {
      paramMediaEntry1 = paramMediaEntry2;
    } else {
      localMediaEntry2.next = paramMediaEntry2;
    }
    return paramMediaEntry1;
  }
  
  int getID()
  {
    return this.ID;
  }
  
  abstract void startLoad();
  
  void cancel()
  {
    this.cancelled = true;
  }
  
  synchronized int getStatus(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && ((this.status & 0xD) == 0))
    {
      this.status = (this.status & 0xFFFFFFFD | 0x1);
      startLoad();
    }
    return this.status;
  }
  
  void setStatus(int paramInt)
  {
    synchronized (this)
    {
      this.status = paramInt;
    }
    this.tracker.setDone();
  }
}
