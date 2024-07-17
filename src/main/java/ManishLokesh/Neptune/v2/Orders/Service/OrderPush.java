package ManishLokesh.Neptune.v2.Orders.Service;import ManishLokesh.Neptune.v1.OutletsAndMenu.Entity.Outlet;import ManishLokesh.Neptune.v1.OutletsAndMenu.Repository.OutletRepo;import ManishLokesh.Neptune.v2.Orders.Entity.OrderItems;import ManishLokesh.Neptune.v2.Orders.Entity.Orders;import ManishLokesh.Neptune.v2.Orders.Repository.OrderItemsRepository;import ManishLokesh.Neptune.v2.Orders.Repository.OrderRepository;import ManishLokesh.Neptune.v2.Orders.RequestBody.OrderPushToIRCTC.CustomerInfo;import ManishLokesh.Neptune.v2.Orders.RequestBody.OrderPushToIRCTC.OrderItemsInfo;import ManishLokesh.Neptune.v2.Orders.RequestBody.OrderPushToIRCTC.OrderPushToIRCTC;import ManishLokesh.Neptune.v2.Orders.RequestBody.OrderPushToIRCTC.OutletInfo;import ManishLokesh.Neptune.v2.customer.Entity.Customer;import ManishLokesh.Neptune.v2.customer.Repository.CustLoginRepo;import com.fasterxml.jackson.core.JsonProcessingException;import com.fasterxml.jackson.databind.JsonNode;import com.fasterxml.jackson.databind.ObjectMapper;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.beans.factory.annotation.Value;import org.springframework.http.*;import org.springframework.stereotype.Service;import org.springframework.web.client.HttpClientErrorException;import org.springframework.web.client.RestTemplate;import java.util.ArrayList;import java.util.List;import java.util.Objects;import java.util.Optional;@Servicepublic class OrderPush {    public Logger logger = LoggerFactory.getLogger("app.v2.order.push.service");    private RestTemplate restTemplate;    private final String EcateUrl;    private final String AuthToken;    private final ObjectMapper objectMapper;    @Autowired    public CustLoginRepo custLoginRepo;    @Autowired    public OutletRepo outletRepo;    @Autowired    public OrderRepository orderRepository;    @Autowired    public OrderItemsRepository orderItemsRepository;    @Autowired    public OrderPush(RestTemplate restTemplate, @Value("${E-catering.stage.url}") String ecateUrl,                     @Value("${E-catering.auth.token}") String authToken, ObjectMapper objectMapper) {        this.restTemplate = restTemplate;        this.EcateUrl = ecateUrl;        this.AuthToken = authToken;        this.objectMapper = objectMapper;    }    public void OrderPushToIrctc(Orders order) {        logger.info("order push is calling...............");        Long customerId = Long.valueOf(order.getCustomerId());        Optional<Customer> customerDa = custLoginRepo.findById(customerId);        CustomerInfo customerInfo = new CustomerInfo();        customerInfo.setFullName(customerDa.get().getFullName());        customerInfo.setEmail(customerDa.get().getEmailId());        customerInfo.setMobile(customerDa.get().getMobileNumber());        Long outletId = Long.valueOf(order.getOutletId());        Optional<Outlet> outletData = outletRepo.findById(outletId);        OutletInfo outletInfo = new OutletInfo();        outletInfo.setId(outletData.get().getId());        outletInfo.setName(outletData.get().getOutletName());        outletInfo.setAddress(outletData.get().getAddress());        outletInfo.setCity(outletData.get().getCity());        outletInfo.setState(outletData.get().getState());        outletInfo.setPinCode("110001");        outletInfo.setContactNumbers(outletData.get().getMobileNo());        outletInfo.setRelationshipManagerName(outletData.get().getCompanyName());        outletInfo.setRelationshipManagerEmail(outletData.get().getEmailId());        outletInfo.setRelationshipManagerPhone(outletData.get().getMobileNo());        outletInfo.setFssaiNumber(outletData.get().getFssaiNo());        outletInfo.setFssaiCutOffDate(outletData.get().getFssaiValidUpto() + " 00:00 IST");        outletInfo.setGstNumber(outletData.get().getGstNo());        String orderId = String.valueOf(order.getId());        List<OrderItems> orderItemsData = orderItemsRepository.findByOrderId(orderId);        List<OrderItemsInfo> orderItemInfoList = new ArrayList<>();        for (OrderItems orderItems1 : orderItemsData) {            OrderItemsInfo orderItemsInfo = new OrderItemsInfo();            orderItemsInfo.setItemId(orderItems1.getItemId());            orderItemsInfo.setItemName(orderItems1.getItemName());            orderItemsInfo.setDescription(orderItems1.getDescription());            orderItemsInfo.setBasePrice(orderItems1.getBasePrice());            orderItemsInfo.setTaxRate(orderItems1.getTax());            orderItemsInfo.setSellingPrice(orderItems1.getSellingPrice());            orderItemsInfo.setVegetarian(orderItems1.getVeg());            orderItemsInfo.setQuantity(orderItems1.getQuantity());            orderItemsInfo.setOption("");            orderItemInfoList.add(orderItemsInfo);        }        String paymentType = "PRE_PAID";        if (Objects.equals(order.getPaymentType(), "CASH")) {            paymentType = "CASH_ON_DELIVERY";        }        OrderPushToIRCTC orderPush = new OrderPushToIRCTC(order.getId(), "", "", customerInfo,                outletInfo, order.getBookingDate() + " IST", order.getDeliveryDate() + " IST",                order.getPnr(), order.getTrainName(), order.getTrainNo(),                order.getStationCode(), order.getStationName(), order.getCoach(),                order.getBerth(), order.getTotalAmount(), order.getDeliveryCharge(),                order.getGst(), 0.0, order.getPayable_amount(), paymentType, orderItemInfoList);        logger.info("IRCTC order request body {}",orderPush.toString());        HttpHeaders httpHeaders = new HttpHeaders();        httpHeaders.setContentType(MediaType.APPLICATION_JSON);        httpHeaders.add("Authorization", AuthToken);        try {            ResponseEntity<String> response = this.restTemplate.exchange(                    EcateUrl + "api/v1/order/vendor",                    HttpMethod.POST,                    new HttpEntity<>(orderPush, httpHeaders),                    String.class            );            String responseBody = response.getBody();            try {                if (response.getStatusCode().is2xxSuccessful()) {                    JsonNode jsonNode = objectMapper.readTree(responseBody);                    JsonNode resultObject = jsonNode.get("result");                    JsonNode responseOrderId = resultObject.get("id");                    JsonNode orderStatus = resultObject.get("status");                    Optional<Orders> orders1 = orderRepository.findById(order.getId());                    Long irctcOrderId = Long.parseLong(String.valueOf(responseOrderId));                    Orders orders2 = orders1.get();                    orders2.setIrctcOrderId(irctcOrderId);                    String irctcOrderStatus = String.valueOf(orderStatus);                    String result = irctcOrderStatus.substring(1, irctcOrderStatus.length() - 1);                    if (result.equals("ORDER_CONFIRMED")) {                        orders2.setStatus("CONFIRMED");                    } else {                        orders2.setStatus(result);                    }                    Orders orders3 = orderRepository.save(orders2);                }            } catch (JsonProcessingException e) {                logger.info("error valid {}", e.getOriginalMessage());            }            logger.info("IRCTC response body {}", response.getBody());        } catch (HttpClientErrorException e) {            String errorResponse = e.getResponseBodyAsString();            try{                JsonNode jsonNode = objectMapper.readTree(errorResponse);                JsonNode jsonNode1 = jsonNode.get("status");                if("failure".equals(jsonNode1.asText())){                    Optional<Orders> ordersDetails = orderRepository.findById(order.getId());                    Orders orders = ordersDetails.get();                    orders.setStatus("CANCELLED");                    orderRepository.save(orders);                }                logger.info("Exception error : {}", e.getResponseBodyAsString());            }catch (JsonProcessingException t){                t.getOriginalMessage();            }        }        logger.info("order push is completed");    }}