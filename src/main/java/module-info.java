module br.edu.ufersa.cc.pd {
    // requires transitive javafx.controls;
    requires transitive javafx.controls;
    requires javafx.fxml;

    requires static lombok;

    opens br.edu.ufersa.cc.pd.api.controllers to javafx.fxml;

    exports br.edu.ufersa.cc.pd;
}
