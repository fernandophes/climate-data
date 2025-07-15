package br.edu.ufersa.cc.pd.utils.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DroneMessage implements Serializable {

    private String droneName;
    private DataFormat dataFormat;
    private String message;

}
