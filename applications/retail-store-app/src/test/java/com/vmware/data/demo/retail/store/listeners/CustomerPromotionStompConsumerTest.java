package com.vmware.data.demo.retail.store.listeners;

import com.vmware.data.demo.retail.store.domain.Promotion;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.EntryEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerPromotionStompConsumerTest
{
    @Mock
    private SimpMessagingTemplate messageTemple;

    @Mock
    private EntryEvent<String, Collection<Promotion>> event;

    @Test
    void accept_WhenNotPromotions_Then_DoNotSend()
    {
        var subject = new CustomerPromotionStompConsumer(messageTemple);
        subject.accept(event);

        verify(messageTemple,never()).convertAndSend(anyString(),any(Promotion.class));
    }

    @Test
    void accept_WhenNotNull()
    {
        var subject = new CustomerPromotionStompConsumer(messageTemple);
        Collection<Promotion> promotions = JavaBeanGeneratorCreator.of(Promotion.class).createCollection(1);
        String userName = "josiah";
        when(event.getNewValue()).thenReturn(promotions);
        when(event.getKey()).thenReturn(userName);
        subject.accept(event);

        verify(messageTemple).convertAndSend(anyString(),any(Promotion.class));
    }
}