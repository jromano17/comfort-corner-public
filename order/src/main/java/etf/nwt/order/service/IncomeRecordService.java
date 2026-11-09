package etf.nwt.order.service;

import etf.nwt.order.dto.incomeRecord.IncomeRecordDTO;
import etf.nwt.order.exception.ResourceNotFoundException;
import etf.nwt.order.models.IncomeRecord;
import etf.nwt.order.models.Order;
import etf.nwt.order.repository.IncomeRecordRepository;
import etf.nwt.order.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeRecordService {
    private final IncomeRecordRepository incomeRecordRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper mapper;

    public IncomeRecordService(IncomeRecordRepository incomeRecordRepository, OrderRepository orderRepository, ModelMapper mapper) {
        this.incomeRecordRepository = incomeRecordRepository;
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    public List<IncomeRecordDTO> getAllIncomeRecords() {
        return incomeRecordRepository.findAll()
                .stream()
                .map(incomeRecord -> mapper.map(incomeRecord, IncomeRecordDTO.class))
                .collect(Collectors.toList());
    }

    public IncomeRecordDTO getIncomeRecord(Long id) {
        IncomeRecord incomeRecord = incomeRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income record with ID " + id + " not found"));
        return mapper.map(incomeRecord, IncomeRecordDTO.class);
    }

    public IncomeRecordDTO recordIncome(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found"));

        double tax = 0.0;
        double cost = 0.0;
        double netIncome = order.getTotalPrice() - tax - cost;

        IncomeRecord record = new IncomeRecord();
        record.setOrderId(order.getId());
        record.setRecordedAt(LocalDateTime.now());
        record.setIncomeAmount(order.getTotalPrice());
        record.setTax(tax);
        record.setCost(cost);
        record.setNetIncome(netIncome);

        IncomeRecord saved = incomeRecordRepository.save(record);

        IncomeRecordDTO dto = mapper.map(record, IncomeRecordDTO.class);
        return dto;
    }


    public IncomeRecordDTO updateIncomeRecord(Long id, IncomeRecordDTO dto) {
        IncomeRecord incomeRecord = incomeRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income record with ID " + id + " not found"));

        mapper.map(dto, incomeRecord);

        IncomeRecord saved = incomeRecordRepository.save(incomeRecord);
        return mapper.map(saved, IncomeRecordDTO.class);
    }

    public void deleteIncomeRecord(Long id) {
        incomeRecordRepository.deleteById(id);
    }

    public Double getTotalIncome(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        Double retVal =  incomeRecordRepository.getTotalIncomeByDateRange(startDateTime, endDateTime);

        return retVal != null ? retVal : 0.0;
    }

}
