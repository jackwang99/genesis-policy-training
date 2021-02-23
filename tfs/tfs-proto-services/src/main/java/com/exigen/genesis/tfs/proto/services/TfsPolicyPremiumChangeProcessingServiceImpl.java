package com.exigen.genesis.tfs.proto.services;

import com.eisgroup.genesis.factory.model.billedpolicytax.impl.BilledPolicyTaxEntityImpl;
import com.eisgroup.genesis.factory.model.billedpolicytax.impl.BilledPolicyTaxFactory;
import com.eisgroup.genesis.factory.model.billedpolicytax.impl.PremiumTaxEntryEntityImpl;
import com.eisgroup.genesis.factory.modeling.types.BilledPolicyTax;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.tfs.services.api.TfsCalculationContext;
import com.eisgroup.genesis.tfs.services.api.TfsPolicyPremiumChangeProcessingService;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import java.util.stream.Collectors;

/**
 * @author arsatyants
 * @since 10.4
 */
public class TfsPolicyPremiumChangeProcessingServiceImpl implements TfsPolicyPremiumChangeProcessingService {

    public static final int DEFAULT_ROUNDING_SCALE = 8;
    private final  BilledPolicyTaxFactory factory  = new BilledPolicyTaxFactory();

   public BilledPolicyTax process(TfsCalculationContext context){

       BilledPolicyTax billedPolicyTax = factory.createBilledPolicyTaxEntity();
       billedPolicyTax.setPolicyModelVersion(context.getPremiumChangeData().getPolicyModelVersion());
       billedPolicyTax.setPolicyModelName(context.getPremiumChangeData().getPolicyModelName());
       billedPolicyTax.setEffectiveDate(context.getPremiumChangeData().getEffectiveDate());
       billedPolicyTax.setCreationDate(LocalDateTime.now());
       billedPolicyTax.setPolicyNumber(context.getPremiumChangeData().getPolicyNumber());
       ((BilledPolicyTaxEntityImpl) billedPolicyTax).setKey(new RootEntityKey(UUID.randomUUID(), context.getPremiumChangeData().getPolicyRevisionNo()));
       billedPolicyTax.setPremiumTxEntries(context.getRatingData().getTaxPremiumResult().stream().map(rule -> {
         PremiumTaxEntryEntityImpl premiumTaxEntryEntity = (PremiumTaxEntryEntityImpl) factory.createPremiumTaxEntryEntity();
         premiumTaxEntryEntity.setTaxRate(rule.getRate());
         premiumTaxEntryEntity.setChangePremiumAmount(rule.getPremiumAmount());
         premiumTaxEntryEntity.setCreationDateTime(LocalDateTime.now());
         premiumTaxEntryEntity.setOrder(context.getLastOrderNumber()+1);
         premiumTaxEntryEntity.setPolicyNumber(context.getPremiumChangeData().getPolicyNumber());
         premiumTaxEntryEntity.setPolicy(context.getEntityLink());
         premiumTaxEntryEntity.setTotalPremiumAmount(context.getPremiumChangeData().getTotalPremium());
         premiumTaxEntryEntity.setPolicyNumber(context.getPremiumChangeData().getPolicyNumber());
         int roundingScale  = rule.getRoundable() ? rule.getRoundingScale().intValue() : DEFAULT_ROUNDING_SCALE;
         premiumTaxEntryEntity.setUnbilledPremiumAmount(context.getPremiumChangeData().getChangePremium());
         premiumTaxEntryEntity.setUnbilledTaxAmount(context.getPremiumChangeData().getChangePremium().multiply(rule.getRate()).setScale(roundingScale, RoundingMode.HALF_UP));

         return premiumTaxEntryEntity;
       }).collect(Collectors.toList()));

       return billedPolicyTax;
    }
}
