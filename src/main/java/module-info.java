module com.test.test {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jfree.jfreechart;


    opens com.test.test to javafx.fxml;
    exports com.test.test;
    exports com.test.test.surfacegraph.calculations;
    opens com.test.test.surfacegraph.calculations to javafx.fxml;
    exports com.test.test.surfacegraph.calculations.impl;
    opens com.test.test.surfacegraph.calculations.impl to javafx.fxml;
    exports com.test.test.surfacegraph;
    opens com.test.test.surfacegraph to javafx.fxml;
    exports com.test.test.config;
    opens com.test.test.config to javafx.fxml;
    exports com.test.test.bifurcationgraph;
    opens com.test.test.bifurcationgraph to javafx.fxml;
}