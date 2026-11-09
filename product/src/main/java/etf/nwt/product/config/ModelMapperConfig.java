package etf.nwt.product.config;

import etf.nwt.product.dto.CategoryDTO;
import etf.nwt.product.dto.ChairDTO;
import etf.nwt.product.dto.ChairVariantDTO;
import etf.nwt.product.dto.SupplierDTO;
import etf.nwt.product.model.Category;
import etf.nwt.product.model.Chair;
import etf.nwt.product.model.ChairVariant;
import etf.nwt.product.model.Supplier;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;


@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setImplicitMappingEnabled(true);

        configureCustomMappings(modelMapper);

        return modelMapper;
    }

    private void configureCustomMappings(ModelMapper modelMapper) {
        configureCategoryMappings(modelMapper);
        configureSupplierMappings(modelMapper);
        configureChairVariantMappings(modelMapper);
    }
    private void configureCategoryMappings(ModelMapper modelMapper) {
        modelMapper.emptyTypeMap(Category.class, CategoryDTO.class)
                .addMappings(mapper -> mapper.skip(CategoryDTO::setChairIds))
                .implicitMappings() // <-- Tell it to do the automatic stuff AFTER the skip
                .setPostConverter(context -> {
                    Category source = context.getSource();
                    CategoryDTO dest = context.getDestination();
                    if (source.getChairs() != null) {
                        dest.setChairIds(source.getChairs().stream().map(Chair::getId).collect(Collectors.toList()));
                    }
                    return dest;
                });

        // DTO -> Entity
        modelMapper.emptyTypeMap(CategoryDTO.class, Category.class)
                .addMappings(mapper -> mapper.skip(Category::setChairs))
                .implicitMappings()
                .setPostConverter(context -> {
                    CategoryDTO source = context.getSource();
                    Category dest = context.getDestination();
                    if (source.getChairIds() != null) {
                        dest.setChairs(source.getChairIds().stream().map(id -> {
                            Chair chair = new Chair();
                            chair.setId(id);
                            return chair;
                        }).collect(Collectors.toList()));
                    }
                    return dest;
                });
    }
    private void configureSupplierMappings(ModelMapper modelMapper) {

        modelMapper.emptyTypeMap(Supplier.class, SupplierDTO.class)
                .addMappings(mapper -> mapper.skip(SupplierDTO::setChairIds))
                .implicitMappings()
                .setPostConverter(context -> {
                    Supplier source = context.getSource();
                    SupplierDTO dest = context.getDestination();
                    if (source.getChairs() != null) {
                        dest.setChairIds(source.getChairs().stream().map(Chair::getId).collect(Collectors.toList()));
                    }
                    return dest;
                });

        // DTO -> Entity
        modelMapper.emptyTypeMap(SupplierDTO.class, Supplier.class)
                .addMappings(mapper -> mapper.skip(Supplier::setChairs))
                .implicitMappings()
                .setPostConverter(context -> {
                    SupplierDTO source = context.getSource();
                    Supplier dest = context.getDestination();
                    if (source.getChairIds() != null) {
                        dest.setChairs(source.getChairIds().stream().map(id -> {
                            Chair chair = new Chair();
                            chair.setId(id);
                            return chair;
                        }).collect(Collectors.toList()));
                    }
                    return dest;
                });
    }
    private void configureChairVariantMappings(ModelMapper modelMapper) {
        modelMapper.emptyTypeMap(ChairVariant.class, ChairVariantDTO.class)
                .addMappings(mapper -> mapper.map(src -> src.getChair().getId(), ChairVariantDTO::setChairId))
                .implicitMappings();

        // DTO -> Entity (This is the one that was crashing!)
        modelMapper.emptyTypeMap(ChairVariantDTO.class, ChairVariant.class)
                .addMappings(mapper -> mapper.skip(ChairVariant::setChair))
                .implicitMappings() // Do automatic mapping NOW, since chair is skipped
                .setPostConverter(context -> {
                    ChairVariantDTO source = context.getSource();
                    ChairVariant dest = context.getDestination();
                    if (source.getChairId() != null) {
                        Chair chair = new Chair();
                        chair.setId(source.getChairId());
                        dest.setChair(chair);
                    }
                    return dest;
                });
    }
    private void configureChairMappings(ModelMapper modelMapper) {
        modelMapper.emptyTypeMap(Chair.class, ChairDTO.class)
                .addMappings(mapper -> mapper.skip(ChairDTO::setAllImages))
                .implicitMappings();
    }
}
/*
import etf.nwt.product.dto.*;
import etf.nwt.product.model.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(true);

        modelMapper.getConfiguration().setImplicitMappingEnabled(false);

        defineAllMappings(modelMapper);

        return modelMapper;
    }

    private void defineAllMappings(ModelMapper modelMapper) {
        modelMapper.createTypeMap(MaterialDTO.class, Material.class)
                .addMapping(MaterialDTO::getName, Material::setName)
                .addMapping(MaterialDTO::getDescription, Material::setDescription);

        modelMapper.createTypeMap(Material.class, MaterialDTO.class)
                .addMapping(Material::getId, MaterialDTO::setId)
                .addMapping(Material::getName, MaterialDTO::setName)
                .addMapping(Material::getDescription, MaterialDTO::setDescription);

        modelMapper.createTypeMap(ColorOptionDTO.class, ColorOption.class)
                .addMapping(ColorOptionDTO::getName, ColorOption::setName)
                .addMapping(ColorOptionDTO::getHexCode, ColorOption::setHexCode);

        modelMapper.createTypeMap(ColorOption.class, ColorOptionDTO.class)
                .addMapping(ColorOption::getId, ColorOptionDTO::setId)
                .addMapping(ColorOption::getName, ColorOptionDTO::setName)
                .addMapping(ColorOption::getHexCode, ColorOptionDTO::setHexCode);

        modelMapper.createTypeMap(DimensionDTO.class, Dimension.class)
                .addMapping(DimensionDTO::getName, Dimension::setName)
                .addMapping(DimensionDTO::getHeight, Dimension::setHeight)
                .addMapping(DimensionDTO::getWidth, Dimension::setWidth)
                .addMapping(DimensionDTO::getWeightCapacity, Dimension::setWeightCapacity)
                .addMapping(DimensionDTO::getDepth, Dimension::setDepth);

        modelMapper.createTypeMap(Dimension.class, DimensionDTO.class)
                .addMapping(Dimension::getName, DimensionDTO::setName)
                .addMapping(Dimension::getId, DimensionDTO::setId)
                .addMapping(Dimension::getHeight, DimensionDTO::setHeight)
                .addMapping(Dimension::getWidth, DimensionDTO::setWidth)
                .addMapping(Dimension::getWeightCapacity, DimensionDTO::setWeightCapacity)
                .addMapping(Dimension::getDepth, DimensionDTO::setDepth);

        modelMapper.createTypeMap(Category.class, CategoryDTO.class)
                .setPostConverter(context -> {
                    Category source = context.getSource();
                    CategoryDTO destination = context.getDestination();

                    destination.setId(source.getId());
                    destination.setName(source.getName());
                    destination.setDescription(source.getDescription());

                    if (source.getChairs() != null) {
                        destination.setChairIds(source.getChairs().stream()
                                .map(Chair::getId)
                                .collect(Collectors.toList()));
                    }

                    return destination;
                });

        modelMapper.createTypeMap(CategoryDTO.class, Category.class)
                .setPostConverter(context -> {
                    CategoryDTO source = context.getSource();
                    Category destination = context.getDestination();

                    destination.setId(source.getId());
                    destination.setName(source.getName());
                    destination.setDescription(source.getDescription());

                    return destination;
                });

        modelMapper.createTypeMap(Chair.class, ChairDTO.class)
                .setPostConverter(context -> {
                    Chair source = context.getSource();
                    ChairDTO destination = context.getDestination();

                    destination.setId(source.getId());
                    destination.setName(source.getName());
                    destination.setDescription(source.getDescription());

                    if (source.getCategory() != null) {
                        destination.setCategoryId(source.getCategory().getId());
                    }


                    return destination;
                });

        modelMapper.createTypeMap(ChairDTO.class, Chair.class)
                .setPostConverter(context -> {
                    ChairDTO source = context.getSource();
                    Chair destination = context.getDestination();

                    destination.setId(source.getId());
                    destination.setName(source.getName());
                    destination.setDescription(source.getDescription());
                    return destination;
                });
        configureSupplierMappings(modelMapper);
        //configureChairVariantMappings(modelMapper);
    }
    private void configureSupplierMappings(ModelMapper modelMapper) {
        modelMapper.createTypeMap(SupplierDTO.class, Supplier.class)
                .setConverter(context -> {
                    SupplierDTO source = context.getSource();
                    Supplier destination = context.getDestination() != null ?
                            context.getDestination() : new Supplier();

                    if (source.getId() != null) {
                        destination.setId(source.getId());
                    }
                    destination.setName(source.getName());
                    destination.setContactEmail(source.getContactEmail());
                    destination.setCountry(source.getCountry());

                    if (source.getChairIds() != null && !source.getChairIds().isEmpty()) {
                        List<Chair> chairs = new ArrayList<>();
                        for (Long chairId : source.getChairIds()) {
                            Chair chair = new Chair();
                            chair.setId(chairId);
                            chairs.add(chair);
                        }
                        destination.setChairs(chairs);
                    }

                    return destination;
                });

        modelMapper.createTypeMap(Supplier.class, SupplierDTO.class)
                .setConverter(context -> {
                    Supplier source = context.getSource();
                    SupplierDTO destination = new SupplierDTO();

                    destination.setId(source.getId());
                    destination.setName(source.getName());
                    destination.setContactEmail(source.getContactEmail());
                    destination.setCountry(source.getCountry());

                    if (source.getChairs() != null) {
                        List<Long> chairIds = source.getChairs().stream()
                                .map(Chair::getId)
                                .collect(Collectors.toList());
                        destination.setChairIds(chairIds);

                        List<ChairDTO> chairDTOs = source.getChairs().stream()
                                .map(chair -> modelMapper.map(chair, ChairDTO.class))
                                .collect(Collectors.toList());
                        destination.setChairs(chairDTOs);
                    }

                    return destination;
                });
    }
    /*
    private void configureChairVariantMappings(ModelMapper modelMapper) {

        modelMapper.createTypeMap(ChairVariantDTO.class, ChairVariant.class)
                .setConverter(context -> {
                    ChairVariantDTO source = context.getSource();
                    ChairVariant destination = context.getDestination() != null ?
                            context.getDestination() : new ChairVariant();

                    if (source.getId() != null) {
                        destination.setId(source.getId());
                    }

                    if (source.getChairId() != null) {
                        Chair chair = new Chair();
                        chair.setId(source.getChairId());
                        destination.setChair(chair);
                    }

                    if (source.getMaterial() != null) {
                        destination.setMaterial(
                                modelMapper.map(source.getMaterial(), Material.class)
                        );
                    }

                    if (source.getColorOption() != null) {
                        destination.setColorOption(
                                modelMapper.map(source.getColorOption(), ColorOption.class)
                        );
                    }

                    if (source.getDimension() != null) {
                        destination.setDimension(
                                modelMapper.map(source.getDimension(), Dimension.class)
                        );
                    }

                    destination.setFinalPrice(source.getFinalPrice());
                    destination.setStockQuantity(source.getStockQuantity());

                    return destination;
                });

        modelMapper.createTypeMap(ChairVariant.class, ChairVariantDTO.class)
                .setConverter(context -> {
                    ChairVariant source = context.getSource();
                    ChairVariantDTO destination = new ChairVariantDTO();

                    destination.setId(source.getId());

                    if (source.getChair() != null) {
                        destination.setChairId(source.getChair().getId());

                        destination.setChair(
                                modelMapper.map(source.getChair(), ChairDTO.class)
                        );
                    }

                    if (source.getMaterial() != null) {
                        destination.setMaterial(
                                modelMapper.map(source.getMaterial(), MaterialDTO.class)
                        );
                    }

                    if (source.getColorOption() != null) {
                        destination.setColorOption(
                                modelMapper.map(source.getColorOption(), ColorOptionDTO.class)
                        );
                    }

                    if (source.getDimension() != null) {
                        destination.setDimension(
                                modelMapper.map(source.getDimension(), DimensionDTO.class)
                        );
                    }

                    destination.setFinalPrice(source.getFinalPrice());
                    destination.setStockQuantity(source.getStockQuantity());

                    return destination;
                });
    }
    private void configureChairVariantMappings(ModelMapper modelMapper) {

        modelMapper.createTypeMap(ChairVariantDTO.class, ChairVariant.class)
                .setConverter(context -> {
                    ChairVariantDTO source = context.getSource();
                    ChairVariant destination = context.getDestination() != null ?
                            context.getDestination() : new ChairVariant();

                    if (source.getId() != null) {
                        destination.setId(source.getId());
                    }

                    if (source.getChairId() != null) {
                        Chair chair = new Chair();
                        chair.setId(source.getChairId());
                        destination.setChair(chair);
                    }

                    if (source.getMaterial() != null) {
                        destination.setMaterial(
                                modelMapper.map(source.getMaterial(), Material.class)
                        );
                    }

                    if (source.getColorOption() != null) {
                        destination.setColorOption(
                                modelMapper.map(source.getColorOption(), ColorOption.class)
                        );
                    }

                    if (source.getDimension() != null) {
                        destination.setDimension(
                                modelMapper.map(source.getDimension(), Dimension.class)
                        );
                    }

                    if (source.getFinalPrice() != null) {
                        destination.setFinalPrice(source.getFinalPrice());
                    }

                    if (source.getStockQuantity() != null) {
                        destination.setStockQuantity(source.getStockQuantity());
                    }



                    return destination;
                });

        modelMapper.createTypeMap(ChairVariant.class, ChairVariantDTO.class)
                .setConverter(context -> {
                    ChairVariant source = context.getSource();
                    ChairVariantDTO destination = new ChairVariantDTO();

                    destination.setId(source.getId());

                    if (source.getChair() != null) {
                        destination.setChairId(source.getChair().getId());
                        destination.setChair(
                                modelMapper.map(source.getChair(), ChairDTO.class)
                        );
                    }

                    if (source.getMaterial() != null) {
                        destination.setMaterial(
                                modelMapper.map(source.getMaterial(), MaterialDTO.class)
                        );
                    }

                    if (source.getColorOption() != null) {
                        destination.setColorOption(
                                modelMapper.map(source.getColorOption(), ColorOptionDTO.class)
                        );
                    }

                    if (source.getDimension() != null) {
                        destination.setDimension(
                                modelMapper.map(source.getDimension(), DimensionDTO.class)
                        );
                    }

                    destination.setFinalPrice(source.getFinalPrice());
                    destination.setStockQuantity(source.getStockQuantity());

                    return destination;
                });
    }

}*/