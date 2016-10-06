import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;
import javax.sql.rowset.spi.SyncProvider;


public class MySyncProvider extends SyncProvider {

    private int dataSourceLock;

    /**
     * creates a default SyncProvider object.
     */
    public MySyncProvider() {
        System.out.println("MySyncProvider: constructor.");
        this.dataSourceLock = SyncProvider.DATASOURCE_NO_LOCK;
    }

    /**
     * Returns the current data source lock severity level active
     * in this SyncProvider implementation.
     */
    public int  getDataSourceLock() {
        return this.dataSourceLock;
    }

    /**
     * Returns a constant indicating the grade of synchronization a
     * RowSet object can expect from this SyncProvider object.
     */
    public int getProviderGrade() {
        return SyncProvider.GRADE_NONE;
    }

    /**
     * Returns the unique identifier for this SyncProvider object.
     */
    public String  getProviderID() {
        String id = getClass().getName();
        System.out.println("--- MySyncProvider: getProviderID() ="+id);
        return id; //"MySyncProvider";
    }


    /**
     * Returns a javax.sql.RowSetReader object, which can be used to
     * populate a RowSet object with data.
     */
    public RowSetReader  getRowSetReader() {
        System.out.println("--- MySyncProvider: getRowSetReader() ---");
        return new CustomRowSetReader();
    }

    /**
     * Returns a javax.sql.RowSetWriter object, which can be used to
     * write a RowSet object's data back to the underlying data source.
     */
    public RowSetWriter  getRowSetWriter() {
        System.out.println("--- MySyncProvider: getRowSetWriter() ---");
        return new CustomRowSetWriter();
    }

    /**
     * Returns the vendor name of this SyncProvider instance
     */
    public String getVendor() {
        return "custom-made";
    }

    /**
     * Returns the release version of this SyncProvider instance.
     */
    public String getVersion() {
        return "1.0";
    }

    /**
     * Sets a lock on the underlying data source at the level
     * indicated by datasourceLock.
     */
    public void  setDataSourceLock(int dataSourceLock) {
        this.dataSourceLock = dataSourceLock;
    }

    /**
     * Returns whether this SyncProvider implementation can perform
     * synchronization between a RowSet object and the SQL VIEW in
     * the data source from which the RowSet object got its data.
     */
    public int supportsUpdatableView() {
        return SyncProvider.NONUPDATABLE_VIEW_SYNC;
    }
}