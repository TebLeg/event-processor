package za.co.application;

import javax.persistence.*;

/**
 * The JPA entity class representing the table.
 * Created by A100286 on 3/13/2018.
 */
@Entity
@Table(name="Counter")
public class Counter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="runid")
    private String runInstance;
    private int count;

    public Counter() {
    }

    public Counter(String runInstance, int count) {
        this.runInstance = runInstance;
        this.count = count;
    }

    public String getRunInstance() {
        return runInstance;
    }

    public void setRunInstance(String runInstance) {
        this.runInstance = runInstance;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getId() {
        return id;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Counter{");
        sb.append("id=").append(id);
        sb.append(", runInstance='").append(runInstance).append('\'');
        sb.append(", count=").append(count);
        sb.append('}');
        return sb.toString();
    }
}
