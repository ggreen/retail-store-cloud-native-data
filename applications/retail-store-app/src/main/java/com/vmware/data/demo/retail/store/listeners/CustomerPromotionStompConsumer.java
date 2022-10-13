package com.vmware.data.demo.retail.store.listeners;

import com.vmware.data.demo.retail.store.domain.Promotion;
import org.apache.geode.cache.EntryEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * CustomerPromotionCacheListener
 *
 * @author Gregory Green
 */
@Component
public class CustomerPromotionStompConsumer implements Consumer<EntryEvent<String, Collection<Promotion>>>
{
    private final SimpMessagingTemplate messageTemple;

    public CustomerPromotionStompConsumer(SimpMessagingTemplate messageTemple)
    {
        this.messageTemple = messageTemple;
    }

    /**
     * Performs this operation on the given argument.
     *
     * @param promotionEntryEvent the input argument
     */
    @Override
    public void accept(EntryEvent<String, Collection<Promotion>> promotionEntryEvent)
    {
        String userName = promotionEntryEvent.getKey();
        String destination = "/topic/customerPromotions/"+userName;

        Collection<Promotion>  promotions = promotionEntryEvent.getNewValue();
        if(promotions == null)
            return;

        for (Promotion promotion: promotions) {

            messageTemple.convertAndSend(destination,promotion);
        }
    }
}
