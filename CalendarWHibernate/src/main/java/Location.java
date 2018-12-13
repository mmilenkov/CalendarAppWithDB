import javax.persistence.*;

@Entity
@Table(name = "location_list",schema = "event_list")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id",updatable = false, nullable = false)
    private int locationId;

    private String location;

    public Location () {}

    String getLocation() {
        return location;
    }

    void setLocation(String location) {
        this.location = location;
    }

    int getLocationId() {
        return locationId;
    }

    void setLocationId(int locationId) {
        this.locationId = locationId;
    }
}