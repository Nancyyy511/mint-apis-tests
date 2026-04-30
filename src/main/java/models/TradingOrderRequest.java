package models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TradingOrderRequest {
    private String accountId;
    private String type; // buy or sell
    private String execution; // market or limit
    private Integer quantity;
    private String validity;
    private Double limit;
    private String expiryDate;
}
