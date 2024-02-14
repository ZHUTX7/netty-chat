package com.mindset.ameeno.pojo.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/11/2 20:04
 * @Version 1.0
 */
@Data
public class ApplePurchaseBO {
    private String transactionId;
    private String originalTransactionId;
    private String webOrderLineItemId;
    private String bundleId;
    private String productId;
    private String subscriptionGroupIdentifier;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date purchaseDate;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date originalPurchaseDate;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date expiresDate;

    private int quantity;
    private String type;
    private String inAppOwnershipType;
    private long signedDate;
    private String environment;
    private String transactionReason;
    private String storefront;
    private String storefrontId;
    private int price;
    private String currency;

}
