import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "event",schema = "event_list")
public class Event {

    private String name;

    @Transient
    private String location;

    @ManyToOne (optional = false)
    @JoinColumn(name = "location_id" , insertable = false,updatable = false)
    private Location locationO;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id",updatable = false, nullable = false)
    private int eventID;

    @Column(name = "location_id")
    private int locationId;

    public Event(){}

    Event(int d,int m, int y, String name, int locationId, String startTime, String endTime) {
        this.name = name;
        this.locationId = locationId;
        startDate = new Date(y-1900,m,d,Integer.valueOf(startTime.substring(0,2)),Integer.valueOf(startTime.substring(3)));
        endDate = new Date(y-1900,m,d,Integer.valueOf(endTime.substring(0,2)),Integer.valueOf(endTime.substring(3)));
    }

    Event (int id,String name, Date startDate, Date endDate, String location) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.eventID = id;
    }

    int getEventID() {
        return eventID;
    }

    String getName() {
        return name;
    }

    String getLocation() {
        return location;
    }

    int getLocationId() {
        return  locationId;
    }

    void setName(String name) {
        this.name = name;
    }

    void setLocation(String location) {
        this.location = location;
    }

    Date getStartDate() {
        return startDate;
    }

    void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    Date getEndDate() {
        return endDate;
    }

    void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    void setEventID(int eventID) {
        this.eventID = eventID;
    }

    void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    Location getLocationO() {
        return locationO;
    }

    void setLocationO(Location locationO) {
        this.locationO = locationO;
    }
}

