package com.proto.BIS.common.Controller;


import com.proto.BIS.common.DTO.ClientRequest;
import com.proto.BIS.common.DTO.ClientResponse;
import com.proto.BIS.common.DTO.ClientStatsResponse;
import com.proto.BIS.common.Service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Manage salon clients and client visit statistics")
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    private final ClientService clientService;

    @GetMapping("/getClients")
    @Operation(
            summary = "Get all clients",
            description = "Returns all clients without pagination. Visit statistics are included when available in the response mapping.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clients returned successfully"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content)
    })
    public ResponseEntity<List<ClientResponse>> getClients() {
        log.info("In get clients");
        return ResponseEntity.ok(clientService.getClientsList());
    }

    @GetMapping("/page")
    @Operation(
            summary = "Search clients with pagination",
            description = "Returns clients in pages, optionally searching by client name or phone number. Results are sorted by client name.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated clients returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid page or size parameter", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content)
    })
    public ResponseEntity<Page<ClientResponse>> getClientsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String q) {
        log.info("In get clients page: page={}, size={}, q={}", page, size, q);
        return ResponseEntity.ok(clientService.getClientPaged(q, PageRequest.of(page, size, Sort.by("clientName"))));
    }

    @GetMapping("/stats")
    @Operation(
            summary = "Get client statistics",
            description = "Returns total clients, clients created during the current month, and returning-client counts.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client statistics returned successfully", content = @Content(schema = @Schema(implementation = ClientStatsResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content)
    })
    public ResponseEntity<ClientStatsResponse> getStats() {
        log.info("In get client stats");
        return ResponseEntity.ok(clientService.getStats());
    }

    @PostMapping("/add")
    @Operation(
            summary = "Create a client",
            description = "Creates a client. The phone number must contain exactly 10 digits and must not already belong to another client.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client created", content = @Content(schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid client data or duplicate phone number", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content)
    })
    public ResponseEntity<ClientResponse> addClient(@RequestBody ClientRequest request) {
        log.info("In add client");
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.addClient(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a client",
            description = "Updates an existing client. The phone number must contain exactly 10 digits and remain unique.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client updated", content = @Content(schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "400", description = "Client not found, invalid client data, or duplicate phone number", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content)
    })
    public ResponseEntity<ClientResponse> updateClient(@PathVariable Long id, @RequestBody ClientRequest request) {
        log.info("In update client: {}", id);
        return ResponseEntity.ok(clientService.updateClient(id, request));
    }
}
