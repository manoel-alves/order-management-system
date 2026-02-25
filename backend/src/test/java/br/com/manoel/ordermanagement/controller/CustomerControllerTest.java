package br.com.manoel.ordermanagement.controller;

import br.com.manoel.ordermanagement.dto.request.CreateCustomerRequest;
import br.com.manoel.ordermanagement.exception.GlobalExceptionHandler;
import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import br.com.manoel.ordermanagement.exception.domain.ResourceNotFoundException;
import br.com.manoel.ordermanagement.model.Customer;
import br.com.manoel.ordermanagement.service.CustomerService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(GlobalExceptionHandler.class)
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    // POST /customers

    @Test
    void create_validCustomer_shouldReturn201() throws Exception {
        Customer customer = new Customer("Valid Name", "valid@example.com", Instant.now());
        customer.setId(1L);

        when(customerService.create("Valid Name", "valid@example.com")).thenReturn(customer);

        CreateCustomerRequest request = new CreateCustomerRequest("Valid Name", "valid@example.com");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/customers/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Valid Name"))
                .andExpect(jsonPath("$.email").value("valid@example.com"));
    }

    @Test
    void create_blankName_shouldReturn400() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest("", "valid@example.com");

        when(customerService.create("", "valid@example.com"))
                .thenThrow(new DomainValidationException("Nome vazio ou inexistente"));

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Nome vazio ou inexistente"));
    }

    @Test
    void create_blankEmail_shouldReturn400() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest("Valid Name", "");

        when(customerService.create("Valid Name", ""))
                .thenThrow(new DomainValidationException("Email vazio ou inexistente"));

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email vazio ou inexistente"));
    }

    @Test
    void create_invalidName_shouldReturn400() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest("Invalid  Name123", "valid@example.com");

        when(customerService.create("Invalid  Name123", "valid@example.com"))
                .thenThrow(new DomainValidationException("Nome deve conter apenas letras e espaços únicos"));

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Nome deve conter apenas letras e espaços únicos"));
    }

    @Test
    void create_invalidEmail_shouldReturn400() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest("Valid Name", "invalid-email");

        when(customerService.create("Valid Name", "invalid-email"))
                .thenThrow(new DomainValidationException("Email com formato inválido"));

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email com formato inválido"));
    }

    // GET /customers/{id}

    @Test
    void getById_existingCustomer_shouldReturn200() throws Exception {
        Customer customer = new Customer("Valid Name", "valid@example.com", Instant.now());
        customer.setId(1L);

        when(customerService.findById(1L)).thenReturn(customer);

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Valid Name"))
                .andExpect(jsonPath("$.email").value("valid@example.com"));
    }

    @Test
    void getById_nonExistingCustomer_shouldReturn404() throws Exception {
        when(customerService.findById(404L))
                .thenThrow(new ResourceNotFoundException("Customer not found"));

        mockMvc.perform(get("/customers/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found"));
    }

    // GET /customers?name=<name>

    @Test
    void getByName_existingCustomer_shouldReturn200AndList() throws Exception {
        Customer customer = new Customer("Valid Name", "valid@example.com", Instant.now());
        customer.setId(1L);

        when(customerService.findByName("Valid Name")).thenReturn(List.of(customer));

        mockMvc.perform(get("/customers").param("name", "Valid Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Valid Name"))
                .andExpect(jsonPath("$[0].email").value("valid@example.com"));
    }

    @Test
    void getByName_noCustomerFound_shouldReturn200AndEmptyList() throws Exception {
        when(customerService.findByName("Unknown")).thenReturn(List.of());

        mockMvc.perform(get("/customers").param("name", "Unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // GET /customers

    @Test
    void getAll_shouldReturn200AndAllCustomers() throws Exception {
        Customer c1 = new Customer("A", "a@example.com", Instant.now());
        c1.setId(1L);
        Customer c2 = new Customer("B", "b@example.com", Instant.now());
        c2.setId(2L);

        when(customerService.findAll()).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAll_noCustomersRegistered_shouldReturn200AndEmptyList() throws Exception {
        when(customerService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}