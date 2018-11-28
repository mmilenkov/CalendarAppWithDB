import java.time.LocalDateTime;

public class Event {
    private String name;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int id;

    Event(int d,int m, int y, String name, String location, String startTime, String endTime) {
        this.name = name;
        this.location = location;
        startDate = LocalDateTime.of(y,m,d,Integer.valueOf(startTime.substring(0,2)),Integer.valueOf(startTime.substring(3)));
        endDate = LocalDateTime.of(y,m,d,Integer.valueOf(endTime.substring(0,2)),Integer.valueOf(endTime.substring(3)));
    }

    Event (int id,String name, LocalDateTime startDate, LocalDateTime endDate, String location) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.id = id;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }

    String getLocation() {
        return location;
    }

    LocalDateTime getStartDate () {
        return startDate;
    }

    LocalDateTime getEndDate () {
        return endDate;
    }

}
