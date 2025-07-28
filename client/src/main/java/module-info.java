module br.edu.ufersa.cc.pdclient {
    requires java.logging;
    requires java.net.http;
    requires javafx.fxml;
    requires javafx.controls;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    requires transitive br.edu.ufersa.cc.pdutils;
    requires transitive javafx.graphics;

    requires static lombok;

    opens br.edu.ufersa.cc.pdclient.controllers to javafx.fxml;
    opens br.edu.ufersa.cc.pdclient.entities to org.hibernate.orm.core;

    exports br.edu.ufersa.cc.pdclient;
    exports br.edu.ufersa.cc.pdclient.dto;
    exports br.edu.ufersa.cc.pdclient.controllers;
    exports br.edu.ufersa.cc.pdclient.entities;
    exports br.edu.ufersa.cc.pdclient.repositories;
    exports br.edu.ufersa.cc.pdclient.services;
}
