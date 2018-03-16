package za.co.application;


import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps the result set into the Entity model class.
 * Created by A100286 on 3/13/2018.
 */
public class CounterMapper implements RowMapper<Counter> {
    public Counter mapRow(ResultSet rs, int rowNum) throws SQLException {
        Counter counter = new Counter();
        counter.setCount(rs.getInt("count"));
        counter.setRunInstance(rs.getString("runid"));

        return counter;
    }
}
