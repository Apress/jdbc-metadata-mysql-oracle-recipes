import java.sql.SQLException;
import javax.sql.RowSet;
import javax.sql.rowset.Predicate;
import javax.sql.rowset.FilteredRowSet;


public class AgeFilter implements Predicate {
    private int lowAge;
    private int highAge;
    private int columnIndex;
    private String columnName;

    public AgeFilter(int lowAge, int highAge, int columnIndex, String columnName) {
        this.lowAge = lowAge;
        this.highAge = highAge;
        this.columnName = columnName;
        this.columnIndex = columnIndex;
    }

    public AgeFilter(int lowAge, int highAge, int columnIndex) {
        this(lowAge, highAge, columnIndex, "age");
    }

    public boolean evaluate(Object value, String columnName) {
        boolean evaluation = true;
        if (columnName.equalsIgnoreCase(this.columnName)) {
            int columnValue = ((Integer)value).intValue();
            if ((columnValue >= this.lowAge) && (columnValue <= this.highAge)) {
                evaluation = true;
            }
            else {
                evaluation = false;
            }
        }
        return evaluation;
    }


    public boolean evaluate(Object value, int columnNumber) {
        boolean evaluation = true;
        if (columnIndex == columnNumber) {
            int columnValue = ((Integer)value).intValue();
            if ((columnValue >= this.lowAge) && (columnValue <= this.highAge)) {
                evaluation = true;
            }
            else {
                evaluation = false;
            }
        }
        return evaluation;
    }

    public boolean evaluate(RowSet rs) {
        if (rs == null) {
            return false;
        }

        FilteredRowSet frs = (FilteredRowSet) rs;
        boolean evaluation = false;
        try {
            int columnValue = frs.getInt(this.columnIndex);
            if ((columnValue >= this.lowAge) && (columnValue <= this.highAge)) {
                evaluation = true;
            }
            //System.out.println("3-evaluate: columnValue="+columnValue+" columnName="+columnName+" = "+evaluation);
        }
        catch (SQLException e) {
            return false;
        }
        return evaluation;
    }
}