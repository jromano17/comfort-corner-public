package etf.nwt.product.service;


import etf.nwt.product.dto.*;
import etf.nwt.product.exception.InvalidOperationException;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.model.*;
import etf.nwt.product.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChairVariantService {

    private final ChairVariantRepository chairVariantRepository;
    private final MaterialRepository materialRepository;
    private final DimensionRepository dimensionRepository;
    private final ColorOptionRepository colorOptionRepository;
    private final ChairRepository chairRepository;
    private final FileStorageService fileStorageService;
    private final ModelMapper modelMapper;

    private ChairVariantDTO mapToDTO(ChairVariant variant) {
        ChairVariantDTO dto = new ChairVariantDTO();
        dto.setImageUrl(variant.getImageUrl());
        dto.setId(variant.getId());
        dto.setFinalPrice(variant.getFinalPrice());
        dto.setStockQuantity(variant.getStockQuantity());

        if (variant.getChair() != null) {
            dto.setChairId(variant.getChair().getId());
        }

        if (variant.getMaterial() != null) {
            MaterialDTO materialDTO = new MaterialDTO();
            materialDTO.setId(variant.getMaterial().getId());
            materialDTO.setName(variant.getMaterial().getName());
            materialDTO.setDescription(variant.getMaterial().getDescription());
            dto.setMaterial(materialDTO);
        }

        if (variant.getColorOption() != null) {
            ColorOptionDTO colorDTO = new ColorOptionDTO();
            colorDTO.setId(variant.getColorOption().getId());
            colorDTO.setName(variant.getColorOption().getName());
            colorDTO.setHexCode(variant.getColorOption().getHexCode());
            dto.setColorOption(colorDTO);
        }

        if (variant.getDimension() != null) {
            DimensionDTO dimensionDTO = new DimensionDTO();
            dimensionDTO.setId(variant.getDimension().getId());
            dimensionDTO.setName(variant.getDimension().getName());
            dimensionDTO.setHeight(variant.getDimension().getHeight());
            dimensionDTO.setWidth(variant.getDimension().getWidth());
            dimensionDTO.setDepth(variant.getDimension().getDepth());
            dimensionDTO.setWeightCapacity(variant.getDimension().getWeightCapacity());
            dto.setDimension(dimensionDTO);
        }

        return dto;
    }

    @Transactional
    public ChairVariantDTO createChairVariant(CreateChairVariantDTO chairVariantDTO) {
    /*    Chair chair = chairRepository.findById(chairVariantDTO.getChairId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chair not found with id: " + chairVariantDTO.getChairId()));

        ChairVariant chairVariant = modelMapper.map(chairVariantDTO, ChairVariant.class);
        boolean exists = chairVariantRepository.existsByChairAndMaterialAndColorOptionAndDimension(
                chair,
                chairVariant.getMaterial(),
                chairVariant.getColorOption(),
                chairVariant.getDimension()
        );

        if (exists) {
            throw new InvalidOperationException(
                    "Chair variant with this configuration already exists");
        }

        if (chairVariantDTO.getFinalPrice().compareTo(chair.getBasePrice()) < 0) {
            throw new InvalidOperationException("Final price cannot be less than base price: " + chair.getBasePrice());
        }

        chairVariant.setChair(chair);
        ChairVariant saved = chairVariantRepository.save(chairVariant);

        return toDto(saved);

*/
        ChairVariant variant = new ChairVariant();
        variant.setFinalPrice(chairVariantDTO.getFinalPrice());
        variant.setStockQuantity(chairVariantDTO.getStockQuantity());

        Chair chair = chairRepository.findById(chairVariantDTO.getChairId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chair not found with id: " + chairVariantDTO.getChairId()));

        if (chairVariantDTO.getFinalPrice().compareTo(chair.getBasePrice()) < 0) {
            throw new InvalidOperationException("Final price cannot be less than base price: " + chair.getBasePrice());
        }

        variant.setChair(chair);

        Material material = materialRepository.findById(chairVariantDTO.getMaterialId())
                .orElseThrow(() -> new RuntimeException("Material not found! Please create it first."));
        variant.setMaterial(material);

        ColorOption color = colorOptionRepository.findById(chairVariantDTO.getColorOptionId())
                .orElseThrow(() -> new RuntimeException("Color Option not found! Please create it first."));
        variant.setColorOption(color);

        Dimension dimension = dimensionRepository.findById(chairVariantDTO.getDimensionId())
                .orElseThrow(() -> new RuntimeException("Dimension not found! Please create it first."));
        variant.setDimension(dimension);

        ChairVariant savedVariant = chairVariantRepository.save(variant);

        return mapToDTO(savedVariant);

    }

    @Transactional(readOnly = true)
    public ChairVariantDTO getChairVariantById(Long id) {
        ChairVariant chairVariant = chairVariantRepository.findByIdWithChair(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chair variant not found with id: " + id));

        return mapToDTO(chairVariant);
    }

    @Transactional(readOnly = true)
    public List<ChairVariantDTO> getAllChairVariants() {
        return chairVariantRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChairVariantDTO> getVariantsByChairId(Long chairId) {
        return chairVariantRepository.findByChairId(chairId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChairVariantDTO> getInStockVariants() {
        return chairVariantRepository.findByStockQuantityGreaterThan(0)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChairVariantDTO updateChairVariant(ChairVariantDTO chairVariantDTO) {
        ChairVariant existing = chairVariantRepository.findById(chairVariantDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chair variant not found with id: " + chairVariantDTO.getId()));

        if (!existing.getChair().getId().equals(chairVariantDTO.getChairId())) {
            Chair newChair = chairRepository.findById(chairVariantDTO.getChairId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Chair not found with id: " + chairVariantDTO.getChairId()));
            existing.setChair(newChair);
        }

        ChairVariant chairVariant = modelMapper.map(chairVariantDTO, ChairVariant.class);
        boolean duplicateExists = chairVariantRepository.existsByChairAndMaterialAndColorOptionAndDimensionAndIdNot(
                existing.getChair(),
                chairVariant.getMaterial(),
                chairVariant.getColorOption(),
                chairVariant.getDimension(),
                existing.getId()
        );

        if (duplicateExists) {
            throw new InvalidOperationException(
                    "Another variant with this configuration already exists");
        }

        existing.setMaterial(chairVariant.getMaterial());
        existing.setColorOption(chairVariant.getColorOption());
        existing.setDimension(chairVariant.getDimension());
        existing.setFinalPrice(chairVariantDTO.getFinalPrice());
        existing.setStockQuantity(chairVariantDTO.getStockQuantity());


        if (existing.getFinalPrice().compareTo(existing.getChair().getBasePrice()) < 0) {
            throw new InvalidOperationException(
                    "Final price cannot be less than base price: " +
                            existing.getChair().getBasePrice());
        }

        ChairVariant updated = chairVariantRepository.save(existing);
        return mapToDTO(updated);
    }

    @Transactional
    public ChairVariantDTO updateStockQuantity(Long id, Integer quantity) {
        if (quantity < 0) {
            throw new InvalidOperationException("Stock quantity cannot be negative");
        }

        ChairVariant chairVariant = chairVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chair variant not found with id: " + id));

        chairVariant.setStockQuantity(quantity);
        ChairVariant updated = chairVariantRepository.save(chairVariant);

        return mapToDTO(updated);
    }

    @Transactional
    public void deleteChairVariant(Long variantId) {
        ChairVariant variant = chairVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chair variant not found with id: " + variantId));

        fileStorageService.deleteFileByUrl(variant.getImageUrl());
        chairVariantRepository.deleteById(variantId);
    }

    @Transactional
    public void deleteVariantsByChairId(Long chairId) {
        if (!chairRepository.existsById(chairId)) {
            throw new ResourceNotFoundException("Chair not found with id: " + chairId);
        }
//trebalo bi dodat ko u gornjoj metodi da se obrisu i fizicki slike
        chairVariantRepository.deleteByChairId(chairId);
    }

    @Transactional(readOnly = true)
    public boolean checkStockAvailability(Long variantId, Integer requestedQuantity) {
        return chairVariantRepository.findById(variantId)
                .map(variant -> variant.getStockQuantity() >= requestedQuantity)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chair variant not found with id: " + variantId));
    }

    @Transactional
    public void reduceStock(Long variantId, Integer quantity) {
        ChairVariant variant = chairVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chair variant not found with id: " + variantId));

        if (variant.getStockQuantity() < quantity) {
            throw new InvalidOperationException(
                    "Insufficient stock. Available: " + variant.getStockQuantity() +
                            ", Requested: " + quantity);
        }

        variant.setStockQuantity(variant.getStockQuantity() - quantity);
        chairVariantRepository.save(variant);
    }

    @Transactional
    public void increaseStock(Long variantId, Integer quantity) {
        if (quantity <= 0) {
            throw new InvalidOperationException("Quantity must be positive");
        }

        ChairVariant variant = chairVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chair variant not found with id: " + variantId));

        variant.setStockQuantity(variant.getStockQuantity() + quantity);
        chairVariantRepository.save(variant);
    }

    private ChairVariantDTO toDto(ChairVariant chairVariant) {
        ChairVariantDTO dto = modelMapper.map(chairVariant, ChairVariantDTO.class);
        dto.setChairId(chairVariant.getChair().getId());
        return dto;
    }
    @Transactional
    public StockCheckResponseDTO getVariantStockData(Long variantId){

        ChairVariant chairVariant = chairVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chair variant not found with id: " + variantId));

        ChairVariantDTO variant = toDto(chairVariant);

        boolean isInStock = variant.getStockQuantity() > 0;

        return new StockCheckResponseDTO(
                variant.getId(),
                isInStock,
                variant.getStockQuantity(),
                variant.getFinalPrice()
        );
    }
    @Transactional
    public List<StockCheckResponseDTO> getVariantStockData(List<Long> variantIds) {

        List<ChairVariant> chairVariants = chairVariantRepository.findAllById(variantIds);
        if (chairVariants.isEmpty()) {
            throw new ResourceNotFoundException(("Chair variants not found with provided IDs"));
        }

        List<ChairVariantDTO> variants = chairVariants.stream().map(this::toDto).toList();

        return variants.stream()
                .map(v -> new StockCheckResponseDTO(
                        v.getId(),
                        v.getStockQuantity() > 0,
                        v.getStockQuantity(),
                        v.getFinalPrice()
                )).toList();
    }

    public String uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {

        String fileName = fileStorageService.save(file);
        String url = "/api/products/uploads/" + fileName;

        ChairVariant variant = chairVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chair variant not found with id: " + id));

        variant.setImageUrl(url);
        chairVariantRepository.save(variant);
        return url;
    }
    @Transactional
    public void deleteVariant(Long variantId) {
        ChairVariant chairVariant = chairVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chair variant not found with id: " + variantId));

        fileStorageService.deleteFileByUrl(chairVariant.getImageUrl());

        chairVariantRepository.delete(chairVariant);
    }
}