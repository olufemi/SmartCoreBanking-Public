package com.cwg.centralized.wallet.utility.models;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BvnIdentity implements Serializable {

    private boolean status;
    private String detail;
    private FaceData face_data;
    private BvnData bvn_data;
    private Verification verification;

    @Data
    @NoArgsConstructor
    public class FaceData {

        private boolean status;
        private String message;
    }

    @Data
    @NoArgsConstructor
    public class Verification {

        private String status;
        private String reference;
    }

    @Data
    @NoArgsConstructor
    public class BvnData {

        private String firstName;
        private String middleName;
        private String lastName;
        private String dateOfBirth;
        private String phoneNumber1;
        private String phoneNumber2;
        private String registrationDate;
        private String enrollmentBank;
        private String enrollmentBranch;
        private String email;
        private String gender;
        private String levelOfAccount;
        private String lgaOfOrigin;
        private String lgaOfResidence;
        private String maritalStatus;
        private String nin;
        private String nameOnCard;
        private String nationality;
        private String residentialAddress;
        private String stateOfOrigin;
        private String stateOfResidence;
        private String title;
        private String watchListed;
        private String bvn;
        private String base64Image;
    }

}
