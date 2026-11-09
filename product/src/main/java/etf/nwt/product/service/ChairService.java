package etf.nwt.product.service;

import etf.nwt.product.dto.CategoryDTO;
import etf.nwt.product.dto.ChairDTO;
import etf.nwt.product.dto.ChairVariantDTO;
import etf.nwt.product.dto.SupplierDTO;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.model.Category;
import etf.nwt.product.model.Chair;
import etf.nwt.product.model.ChairVariant;
import etf.nwt.product.model.Supplier;
import etf.nwt.product.repository.CategoryRepository;
import etf.nwt.product.repository.ChairRepository;
import etf.nwt.product.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChairService {

    private final ChairRepository chairRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final FileStorageService fileStorageService;
    private final ModelMapper modelMapper;

    @Autowired
    public ChairService(ChairRepository chairRepository,
                        CategoryRepository categoryRepository,
                        SupplierRepository supplierRepository,
                        FileStorageService fileStorageService,
                        ModelMapper modelMapper) {
        this.chairRepository = chairRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.fileStorageService = fileStorageService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ChairDTO createChair(ChairDTO chairDTO) {
        Category category = categoryRepository.findById(chairDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + chairDTO.getCategoryId()));

        Supplier supplier = supplierRepository.findById(chairDTO.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + chairDTO.getSupplierId()));

        Chair chair = modelMapper.map(chairDTO, Chair.class);

        chair.setCategory(category);
        chair.setSupplier(supplier);

        Chair saved = chairRepository.save(chair);

        return modelMapper.map(saved, ChairDTO.class);
    }

    @Transactional
    public ChairDTO getChairById(Long id) {
        Chair chair = chairRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chair not found with id: " + id));

        ChairDTO dto = modelMapper.map(chair, ChairDTO.class);
        List<String> masterGallery = new ArrayList<>();
        if (chair.getGalleryImageUrls() != null) {
            masterGallery.addAll(chair.getGalleryImageUrls());
        }
        if (chair.getVariants() != null) {
            List<String> variantImages = chair.getVariants().stream()
                    .map(ChairVariant::getImageUrl)
                    .filter(url -> url != null && !url.isEmpty())
                    .toList();

            masterGallery.addAll(variantImages);
        }
        dto.setAllImages(masterGallery);

        return dto;
    }

    @Transactional
    public List<ChairDTO> getAllChairs() {
        return chairRepository.findAll()
                .stream()
                .map(chair -> modelMapper.map(chair, ChairDTO.class))
                //.map(this::toDto)
                .collect(Collectors.toList());
    }

    private ChairDTO toDto(Chair chair) {
        ChairDTO dto = modelMapper.map(chair, ChairDTO.class);

        if (chair.getCategory() != null) {
            dto.setCategoryId(chair.getCategory().getId());
            dto.setCategory(modelMapper.map(chair.getCategory(), CategoryDTO.class));
        }

        if (chair.getSupplier() != null) {
            dto.setSupplierId(chair.getSupplier().getId());
            dto.setSupplier(modelMapper.map(chair.getSupplier(), SupplierDTO.class));
        }

        if (chair.getVariants() != null && !chair.getVariants().isEmpty()) {
            dto.setVariants(chair.getVariants().stream()
                    .map(variant -> modelMapper.map(variant, ChairVariantDTO.class))
                    .collect(Collectors.toList()));
        }

        return dto;
    }
    public ChairDTO updateChair(ChairDTO dto) {
        Chair incomeRecord = chairRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Chair with ID " + dto.getId() + " not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + dto.getCategoryId()));


        Supplier suplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + dto.getSupplierId()));

        modelMapper.map(dto, incomeRecord);

        Chair saved = chairRepository.save(incomeRecord);
        return modelMapper.map(saved,ChairDTO.class);
    }
    public void deleteChair(Long id) {
        Chair chair = chairRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chair with ID " + id + " not found"));

        if (chair.getGalleryImageUrls() != null) {
            for (String url : chair.getGalleryImageUrls()) {
                fileStorageService.deleteFileByUrl(url);
            }
        }
       if (chair.getVariants() != null) {
            for (ChairVariant variant : chair.getVariants()) {
                fileStorageService.deleteFileByUrl(variant.getImageUrl());
            }
        }
        chairRepository.deleteById(id);
    }

    public String uploadImage(Long id, MultipartFile file) {
        String fileName = fileStorageService.save(file);
        String url = "/api/products/uploads/" + fileName;

        Chair chair = chairRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Chair with ID " + id + " not found"));
        chair.getGalleryImageUrls().add(url);
        chairRepository.save(chair);
        return url;
    }

    public List<String> uploadMultipleImages(Long id, List<MultipartFile> files) {
        Chair chair = chairRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chair with ID " + id + " not found"));
        List<String> uploadedUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = fileStorageService.save(file);
            String url = "/api/products/uploads/" + fileName;
            
            uploadedUrls.add(url);
            chair.getGalleryImageUrls().add(url); 
        }
        chairRepository.save(chair);
        return uploadedUrls;
    }
}