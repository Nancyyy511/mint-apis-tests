package utils;

public class TestDataStore {
    private static String ticker;
    private static String isinCode;
    private static Integer stockId;
    private static String orderId;

    public static String getTicker() {
        return ticker;
    }

    public static void setTicker(String ticker) {
        TestDataStore.ticker = ticker;
    }

    public static String getIsinCode() {
        return isinCode;
    }

    public static void setIsinCode(String isinCode) {
        TestDataStore.isinCode = isinCode;
    }

    public static Integer getStockId() {
        return stockId;
    }

    public static void setStockId(Integer stockId) {
        TestDataStore.stockId = stockId;
    }

    public static String getOrderId() {
        return orderId;
    }

    public static void setOrderId(String orderId) {
        TestDataStore.orderId = orderId;
    }
}

