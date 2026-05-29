/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.generalledger.proxies;

import com.smart.core.centralized.wallet.generalledger.models.AddNewUserToLimit;
import com.smart.core.centralized.wallet.generalledger.models.ApiResponseModel;
import com.smart.core.centralized.wallet.generalledger.models.BaseResponse;
import com.smart.core.centralized.wallet.generalledger.models.UpgradeUserToLimit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author SmartCore Contributors
 */
@FeignClient(name = "utility-service")
public interface UtilitiesProxy {

    @RequestMapping(value = "/utilities/walletmgt/add-tier-to-wallet", consumes = "application/json", method = RequestMethod.POST)
    public BaseResponse addNewUserToLimit(@RequestBody AddNewUserToLimit rq);

    @RequestMapping(value = "/utilities/walletmgt/update-wallet-tier", consumes = "application/json", method = RequestMethod.POST)
    public BaseResponse upgradeUserLimit(@RequestBody UpgradeUserToLimit rq);

    @RequestMapping(value = "/utilities/get-limits", consumes = "application/json", method = RequestMethod.GET)
    public ApiResponseModel getLimitLists();

}
