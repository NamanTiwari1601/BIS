package com.proto.BIS.common.Service;

import com.proto.BIS.common.DTO.ClientResponse;
import com.proto.BIS.common.DTO.ClientStatsResponse;
import com.proto.BIS.common.Model.ClientModel;
import com.proto.BIS.common.Repository.ClientRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.proto.BIS.common.DTO.ClientRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepo repo;

    public List<ClientResponse> getClientsList(){
    return repo.findAll().stream().map(this::maptoresponse).toList();
    }

    public Page<ClientResponse> getClientPaged(String q, Pageable pageable) {
        Page<ClientModel> page = (q == null || q.isBlank())
                ? repo.findAll(pageable)
                : repo.search(q.trim(), pageable);

        List<String> phones = page.getContent().stream()
                .map(ClientModel::getClientPhone)
                .filter(Objects::nonNull)
                .toList();

        Map<String, Object[]> stats = new HashMap<>();
        if (!phones.isEmpty()) {
            for (Object[] row : repo.visitStats(phones)) {
                stats.put((String) row[0], row);
            }
        }

        return page.map(model -> {
            ClientResponse response = maptoresponse(model);
            Object[] row = stats.get(model.getClientPhone());
            response.setTotalVisits(row != null ? (Long) row[1] : 0L);
            response.setLastVisit(row != null ? (LocalDateTime) row[2] : null);
            return response;
        });
    }
    public ClientStatsResponse getStats() {
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        return new ClientStatsResponse(
                repo.count(),
                repo.countByCreatedAtGreaterThanEqual(monthStart),
                repo.countReturning());
    }
    public ClientResponse addClient(ClientRequest request) {
        validate(request);
        String phone = request.getClientPhone().trim();
        if (repo.findByClientPhone(phone).isPresent()) {
            log.warn("Client already exists with phone {}", phone);
            throw new IllegalArgumentException("A client with this phone number already exists");
        }
        ClientModel model = new ClientModel();
        applyRequest(model, request);
        if (model.getStatus() == null || model.getStatus().isBlank()) {
            model.setStatus("Active");
        }
        log.info("Adding client {}", model.getClientName());
        return maptoresponse(repo.save(model));
    }

    public ClientResponse updateClient(Long id, ClientRequest request) {
        ClientModel model = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Client not found: {}", id);
                    return new IllegalArgumentException("Client not found: " + id);
                });
        validate(request);
        repo.findByClientPhone(request.getClientPhone().trim())
                .filter(other -> !other.getClientId().equals(id))
                .ifPresent(other -> {
                    throw new IllegalArgumentException("Another client already uses this phone number");
                });
        applyRequest(model, request);
        log.info("Updating client {}", id);
        return maptoresponse(repo.save(model));
    }

    private void applyRequest(ClientModel model, ClientRequest request) {
        model.setClientName(request.getClientName().trim());
        model.setClientPhone(request.getClientPhone().trim());
        model.setClientEmail(request.getClientEmail() == null || request.getClientEmail().isBlank()
                ? null : request.getClientEmail().trim());
        model.setNotes(request.getNotes());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            model.setStatus(request.getStatus());
        }
    }

    private void validate(ClientRequest request) {
        if (request == null || request.getClientName() == null || request.getClientName().isBlank()) {
            throw new IllegalArgumentException("Client name is required");
        }
        if (request.getClientPhone() == null || !request.getClientPhone().trim().matches("^\\d{10}$")) {
            throw new IllegalArgumentException("Phone number must be 10 digits");
        }
    }
    private ClientResponse maptoresponse(ClientModel model){
    ClientResponse response=new ClientResponse();
     response.setClientId(model.getClientId());
     response.setClientName(model.getClientName());
     response.setClientEmail(model.getClientEmail());
     response.setClientPhone(model.getClientPhone());
     response.setStatus(model.getStatus());
     response.setNotes(model.getNotes());

    return response;
    }
}
