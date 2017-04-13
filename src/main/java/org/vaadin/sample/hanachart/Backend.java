package org.vaadin.sample.hanachart;

import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Simplified mock-up of "backend"
 *
 * Just an object that gives the needed data with raw sql-queries
 */
public class Backend {
    private DataSource dataSource;

    private Statement stmt = null;

    private PreparedStatement grossStatement;

    public Backend(DataSource ds) {
        this.dataSource = ds;
        try {
            this.stmt = ds.getConnection().createStatement();
            this.grossStatement = ds.getConnection().prepareStatement("SELECT \n" +
                    "    YEAR(so.\"HISTORY.CREATEDAT\") AS \"year\", \n" +
                    "    MONTH(so.\"HISTORY.CREATEDAT\") AS \"month\", \n" +
                    "    SUM(so.\"GROSSAMOUNT\") AS \"total_gross\",\n" +
                    "    AVG(so.\"GROSSAMOUNT\") AS \"avarage_gross\",\n" +
                    "    COUNT(so.\"SALESORDERID\") AS \"sales_amount\"\n" +
                    "FROM \"sales_order\" AS so\n" +
                    "WHERE YEAR(so.\"HISTORY.CREATEDAT\") = ?\n" +
                    "GROUP BY YEAR(so.\"HISTORY.CREATEDAT\"), MONTH(so.\"HISTORY.CREATEDAT\")\n" +
                    "ORDER BY MONTH(so.\"HISTORY.CREATEDAT\")");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Get years available in source data
     *
     * @return list of years (Integers)
     */
    public List<Integer> getAvailableYears() {
        ArrayList<Integer> years = new ArrayList<>();
        String query = "SELECT \n" +
                "    DISTINCT YEAR(so.\"HISTORY.CREATEDAT\") AS \"year\"\n" +
                "FROM \"sales_order\" AS so\n" +
                "ORDER BY YEAR(so.\"HISTORY.CREATEDAT\") ASC";
        try {
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()) {
                years.add(rs.getInt("year"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return years;
    }

    /**
     * Fetch (monthly) grouped gross sales in DataSeries to simplify process
     *
     * @param year
     * @return Simple DataSeries, with "Month name" => "total gross value" items
     */
    public DataSeries getGrossPerMonth(Integer year) {
        DataSeries gross = new DataSeries("Gross amount per month");
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);

        try {
            grossStatement.setInt(1, year);
            ResultSet rs = grossStatement.executeQuery();

            while(rs.next()) {
                DataSeriesItem i = new DataSeriesItem(dfs.getMonths()[rs.getInt("month") - 1], rs.getBigDecimal("total_gross"));
                gross.add(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gross;
    }
}
