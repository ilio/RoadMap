package roadmapproject;

public interface MultyLanguage {
    public String getCode();
    public String getCurrName();
    public void setCode(String code);
    public void setCurrName(String currName);
    public boolean getVisibilityName();
    public void setVisibilityName(boolean visibility);
    public void setType(int type);
    public int getType();
    public String[] getTypeNames();
}