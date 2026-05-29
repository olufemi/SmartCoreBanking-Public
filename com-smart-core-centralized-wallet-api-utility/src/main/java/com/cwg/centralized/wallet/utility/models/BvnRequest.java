package com.cwg.centralized.wallet.utility.models;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BvnRequest implements Serializable {

    @NotBlank
    private String dob;

    @NotBlank
    private String number;

}
