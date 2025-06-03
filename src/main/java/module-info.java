module br.edu.ufersa.cc.pd {
    // requires transitive javafx.controls;
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens br.edu.ufersa.cc.pd to javafx.fxml;
    exports br.edu.ufersa.cc.pd;
}
