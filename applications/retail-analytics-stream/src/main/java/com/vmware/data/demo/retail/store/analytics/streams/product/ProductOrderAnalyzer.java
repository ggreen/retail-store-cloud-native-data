package com.vmware.data.demo.retail.store.analytics.streams.product;

import com.vmware.data.demo.retail.store.api.order.product.repositories.ProductAssociationEntity;
import com.vmware.data.demo.retail.store.api.order.product.repositories.ProductJdbcAssociationRepository;
import nyla.solutions.core.patterns.machineLearning.associations.AssociationProbabilities;
import nyla.solutions.core.patterns.machineLearning.associations.ProductAssociation;
import nyla.solutions.core.patterns.machineLearning.associations.ProductTransition;
import nyla.solutions.core.patterns.observer.SubjectObserver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProductOrderAnalyzer implements SubjectObserver<ProductAssociation>
{
    private final JdbcTemplate jdbcTemplate;
    private final String selectSql;
    private final AssociationProbabilities analyzer;
    private final ProductJdbcAssociationRepository associateRepository;

    public ProductOrderAnalyzer(@Value("${select.orders}") String selectSql, JdbcTemplate jdbcTemplate,
                                AssociationProbabilities analyzer,
                                ProductJdbcAssociationRepository productJdbcAssociationRepository)
    {
        this.jdbcTemplate = jdbcTemplate;
        this.selectSql = selectSql;
        this.analyzer = analyzer;
        this.associateRepository = productJdbcAssociationRepository;
        this.analyzer.addObserver(this);

    }

    @Scheduled(fixedDelay = 5000)
    public void constructProductsAssociations()
    {
        /*
         *   itemid integer,
         *   orderid integer,
         *   productid integer,
         *   quantity float8,
         *   productname text
         */

        RowCallbackHandler callback = transitionRowMapper();
        jdbcTemplate.query(this.selectSql,callback);

        this.analyzer.notifyFavoriteAssociations();
    }

    protected RowCallbackHandler transitionRowMapper()
    {
        return (rs) ->
        {
            if(rs == null || !rs.next())
                return ;

            this.analyzer.learn(
                    new ProductTransition(rs.getInt("orderid"),rs.getString("productname")));
        };
    }

    @Override
    public void update(String subjectName, ProductAssociation productAssociation)
    {
        this.associateRepository.save(toProductEntity(productAssociation));
    }

    protected ProductAssociationEntity toProductEntity(ProductAssociation data)
    {
        if(data == null)
            return null;

        ProductAssociationEntity entity = new ProductAssociationEntity(data.getProductName(),
                data.getAssociateNames());

        return entity;


    }
}
