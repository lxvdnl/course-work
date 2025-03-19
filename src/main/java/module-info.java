module com.test.test {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jfree.jfreechart;


    opens com.test.test to javafx.fxml;
    exports com.test.test;
    exports com.test.test.calculations;
    opens com.test.test.calculations to javafx.fxml;
    exports com.test.test.calculations.impl;
    opens com.test.test.calculations.impl to javafx.fxml;
}