package com.vmware.data.demo.retail.store.api.order.product.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductJdbcAssociationRepository implements ProductAssociationRepository
        //extends CrudRepository<ProductAssociationEntity,String>
{
    private final JdbcTemplate jdbcTemplate;

    public ProductJdbcAssociationRepository(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(ProductAssociationEntity entity)
    {
        if(entity == null)
            return;
        String id = entity.getId();
        if(id == null || id.trim().length() == 0)
            return;

        String associations = entity.retrieveAssociationsText();
        if(associations == null || associations.trim().length() == 0)
        {
            jdbcTemplate.update("delete from pivotalmarkets.product_association " +
                            "WHERE id= "+entity.getId());
            return;
        }


       int cnt = jdbcTemplate.update("UPDATE pivotalmarkets.product_association " +
                "SET associations= " + SqlUtil.escape(associations) +" "+
                " WHERE id= "+ SqlUtil.escape(entity.getId()));

       if(cnt == 0)
       {
           jdbcTemplate.update("INSERT INTO pivotalmarkets.product_association " +
                           "(id, associations) " +
                           "VALUES("+ SqlUtil.escape(entity.getId()) +", "+ SqlUtil.escape(entity.retrieveAssociationsText()) +"); ");
       }

    }

    @Override
    public Optional<ProductAssociationEntity> findById(ProductAssociationEntity expected)
    {
        ProductAssociationEntity output = jdbcTemplate.queryForObject("SELECT id, associations " +
                "FROM pivotalmarkets.product_association  ", (rs, i) -> {
            return new ProductAssociationEntity(rs.getString(1),rs.getString(2));
        });

        return Optional.of(output);
    }
}
