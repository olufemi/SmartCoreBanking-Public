/*
 To change this license header, choose License Headers in Project Properties.
 To change this template file, choose Tools | Templates
 and open the template in the editor.
 */
package com.cwg.centralized.wallet.utility.models;

import lombok.Data;
import lombok.NoArgsConstructor;

/**

 @author SmartCore Contributors
 */
@Data
@NoArgsConstructor
public class BvnResponseModel {

    private String ResponseCode;
    private boolean status;
    private String detail;
    private String verification_status;
    private String message;
    private String verification_reference;
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

    public BvnResponseModel(boolean simulate, String bvn, String email, String phoneNumber,
                             String firstName, String lstName, String phn2, String dateOfBirth,
                             String gender, String middleName, String baseImg, String imageUrl) {
        this.ResponseCode = "00";
        this.bvn = bvn;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lstName;
        this.dateOfBirth = dateOfBirth;
        this.registrationDate = "";
        this.enrollmentBank = "";
        this.enrollmentBranch = "";
        this.email = email;
        this.gender = gender;
        this.levelOfAccount = "3";
        this.lgaOfOrigin = "";
        this.lgaOfResidence = "";
        this.maritalStatus = "";
        this.nin = "";
        this.nameOnCard = "";
        this.nationality = "";
        this.phoneNumber1 = phoneNumber;
        this.phoneNumber2 = phn2;
        this.residentialAddress = "";
        this.stateOfOrigin = "";
        this.stateOfResidence = "";
        this.watchListed = "";
        this.base64Image = baseImg;
    }

}
