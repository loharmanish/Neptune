package ManishLokesh.Neptune.v1.GetOrderFromIRCTC.RequestBody;import java.util.List;public class IRCTCRequestBody {    private String orderId;    private CustomerDetails CustomerDetails;    private Boolean isAggregatorOrder;    private AggregatorDetails aggregatorDetails;    private String bookingDate;    private String deliveryDate;    private PriceDetails priceDetails;    private String status;    private DeliveryDetails deliveryDetails;    private List<OrderItemsGetFromIRCTC> orderItems;    private String paymentType;    private String comment;    @Override    public String toString() {        return "IRCTCRequestBody{" +                "orderId='" + orderId + '\'' +                ", CustomerDetails=" + CustomerDetails +                ", isAggregatorOrder=" + isAggregatorOrder +                ", aggregatorDetails=" + aggregatorDetails +                ", bookingDate='" + bookingDate + '\'' +                ", deliveryDate='" + deliveryDate + '\'' +                ", priceDetails=" + priceDetails +                ", status='" + status + '\'' +                ", deliveryDetails=" + deliveryDetails +                ", orderItems=" + orderItems +                ", paymentType='" + paymentType + '\'' +                ", comment='" + comment + '\'' +                '}';    }    public String getOrderId() {        return orderId;    }    public void setOrderId(String orderId) {        this.orderId = orderId;    }    public ManishLokesh.Neptune.v1.GetOrderFromIRCTC.RequestBody.CustomerDetails getCustomerDetails() {        return CustomerDetails;    }    public void setCustomerDetails(ManishLokesh.Neptune.v1.GetOrderFromIRCTC.RequestBody.CustomerDetails customerDetails) {        CustomerDetails = customerDetails;    }    public Boolean getAggregatorOrder() {        return isAggregatorOrder;    }    public void setAggregatorOrder(Boolean aggregatorOrder) {        isAggregatorOrder = aggregatorOrder;    }    public AggregatorDetails getAggregatorDetails() {        return aggregatorDetails;    }    public void setAggregatorDetails(AggregatorDetails aggregatorDetails) {        this.aggregatorDetails = aggregatorDetails;    }    public String getBookingDate() {        return bookingDate;    }    public void setBookingDate(String bookingDate) {        this.bookingDate = bookingDate;    }    public String getDeliveryDate() {        return deliveryDate;    }    public void setDeliveryDate(String deliveryDate) {        this.deliveryDate = deliveryDate;    }    public PriceDetails getPriceDetails() {        return priceDetails;    }    public void setPriceDetails(PriceDetails priceDetails) {        this.priceDetails = priceDetails;    }    public String getStatus() {        return status;    }    public void setStatus(String status) {        this.status = status;    }    public DeliveryDetails getDeliveryDetails() {        return deliveryDetails;    }    public void setDeliveryDetails(DeliveryDetails deliveryDetails) {        this.deliveryDetails = deliveryDetails;    }    public List<OrderItemsGetFromIRCTC> getOrderItems() {        return orderItems;    }    public void setOrderItems(List<OrderItemsGetFromIRCTC> orderItems) {        this.orderItems = orderItems;    }    public String getPaymentType() {        return paymentType;    }    public void setPaymentType(String paymentType) {        this.paymentType = paymentType;    }    public String getComment() {        return comment;    }    public void setComment(String comment) {        this.comment = comment;    }}