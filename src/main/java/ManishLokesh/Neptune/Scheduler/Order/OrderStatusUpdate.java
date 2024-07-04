package ManishLokesh.Neptune.Scheduler.Order;import ManishLokesh.Neptune.PushToIRCTC.OrderStatus;import ManishLokesh.Neptune.v2.Orders.Entity.Orders;import ManishLokesh.Neptune.v2.Orders.Repository.OrderRepository;import com.fasterxml.jackson.databind.JsonNode;import com.fasterxml.jackson.databind.ObjectMapper;import org.hibernate.criterion.Order;import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.beans.factory.annotation.Value;import org.springframework.http.*;import org.springframework.scheduling.annotation.Scheduled;import org.springframework.stereotype.Component;import org.springframework.web.client.HttpClientErrorException;import org.springframework.web.client.RestTemplate;import org.slf4j.Logger;import java.time.LocalDateTime;import java.time.format.DateTimeFormatter;import java.util.*;@Componentpublic class OrderStatusUpdate {    private final ObjectMapper objectMapper;    @Autowired    public OrderStatus orderStatus;    @Autowired    public OrderRepository orderRepository;    private final Logger logger = LoggerFactory.getLogger("Auto-Status-Update.Scheduler");    @Autowired    public OrderStatusUpdate(ObjectMapper objectMapper) {        this.objectMapper = objectMapper;    }    @Scheduled(fixedDelay = 120000)    public void AutoStatusUpdate() {        logger.info("Auto Status Update Scheduler running......");        try {            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");            String localDateTime = LocalDateTime.now().format(formatter);            LocalDateTime currentDate = LocalDateTime.parse(localDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));            List<String> ordersList = orderRepository.OrderStatus();            logger.info("order list {}", ordersList.toString());            for (Object order : ordersList) {                String[] parts = order.toString().split(",");                String currentStatus = parts[1];                LocalDateTime deliveryDate = LocalDateTime.parse(parts[2], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));                if (deliveryDate.minusMinutes(30).isBefore(currentDate) &&                        deliveryDate.minusMinutes(25).isAfter(currentDate)                ) {                    Map<String, Object> status = new HashMap<>();                    status.put("status", "ORDER_PREPARING");                    Optional<Orders> orders = orderRepository.findById(Long.valueOf(parts[0]));                    Orders order1 = orders.get();                    String response = orderStatus.StatusPushToIrctc(status, order1.getIrctcOrderId());                    JsonNode jsonNode = objectMapper.readTree(response);                    JsonNode resultObject = jsonNode.get("result");                    JsonNode responseStatus = resultObject.get("status");                    if ("ORDER_PREPARING".equals(responseStatus.asText())) {                        order1.setStatus("PREPARING");                        orderRepository.save(order1);                    }                } else if (deliveryDate.minusMinutes(20).isBefore(currentDate)                        && deliveryDate.minusMinutes(15).isAfter(currentDate)                ) {                    Map<String, Object> status = new HashMap<>();                    status.put("status", "ORDER_PREPARED");                    Optional<Orders> orders = orderRepository.findById(Long.valueOf(parts[0]));                    Orders order1 = orders.get();                    String response = orderStatus.StatusPushToIrctc(status, order1.getIrctcOrderId());                    JsonNode jsonNode = objectMapper.readTree(response);                    JsonNode resultObject = jsonNode.get("result");                    JsonNode responseStatus = resultObject.get("status");                    if ("ORDER_PREPARED".equals(responseStatus.asText())) {                        order1.setStatus("PREPARED");                        orderRepository.save(order1);                    }                } else if (deliveryDate.minusMinutes(10).isBefore(currentDate)                        && deliveryDate.minusMinutes(5).isAfter(currentDate)) {                    Map<String, Object> status = new HashMap<>();                    status.put("status", "ORDER_OUT_FOR_DELIVERY");                    Optional<Orders> orders = orderRepository.findById(Long.valueOf(parts[0]));                    Orders order1 = orders.get();                    String response = orderStatus.StatusPushToIrctc(status, order1.getIrctcOrderId());                    JsonNode jsonNode = objectMapper.readTree(response);                    JsonNode resultObject = jsonNode.get("result");                    JsonNode responseStatus = resultObject.get("status");                    if ("ORDER_OUT_FOR_DELIVERY".equals(responseStatus.asText())) {                        order1.setStatus("OUT_OF_DELIVERY");                        orderRepository.save(order1);                    }                } else if (deliveryDate.plusHours(1).isBefore(currentDate)) {                    Map<String, Object> status = new HashMap<>();                    status.put("status", "ORDER_DELIVERED");                    Optional<Orders> orders = orderRepository.findById(Long.valueOf(parts[0]));                    Orders order1 = orders.get();                    String response = orderStatus.StatusPushToIrctc(status, order1.getIrctcOrderId());                    JsonNode jsonNode = objectMapper.readTree(response);                    JsonNode resultObject = jsonNode.get("result");                    JsonNode responseStatus = resultObject.get("status");                    if ("ORDER_DELIVERED".equals(responseStatus.asText())) {                        order1.setStatus("DELIVERED");                        orderRepository.save(order1);                    }                } else {                    logger.info("No order status update in this cycle");                }            }            logger.info("Cycle is completed....");        } catch (Exception e) {            String msg = e.getMessage();            logger.info("Exception msg {}", msg);        }    }//    public String StatusPushToIrctc(Object status, long irctcOrderId){//        logger.info("request for api/v1/order/"+irctcOrderId+"/status  requested JSON {}", status);//        HttpHeaders httpHeaders = new HttpHeaders();//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);//        httpHeaders.add("Authorization", AuthToken);//        try{//             ResponseEntity<String> response = this.restTemplate.exchange(//                    EcateUrl + "api/v1/order/"+irctcOrderId+"/status",//                    HttpMethod.POST,//                    new HttpEntity<>(status, httpHeaders),//                    String.class//            );//             logger.info("response JSON {}",response.getBody());//             return response.getBody();//        }catch (HttpClientErrorException e){//            logger.info("Exception msg {}",e.getResponseBodyAsString());//            return e.getResponseBodyAsString();//        }//    }}