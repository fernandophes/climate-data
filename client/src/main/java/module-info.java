module br.edu.ufersa.cc.pdclient {
    requires javafx.fxml;
    requires slf4j.api;
    requires org.hibernate.orm.core;

    requires static lombok;

    requires transitive br.edu.ufersa.cc.pdutils;
    requires transitive jakarta.persistence;
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires transitive org.apache.logging.log4j;

    opens br.edu.ufersa.cc.pdclient.controllers to javafx.fxml;
    opens br.edu.ufersa.cc.pdclient.entities to org.hibernate.orm.core;

    exports br.edu.ufersa.cc.pdclient;
    exports br.edu.ufersa.cc.pdclient.dto;
    exports br.edu.ufersa.cc.pdclient.controllers;
    exports br.edu.ufersa.cc.pdclient.entities;
    exports br.edu.ufersa.cc.pdclient.repositories;
    exports br.edu.ufersa.cc.pdclient.services;
}
