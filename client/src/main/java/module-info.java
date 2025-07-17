module br.edu.ufersa.cc.pd {
    requires javafx.fxml;
    requires slf4j.api;
    requires org.hibernate.orm.core;

    requires static lombok;

    requires transitive br.edu.ufersa.cc.pd.utils;
    requires transitive jakarta.persistence;
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires transitive org.apache.logging.log4j;

    opens br.edu.ufersa.cc.pd.controllers to javafx.fxml;
    opens br.edu.ufersa.cc.pd.entities to org.hibernate.orm.core;

    exports br.edu.ufersa.cc.pd.entities;
    exports br.edu.ufersa.cc.pd.repositories;
    exports br.edu.ufersa.cc.pd.services;
    exports br.edu.ufersa.cc.pd.controllers;
    exports br.edu.ufersa.cc.pd;
}
