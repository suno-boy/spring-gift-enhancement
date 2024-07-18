package gift.Service;

import gift.Entity.OptionEntity;
import gift.Entity.ProductEntity;
import gift.DTO.OptionDTO;
import gift.Repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OptionService {

    @Autowired
    private OptionRepository optionRepository;

    public OptionDTO createOption(OptionDTO optionDTO) {
        validateOptionNameUniqueness(optionDTO.getName(), optionDTO.getProductId());
        OptionEntity optionEntity = new OptionEntity(
                optionDTO.getName(),
                optionDTO.getQuantity(),
                new ProductEntity()
        );
        optionEntity = optionRepository.save(optionEntity);
        return convertToDTO(optionEntity);
    }

    public OptionDTO getOptionById(Long id) {
        OptionEntity optionEntity = optionRepository.findById(id).orElseThrow(() -> new RuntimeException("Option not found"));
        return convertToDTO(optionEntity);
    }

    public List<OptionDTO> getAllOptions() {
        List<OptionEntity> optionEntities = optionRepository.findAll();
        return optionEntities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public OptionDTO updateOption(Long id, OptionDTO optionDTO) {
        OptionEntity optionEntity = optionRepository.findById(id).orElseThrow(() -> new RuntimeException("Option not found"));
        if (!optionEntity.getName().equals(optionDTO.getName())) {
            validateOptionNameUniqueness(optionDTO.getName(), optionDTO.getProductId());
        }
        optionEntity.setName(optionDTO.getName());
        optionEntity.setQuantity(optionDTO.getQuantity());
        optionEntity = optionRepository.save(optionEntity);
        return convertToDTO(optionEntity);
    }

    public void deleteOption(Long id) {
        optionRepository.deleteById(id);
    }

    private OptionDTO convertToDTO(OptionEntity optionEntity) {
        return new OptionDTO(
                optionEntity.getId(),
                optionEntity.getName(),
                optionEntity.getQuantity(),
                optionEntity.getProduct() != null ? optionEntity.getProduct().getId() : null
        );
    }

    private void validateOptionNameUniqueness(String name, Long productId) {
        List<OptionEntity> options = optionRepository.findByProductId(productId);
        for (OptionEntity option : options) {
            if (option.getName().equals(name)) {
                throw new RuntimeException("동일한 상품 내에서 옵션 이름이 중복될 수 없습니다.");
            }
        }
    }
}
