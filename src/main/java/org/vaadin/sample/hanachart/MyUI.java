package org.vaadin.sample.hanachart;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.text.DateFormatSymbols;
import java.util.List;
import java.util.Locale;

/**
 * Main UI. For sake of simplicity all logic is added in this single file
 *
 */
@SpringUI
public class MyUI extends UI {
    @Autowired
    private DataSource dataSource;

    private Integer chosenYear;

    private Backend backend;

    private Chart chart;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        // Initialize "backend"
        backend = new Backend(dataSource);

        // Create UI
        final VerticalLayout layout = new VerticalLayout();

        // Initialize chart
        chart = new Chart(ChartType.COLUMN);
        initChart();

        // Make combobox for choosing year
        List<Integer> years = backend.getAvailableYears();
        ComboBox<Integer> yearChooseComboBox = new ComboBox<>("Choose year");
        yearChooseComboBox.setItems(years);
        // Bind value-change event to re-draw chart
        yearChooseComboBox.addValueChangeListener(valueChangeEvent -> chooseYear(valueChangeEvent.getValue()));

        // choose first year
        chosenYear = years.get(0);
        yearChooseComboBox.setSelectedItem(chosenYear);

        layout.addComponent(yearChooseComboBox);
        layout.addComponent(chart);
        setContent(layout);
    }

    /**
     * Re-draws chart for given year
     * @param year
     */
    protected void chooseYear(Integer year) {
        if (year == null ) {
            return;
        }
        this.chosenYear = year;
        drawChart();
    }

    /**
     * Basic setting for chart (styling, titles)
     */
    protected void initChart() {
        Configuration conf = chart.getConfiguration();
        // margins
        conf.getChart().setMargin(50, 80, 100, 80);

        // add months on x -axis
        XAxis xAxis = new XAxis();
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);
        for (int i = 0; i < 12; i++) {
            xAxis.addCategory(dfs.getShortMonths()[i]);
        }
        conf.addxAxis(xAxis);

        // set title
        YAxis yAxis = new YAxis();
        yAxis.setMin(0);
        yAxis.setTitle(new AxisTitle("Sales (gross amount)"));
        conf.addyAxis(yAxis);
    }

    protected void drawChart() {
        Configuration conf = chart.getConfiguration();
        // Update title
        conf.setTitle(new Title("Gross sales in year " + this.chosenYear));
        // Update data
        conf.setSeries(backend.getGrossPerMonth(this.chosenYear));
        chart.drawChart(conf);
    }
}
