package ru.yandex.practicum.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.interaction.api.dto.NewProductWarehouseRequest;
import ru.yandex.practicum.warehouse.model.CachedProduct;
import ru.yandex.practicum.warehouse.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "dimension.width", target = "width")
    @Mapping(source = "dimension.height", target = "height")
    @Mapping(source = "dimension.depth", target = "depth")
    @Mapping(target = "quantity", ignore = true)
    Product toEntity(NewProductWarehouseRequest request);

    Product toEntity(CachedProduct cachedProduct);

    CachedProduct toCachedProduct(Product entity);
}
