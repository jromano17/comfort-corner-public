package etf.nwt.order.controller;

import etf.nwt.order.dto.incomeRecord.IncomeRecordDTO;
import etf.nwt.order.exception.InvalidOperationException;
import etf.nwt.order.service.IncomeRecordService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/income")
public class IncomeRecordController {

    private final IncomeRecordService incomeService;

    public IncomeRecordController(IncomeRecordService incomeService) {
        this.incomeService = incomeService;
    }

    @PostMapping("/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public IncomeRecordDTO recordIncome(@PathVariable Long orderId,
                                        @Valid @RequestBody IncomeRecordDTO dto) {
        return incomeService.recordIncome(orderId);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<IncomeRecordDTO> getAll() {
        return incomeService.getAllIncomeRecords();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public IncomeRecordDTO getById(@PathVariable Long id) {
        return incomeService.getIncomeRecord(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncomeRecord(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public IncomeRecordDTO createIncome(@Valid  Long orderId) {
        return incomeService.recordIncome(orderId);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public IncomeRecordDTO updateIncome(@PathVariable Long id, @Valid @RequestBody IncomeRecordDTO dto) {
        return incomeService.updateIncomeRecord(id, dto);
    }
    @GetMapping("/total")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Double getTotalIncome(
            @RequestParam @PastOrPresent LocalDate startDate,
            @RequestParam @PastOrPresent LocalDate endDate) {

        validateDateRange(startDate, endDate);
        return incomeService.getTotalIncome(startDate, endDate);
    }
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidOperationException("Start date must be before or equal to end date");
        }
    }
}