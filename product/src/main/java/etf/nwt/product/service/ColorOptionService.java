package etf.nwt.product.service;

import etf.nwt.product.dto.ColorOptionDTO;
import etf.nwt.product.exception.InvalidOperationException;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.model.ColorOption;
import etf.nwt.product.repository.ColorOptionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ColorOptionService {

    private final ColorOptionRepository colorOptionRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ColorOptionDTO createColorOption(ColorOptionDTO colorOptionDTO) {
        if (colorOptionRepository.existsByName(colorOptionDTO.getName())) {
            throw new InvalidOperationException(
                    "Color option with name '" + colorOptionDTO.getName() + "' already exists");
        }

        if (colorOptionRepository.existsByHexCode(colorOptionDTO.getHexCode())) {
            throw new InvalidOperationException(
                    "Color option with hex code '" + colorOptionDTO.getHexCode() + "' already exists");
        }

        ColorOption colorOption = modelMapper.map(colorOptionDTO, ColorOption.class);
        ColorOption saved = colorOptionRepository.save(colorOption);

        return modelMapper.map(saved, ColorOptionDTO.class);
    }

    @Transactional(readOnly = true)
    public ColorOptionDTO getColorOptionById(Long id) {
        ColorOption colorOption = colorOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Color option not found with id: " + id));

        return modelMapper.map(colorOption, ColorOptionDTO.class);
    }

    @Transactional(readOnly = true)
    public ColorOptionDTO getColorOptionByName(String name) {
        ColorOption colorOption = colorOptionRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Color option not found with name: " + name));

        return modelMapper.map(colorOption, ColorOptionDTO.class);
    }

    @Transactional(readOnly = true)
    public ColorOptionDTO getColorOptionByHexCode(String hexCode) {
        ColorOption colorOption = colorOptionRepository.findByHexCode(hexCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Color option not found with hex code: " + hexCode));

        return modelMapper.map(colorOption, ColorOptionDTO.class);
    }

    @Transactional(readOnly = true)
    public List<ColorOptionDTO> getAllColorOptions() {
        return colorOptionRepository.findAll()
                .stream()
                .map(color -> modelMapper.map(color, ColorOptionDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ColorOptionDTO> searchColorOptions(String keyword) {
        return colorOptionRepository.search(keyword)
                .stream()
                .map(color -> modelMapper.map(color, ColorOptionDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public ColorOptionDTO updateColorOption(Long id, ColorOptionDTO colorOptionDTO) {
        ColorOption existing = colorOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Color option not found with id: " + id));

        if (!existing.getName().equals(colorOptionDTO.getName()) &&
                colorOptionRepository.existsByNameAndIdNot(colorOptionDTO.getName(), id)) {
            throw new InvalidOperationException(
                    "Color option with name '" + colorOptionDTO.getName() + "' already exists");
        }

        if (!existing.getHexCode().equals(colorOptionDTO.getHexCode()) &&
                colorOptionRepository.existsByHexCodeAndIdNot(colorOptionDTO.getHexCode(), id)) {
            throw new InvalidOperationException(
                    "Color option with hex code '" + colorOptionDTO.getHexCode() + "' already exists");
        }

        existing.setName(colorOptionDTO.getName());
        existing.setHexCode(colorOptionDTO.getHexCode());

        ColorOption updated = colorOptionRepository.save(existing);

        return modelMapper.map(updated, ColorOptionDTO.class);
    }

    @Transactional
    public void deleteColorOption(Long id) {
        if (!colorOptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Color option not found with id: " + id);
        }
        colorOptionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return colorOptionRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return colorOptionRepository.existsByName(name);
    }

    @Transactional(readOnly = true)
    public boolean existsByHexCode(String name) {
        return colorOptionRepository.existsByHexCode(name);
    }

}