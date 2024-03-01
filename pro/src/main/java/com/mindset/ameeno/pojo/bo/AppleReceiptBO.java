package com.mindset.ameeno.pojo.bo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author zhutianxiang
 
 * @Date 2024/1/11 14:47
 * @Version 1.0
 */
@Data
public class AppleReceiptBO {
    private Long transactionId;
    private Long originalTransactionId;
    private Long webOrderLineItemId;
    private String bundleId;
    private String productId;
    private String subscriptionGroupIdentifier;
    private Date purchaseDate;
    private Date originalPurchaseDate;
    private Date expiresDate;
    private Integer d;
    private Integer quantity;
    private String type;
    private String inAppOwnershipType;
    private Long signedDate;
    private String environment;
    private String transactionReason;
    private String storefront;
    private String storefrontId;
    private BigDecimal price;
    private String currency;

    public static AppleReceiptBO claims2ReceiptBO(Jws<Claims> c){
        Claims claimMap =  c.getBody();
        AppleReceiptBO bo = new AppleReceiptBO();
        bo.setTransactionId((Long) claimMap.get("transactionId"));
        bo.setOriginalTransactionId((Long) claimMap.get("originalTransactionId"));
        bo.setWebOrderLineItemId((Long) claimMap.get("webOrderLineItemId"));
        bo.setBundleId((String) claimMap.get("bundleId"));
        bo.setProductId((String) claimMap.get("productId"));
        bo.setSubscriptionGroupIdentifier((String) claimMap.get("subscriptionGroupIdentifier"));
        bo.setPurchaseDate(new Date((Long) claimMap.get("purchaseDate")));
        bo.setOriginalPurchaseDate(new Date((Long) claimMap.get("originalPurchaseDate")));
        bo.setExpiresDate(new Date((Long) claimMap.get("expeiresDate")));
        bo.setD((Integer) claimMap.get("d"));
        bo.setQuantity((Integer) claimMap.get("quantity"));
        bo.setType((String) claimMap.get("type"));
        bo.setInAppOwnershipType((String) claimMap.get("inAppOwnershipType"));
        bo.setSignedDate((Long) claimMap.get("signedDate"));
        bo.setEnvironment((String) claimMap.get("environment"));
        bo.setTransactionReason((String) claimMap.get("transactionReason"));
        bo.setStorefront((String) claimMap.get("storefront"));
        bo.setStorefrontId((String) claimMap.get("storefrontId"));
        // Assuming price is in cents and needs to be converted to a BigDecimal representation of currency
        bo.setPrice(new BigDecimal(((Integer) claimMap.get("price")) / 1000.0).setScale(2, BigDecimal.ROUND_HALF_UP));
        bo.setCurrency((String) claimMap.get("currency"));
        return bo;
    }
}
