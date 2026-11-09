package etf.nwt.product.service;

import etf.nwt.product.dto.MaterialDTO;
import etf.nwt.product.exception.InvalidOperationException;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.model.Material;
import etf.nwt.product.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public MaterialDTO createMaterial(MaterialDTO materialDTO) {
        if (materialRepository.existsByName(materialDTO.getName())) {
            throw new InvalidOperationException(
                    "Material with name '" + materialDTO.getName() + "' already exists");
        }
        Material material = modelMapper.map(materialDTO, Material.class);
        Material saved = materialRepository.save(material);
        return modelMapper.map(saved, MaterialDTO.class);
    }

    @Transactional(readOnly = true)
    public MaterialDTO getMaterialById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Material not found with id: " + id));

        return modelMapper.map(material, MaterialDTO.class);
    }

    @Transactional(readOnly = true)
    public MaterialDTO getMaterialByName(String name) {
        Material material = materialRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Material not found with name: " + name));

        return modelMapper.map(material, MaterialDTO.class);
    }

    @Transactional(readOnly = true)
    public List<MaterialDTO> getAllMaterials() {
        return materialRepository.findAll()
                .stream()
                .map(material -> modelMapper.map(material, MaterialDTO.class))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<MaterialDTO> searchMaterials(String keyword) {
        return materialRepository.search(keyword)
                .stream()
                .map(material -> modelMapper.map(material, MaterialDTO.class))
                .collect(Collectors.toList());
    }


    @Transactional
    public MaterialDTO updateMaterial(Long id, MaterialDTO materialDTO) {
        Material existing = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Material not found with id: " + id));

        if (!existing.getName().equals(materialDTO.getName()) &&
                materialRepository.existsByNameAndIdNot(materialDTO.getName(), id)) {
            throw new InvalidOperationException(
                    "Material with name '" + materialDTO.getName() + "' already exists");
        }

        existing.setName(materialDTO.getName());
        existing.setDescription(materialDTO.getDescription());

        Material updated = materialRepository.save(existing);

        return modelMapper.map(updated, MaterialDTO.class);
    }

    @Transactional
    public void deleteMaterial(Long id) {
        if (!materialRepository.existsById(id)) {
            throw new ResourceNotFoundException("Material not found with id: " + id);
        }
        materialRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return materialRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existByName(String name){
        return materialRepository.existsByName(name);
    }
}