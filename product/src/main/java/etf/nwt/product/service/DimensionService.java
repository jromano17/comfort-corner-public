package etf.nwt.product.service;

import etf.nwt.product.dto.DimensionDTO;
import etf.nwt.product.exception.InvalidOperationException;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.model.Dimension;
import etf.nwt.product.repository.DimensionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DimensionService {

    private final DimensionRepository dimensionRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public DimensionDTO createDimension(DimensionDTO dimensionDTO) {
        if (dimensionRepository.existsByName(dimensionDTO.getName())) {
            throw new InvalidOperationException(
                    "Dimension with name '" + dimensionDTO.getName() + "' already exists");
        }

        Dimension dimension = modelMapper.map(dimensionDTO, Dimension.class);
        Dimension saved = dimensionRepository.save(dimension);

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public DimensionDTO getDimensionById(Long id) {
        Dimension dimension = dimensionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dimension not found with id: " + id));

        return toDto(dimension);
    }

    @Transactional(readOnly = true)
    public DimensionDTO getDimensionByName(String name) {
        Dimension dimension = dimensionRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dimension not found with name: " + name));

        return toDto(dimension);
    }

    @Transactional(readOnly = true)
    public List<DimensionDTO> getAllDimensions() {
        return dimensionRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DimensionDTO> searchDimensions(String keyword) {
        return dimensionRepository.search(keyword)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DimensionDTO> getDimensionsBySizeRange(
            Double minWidth, Double maxWidth,
            Double minHeight, Double maxHeight,
            Double minDepth, Double maxDepth) {

        return dimensionRepository.findBySizeRange(
                        minWidth, maxWidth,
                        minHeight, maxHeight,
                        minDepth, maxDepth)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DimensionDTO> getDimensionsByMinWeightCapacity(Double minWeightCapacity) {
        return dimensionRepository.findByMinWeightCapacity(minWeightCapacity)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Object getSizeStatistics() {
        return dimensionRepository.getSizeStatistics();
    }

    @Transactional
    public DimensionDTO updateDimension(Long id, DimensionDTO dimensionDTO) {
        Dimension existing = dimensionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dimension not found with id: " + id));

        if (!existing.getName().equals(dimensionDTO.getName()) &&
                dimensionRepository.existsByNameAndIdNot(dimensionDTO.getName(), id)) {
            throw new InvalidOperationException(
                    "Dimension with name '" + dimensionDTO.getName() + "' already exists");
        }

        existing.setName(dimensionDTO.getName());
        existing.setWidth(dimensionDTO.getWidth());
        existing.setHeight(dimensionDTO.getHeight());
        existing.setDepth(dimensionDTO.getDepth());
        existing.setWeightCapacity(dimensionDTO.getWeightCapacity());

        Dimension updated = dimensionRepository.save(existing);

        return toDto(updated);
    }


    @Transactional
    public void deleteDimension(Long id) {
        if (!dimensionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Dimension not found with id: " + id);
        }
        dimensionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return dimensionRepository.existsById(id);
    }


    private DimensionDTO toDto(Dimension dimension) {
        return modelMapper.map(dimension, DimensionDTO.class);
    }
}