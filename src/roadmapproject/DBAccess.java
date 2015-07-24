package roadmapproject;
import java.sql.*;
import java.awt.*;
public interface DBAccess extends LoadSaveCONTANTS{
    public boolean loadFields(Statement stmt,int indObj);
    public void saveFields(Statement stmt);
    public String[] getTablesConfiguration();
    public String[] getTableNames();
    public boolean isDead();
    public int getLoadPriority();
    public void remove();
    public CheckReport selfCheck();
    public CurveContainer getOutline();
}