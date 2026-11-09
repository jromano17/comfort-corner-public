package etf.nwt.product.config;

import etf.nwt.product.dto.CategoryDTO;
import etf.nwt.product.dto.ChairDTO;
import etf.nwt.product.model.Category;
import etf.nwt.product.model.Chair;
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

        // Minimal configuration
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(true);

        // Disable implicit mapping for problematic entities
        modelMapper.getConfiguration().setImplicitMappingEnabled(false);

        // Manually define ALL mappings
        defineAllMappings(modelMapper);

        return modelMapper;
    }

    private void defineAllMappings(ModelMapper modelMapper) {
        // Category → CategoryDTO
        modelMapper.createTypeMap(Category.class, CategoryDTO.class)
                .setPostConverter(context -> {
                    Category source = context.getSource();
                    CategoryDTO destination = context.getDestination();

                    // Manually set properties
                    destination.setId(source.getId());
                    destination.setName(source.getName());
                    destination.setDescription(source.getDescription());

                    // Set chair IDs if chairs exist
                    if (source.getChairs() != null) {
                        destination.setChairIds(source.getChairs().stream()
                                .map(Chair::getId)
                                .collect(Collectors.toList()));
                    }

                    return destination;
                });

        // CategoryDTO → Category
        modelMapper.createTypeMap(CategoryDTO.class, Category.class)
                .setPostConverter(context -> {
                    CategoryDTO source = context.getSource();
                    Category destination = context.getDestination();

                    destination.setId(source.getId());
                    destination.setName(source.getName());
                    destination.setDescription(source.getDescription());
                    // Chairs will be set separately in service

                    return destination;
                });

        // Chair → ChairDTO
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
    }
}