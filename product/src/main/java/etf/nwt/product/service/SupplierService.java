package etf.nwt.product.service;

import etf.nwt.product.dto.SupplierDTO;
import etf.nwt.product.dto.ChairDTO;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.exception.InvalidOperationException;
import etf.nwt.product.model.Supplier;
import etf.nwt.product.model.Chair;
import etf.nwt.product.repository.SupplierRepository;
import etf.nwt.product.repository.ChairRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final ChairRepository chairRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        log.info("Creating supplier: {}", supplierDTO.getName());

        if (supplierRepository.existsByContactEmailIgnoreCase(supplierDTO.getContactEmail())) {
            throw new InvalidOperationException(
                    "Supplier with email '" + supplierDTO.getContactEmail() + "' already exists");
        }

        if (supplierRepository.existsByNameIgnoreCase(supplierDTO.getName())) {
            throw new InvalidOperationException(
                    "Supplier with username '" + supplierDTO.getContactEmail() + "' already exists");
        }

        Supplier supplier = modelMapper.map(supplierDTO, Supplier.class);

        Supplier saved = supplierRepository.save(supplier);
        log.info("Supplier created with ID: {}", saved.getId());

        return toDto(saved, false);
    }

    @Transactional(readOnly = true)
    public SupplierDTO getSupplierById(Long id) {
        log.debug("Fetching supplier by ID: {}", id);

        Supplier supplier = supplierRepository.findByIdWithChairs(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + id));

        return toDto(supplier, true);
    }

    @Transactional(readOnly = true)
    public SupplierDTO getSupplierByName(String name) {
        log.debug("Fetching supplier by name: {}", name);

        Supplier supplier = supplierRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with name: " + name));

        return toDto(supplier, true);
    }

    @Transactional(readOnly = true)
    public SupplierDTO getSupplierByEmail(String email) {
        log.debug("Fetching supplier by email: {}", email);

        Supplier supplier = supplierRepository.findByContactEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with email: " + email));

        return toDto(supplier, true);
    }

    @Transactional(readOnly = true)
    public List<SupplierDTO> getAllSuppliers() {
        log.debug("Fetching all suppliers");

        return supplierRepository.findAll()
                .stream()
                .map(supplier -> toDto(supplier, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupplierDTO> getAllSuppliersWithChairs() {
        log.debug("Fetching all suppliers with chairs");

        return supplierRepository.findAllWithChairs()
                .stream()
                .map(supplier -> toDto(supplier, true))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupplierDTO> getSuppliersByCountry(String country) {
        log.debug("Fetching suppliers by country: {}", country);

        return supplierRepository.findByCountryIgnoreCase(country)
                .stream()
                .map(supplier -> toDto(supplier, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupplierDTO> searchSuppliers(String keyword, boolean includeChairs ) {
        log.debug("Searching suppliers with keyword: {}", keyword);

        List<Supplier> suppliers;
        if (includeChairs) {
            suppliers = supplierRepository.searchWithChairs(keyword);
        } else {
            suppliers = supplierRepository.search(keyword);
        }

        return suppliers.stream()
                .map(supplier -> toDto(supplier, includeChairs))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getChairCount(Long supplierId) {
        log.debug("Getting chair count for supplier ID: {}", supplierId);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + supplierId);
        }

        return supplierRepository.countChairsBySupplierId(supplierId);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getSupplierStatsByCountry() {
        log.debug("Getting supplier statistics by country");

        return supplierRepository.getSupplierCountByCountry()
                .stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));
    }

    @Transactional
    public SupplierDTO updateSupplier(SupplierDTO supplierDTO) {
        log.info("Updating supplier ID: {}", supplierDTO.getId());

        Supplier existing = supplierRepository.findById(supplierDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + supplierDTO.getId()));

        if (!existing.getContactEmail().equalsIgnoreCase(supplierDTO.getContactEmail()) &&
                supplierRepository.existsByContactEmailIgnoreCaseAndIdNot(
                        supplierDTO.getContactEmail(), existing.getId())) {
            throw new InvalidOperationException(
                    "Supplier with email '" + supplierDTO.getContactEmail() + "' already exists");
        }

        existing.setName(supplierDTO.getName());
        existing.setContactEmail(supplierDTO.getContactEmail());
        existing.setCountry(supplierDTO.getCountry());

        if (supplierDTO.getChairIds() != null && !supplierDTO.getChairIds().isEmpty()) {
            updateSupplierChairs(existing, supplierDTO.getChairIds());
        }

        Supplier updated = supplierRepository.save(existing);
        log.info("Supplier updated successfully: {}", updated.getId());

        return toDto(updated, true);
    }

    @Transactional
    public SupplierDTO updateSupplierEmail(Long id, String email) {
        log.info("Updating supplier email for ID: {} to: {}", id, email);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + id));

        if (!supplier.getContactEmail().equalsIgnoreCase(email) &&
                supplierRepository.existsByContactEmailIgnoreCaseAndIdNot(email, id)) {
            throw new InvalidOperationException(
                    "Supplier with email '" + email + "' already exists");
        }

        supplier.setContactEmail(email);
        Supplier updated = supplierRepository.save(supplier);

        return toDto(updated, false);
    }

    @Transactional
    public SupplierDTO updateSupplierCountry(Long id, String country) {
        log.info("Updating supplier country for ID: {} to: {}", id, country);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + id));

        supplier.setCountry(country);
        Supplier updated = supplierRepository.save(supplier);

        return toDto(updated, false);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        log.info("Deleting supplier ID: {}", id);

        Supplier supplier = supplierRepository.findByIdWithChairs(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + id));

        if (!supplier.getChairs().isEmpty()) {
            throw new InvalidOperationException(
                    "Cannot delete supplier with chairs. " +
                            "Please remove all chairs first or use force delete.");
        }

        supplierRepository.delete(supplier);
        log.info("Supplier deleted successfully: {}", id);
    }

    @Transactional
    public void forceDeleteSupplier(Long id) {
        log.warn("Force deleting supplier ID: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + id));

        try {
            supplierRepository.delete(supplier);
            log.warn("Supplier force deleted: {}", id);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidOperationException(
                    "Cannot delete supplier due to foreign key constraints. " +
                            "Please remove or reassign related chairs first.");
        }
    }

    @Transactional
    public SupplierDTO addChairToSupplier(Long supplierId, Long chairId) {
        log.info("Adding chair {} to supplier {}", chairId, supplierId);

        Supplier supplier = supplierRepository.findByIdWithChairs(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + supplierId));

        Chair chair = chairRepository.findById(chairId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chair not found with id: " + chairId));

        if (chair.getSupplier() != null && !chair.getSupplier().getId().equals(supplierId)) {
            throw new InvalidOperationException(
                    "Chair is already assigned to supplier: " + chair.getSupplier().getName());
        }

        supplier.getChairs().add(chair);
        chair.setSupplier(supplier);

        Supplier updated = supplierRepository.save(supplier);

        return toDto(updated, true);
    }

    @Transactional
    public SupplierDTO removeChairFromSupplier(Long supplierId, Long chairId) {
        log.info("Removing chair {} from supplier {}", chairId, supplierId);

        Supplier supplier = supplierRepository.findByIdWithChairs(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + supplierId));

        Chair chair = chairRepository.findById(chairId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chair not found with id: " + chairId));

        if (chair.getSupplier() == null || !chair.getSupplier().getId().equals(supplierId)) {
            throw new InvalidOperationException(
                    "Chair is not assigned to this supplier");
        }

        supplier.getChairs().remove(chair);
        chair.setSupplier(null);

        Supplier updated = supplierRepository.save(supplier);

        return toDto(updated, true);
    }

    @Transactional(readOnly = true)
    public List<SupplierDTO> getTopSuppliers(int limit) {
        log.debug("Fetching top {} suppliers by chair count", limit);

        return supplierRepository.findTopSuppliersByChairCount(limit)
                .stream()
                .map(supplier -> toDto(supplier, true))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return supplierRepository.existsByContactEmailIgnoreCase(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return supplierRepository.existsByNameIgnoreCase(name);
    }

    private void updateSupplierChairs(Supplier supplier, List<Long> chairIds) {
        for (Chair chair : supplier.getChairs()) {
            chair.setSupplier(null);
        }
        supplier.getChairs().clear();

        for (Long chairId : chairIds) {
            Chair chair = chairRepository.findById(chairId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Chair not found with id: " + chairId));
            supplier.getChairs().add(chair);
            chair.setSupplier(supplier);
        }
    }

    private SupplierDTO toDto(Supplier supplier, boolean includeChairs) {
        SupplierDTO dto = modelMapper.map(supplier, SupplierDTO.class);

        if (includeChairs && supplier.getChairs() != null) {
            List<ChairDTO> chairDTOs = supplier.getChairs().stream()
                    .map(chair -> modelMapper.map(chair, ChairDTO.class))
                    .collect(Collectors.toList());
            dto.setChairs(chairDTOs);
        }

        return dto;
    }
}