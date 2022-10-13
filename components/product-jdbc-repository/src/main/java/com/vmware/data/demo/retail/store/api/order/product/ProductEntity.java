package com.vmware.data.demo.retail.store.api.order.product;

import com.vmware.data.demo.retail.store.domain.Product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="product")
public class ProductEntity  extends Product
{

    @Id
    @Column(name="productid")
    @Override
    public int getProductId()
    {
        return super.getProductId();
    }
}
