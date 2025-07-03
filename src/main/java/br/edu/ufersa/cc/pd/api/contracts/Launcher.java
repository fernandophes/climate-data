package br.edu.ufersa.cc.pd.api.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class Launcher<A extends App> {

    private String name;

    public abstract A launch();

}
