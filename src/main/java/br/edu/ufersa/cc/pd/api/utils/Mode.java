package br.edu.ufersa.cc.pd.api.utils;

import br.edu.ufersa.cc.pd.api.contracts.App;
import br.edu.ufersa.cc.pd.api.contracts.Launcher;
import br.edu.ufersa.cc.pd.api.launchers.DroneLauncher;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Mode {

    DRONE(new DroneLauncher());

    private final Launcher<? extends App> launcher;

}
