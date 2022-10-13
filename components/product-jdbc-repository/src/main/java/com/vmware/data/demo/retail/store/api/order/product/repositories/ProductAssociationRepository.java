package com.vmware.data.demo.retail.store.api.order.product.repositories;

import java.util.Optional;

/**
 * ProductAssociationRepository
 *
 * @author Gregory Green
 */
public interface ProductAssociationRepository
{
    void save(ProductAssociationEntity entity);

    Optional<ProductAssociationEntity> findById(ProductAssociationEntity expected);
}
