import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;

public class ExampleListener implements RowSetListener {

  public void cursorMoved(RowSetEvent event) {
    System.out.println("ExampleListener notified of cursorMoved event");
    System.out.println(event.toString());
  }

  public void rowChanged(RowSetEvent event) {
    System.out.println("ExampleListener notified of rowChanged event");
    System.out.println(event.toString());
  }

  public void rowSetChanged(RowSetEvent event) {
    System.out.println("ExampleListener notified of rowSetChanged event");
    System.out.println(event.toString());
  }
}


