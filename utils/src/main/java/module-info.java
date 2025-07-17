module br.edu.ufersa.cc.pdutils {
    requires com.google.gson;
    requires com.rabbitmq.client;
    requires org.eclipse.paho.client.mqttv3;
    
    requires transitive com.fasterxml.jackson.annotation;
    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive com.fasterxml.jackson.datatype.jsr310;
    requires transitive slf4j.api;

    requires static lombok;

    exports br.edu.ufersa.cc.pd.contracts;
    exports br.edu.ufersa.cc.pd.dto;
    exports br.edu.ufersa.cc.pd.exceptions;
    exports br.edu.ufersa.cc.pd.mq;
    exports br.edu.ufersa.cc.pd.utils;
    exports br.edu.ufersa.cc.pd.utils.contracts;
    exports br.edu.ufersa.cc.pd.utils.dto;
}