package roadmapproject;

public interface ProgressFrameInterface{
  public int getProgressStatus();
  public String getProgressTitle();
  public String getCurrentOperation();
  public boolean done();
}