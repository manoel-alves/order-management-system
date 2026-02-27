package br.com.manoel.ordermanagement.controller;

import br.com.manoel.ordermanagement.dto.request.CreateOrderRequest;
import br.com.manoel.ordermanagement.dto.response.OrderResponse;
import br.com.manoel.ordermanagement.exception.GlobalExceptionHandler;
import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import br.com.manoel.ordermanagement.exception.domain.ResourceNotFoundException;
import br.com.manoel.ordermanagement.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(GlobalExceptionHandler.class)
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService service;

    @Autowired
    private ObjectMapper objectMapper;

    // --- MOCK HELPERS ---
    private OrderResponse mockResponse() {
        return new OrderResponse(
                1L,
                10L,
                List.of(
                        new OrderResponse.OrderItemResponse(
                                100L,
                                BigDecimal.valueOf(100),
                                2,
                                BigDecimal.ZERO,
                                BigDecimal.valueOf(200)
                        )
                ),
                BigDecimal.valueOf(200),
                Instant.now()
        );
    }

    // TESTS

    // Create

    @Test
    @DisplayName("GET /orders/{id} - Should return 200")
    void getById_shouldReturn200() throws Exception {

        when(service.findResponseById(1L))
                .thenReturn(mockResponse());

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /orders/{id} - Should return 404")
    void getById_shouldReturn404() throws Exception {

        when(service.findResponseById(99L))
                .thenThrow(new ResourceNotFoundException("Pedido não encontrado"));

        mockMvc.perform(get("/orders/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Pedido não encontrado"));
    }

    // Get by ID

    @Test
    @DisplayName("POST /orders - Should return 201")
    void create_shouldReturn201() throws Exception {

        CreateOrderRequest request = new CreateOrderRequest(
                10L,
                List.of(
                        new CreateOrderRequest.OrderItemRequest(
                                100L,
                                2,
                                BigDecimal.ZERO
                        )
                )
        );

        when(service.create(request)).thenReturn(mockResponse());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/orders/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value(10))
                .andExpect(jsonPath("$.items[0].productId").value(100))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    @DisplayName("POST /orders - Should return 400 when service throws DomainValidationException")
    void create_shouldReturn400() throws Exception {

        CreateOrderRequest request = new CreateOrderRequest(10L, List.of());

        when(service.create(request))
                .thenThrow(new DomainValidationException("Pedido precisa ter pelo menos 1 item"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Pedido precisa ter pelo menos 1 item"));
    }

    // Get All

    @Test
    @DisplayName("GET /orders - Should return list")
    void getAll_shouldReturnList() throws Exception {

        when(service.findAllResponses())
                .thenReturn(List.of(mockResponse(), mockResponse()));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // Get by Customer

    @Test
    void getByCustomer_shouldReturnList() throws Exception {

        when(service.findByCustomerIdResponse(10L))
                .thenReturn(List.of(mockResponse()));

        mockMvc.perform(get("/orders")
                        .param("customerId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // Get by Product

    @Test
    void getByProduct_shouldReturnList() throws Exception {

        when(service.findByProductIdResponse(100L))
                .thenReturn(List.of(mockResponse()));

        mockMvc.perform(get("/orders")
                        .param("productId", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // Get by Period
    @Test
    void getByPeriod_shouldReturnList() throws Exception {

        Instant start = Instant.now();
        Instant end = start.plusSeconds(3600);

        when(service.findByPeriodResponse(start, end))
                .thenReturn(List.of(mockResponse()));

        mockMvc.perform(get("/orders/by-period")
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // Total

    @Test
    void getTotal_shouldReturnAmount() throws Exception {

        when(service.findTotalAmountByCustomer(10L))
                .thenReturn(BigDecimal.valueOf(500));

        mockMvc.perform(get("/orders/total")
                        .param("customerId", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("500"));
    }
}