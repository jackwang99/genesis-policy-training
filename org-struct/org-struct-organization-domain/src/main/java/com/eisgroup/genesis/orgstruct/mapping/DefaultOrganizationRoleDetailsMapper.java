/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.orgstruct.mapping;

import com.eisgroup.genesis.factory.model.organization.immutable.AgencyEntity;
import com.eisgroup.genesis.factory.model.organization.immutable.InsurerEntity;
import com.eisgroup.genesis.factory.model.organization.immutable.ProducerEntity;
import com.eisgroup.genesis.factory.model.organization.immutable.UnderwritingCompanyEntity;
import com.eisgroup.genesis.factory.modeling.types.immutable.OrganizationRole;
import com.eisgroup.genesis.factory.modeling.types.immutable.OrganizationRoleDetails;
import com.eisgroup.genesis.mapping.RoleDetailsMapper;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static com.eisgroup.genesis.orgstruct.mapping.OrganizationRoleTypes.*;

/**
 * Default mapper to retrieve role details code by role type.
 *
 * @author gvisokinskas
 */
public class DefaultOrganizationRoleDetailsMapper implements RoleDetailsMapper {
    private static final Collection<SupportedRole<?>> SUPPORTED_ROLES = ImmutableList.of(
            SupportedRole.create(AgencyEntity.class, AgencyEntity::getAgencyCd, AGENCY_ROLE),
            SupportedRole.create(InsurerEntity.class, InsurerEntity::getInsurerCd, INSURER_ROLE),
            SupportedRole.create(ProducerEntity.class, ProducerEntity::getProducerCd, PRODUCER_ROLE),
            SupportedRole.create(UnderwritingCompanyEntity.class, UnderwritingCompanyEntity::getUnderwritingCompanyCd, UNDERWRITER_ROLE)
    );

    @Override
    public String retrieveRoleDetailsCode(OrganizationRole role) {
        return SUPPORTED_ROLES.stream()
                .map(supportedRole -> supportedRole.tryResolve(role))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new UnsupportedRoleException(role.getRoleTypeCd()));
    }

    private static final class SupportedRole<T extends OrganizationRoleDetails> {
        private final Class<T> clazz;
        private final Function<T, String> mapper;
        private final String code;

        private SupportedRole(Class<T> clazz, Function<T, String> mapper, String code) {
            this.clazz = clazz;
            this.mapper = mapper;
            this.code = code;
        }

        private static <T extends OrganizationRoleDetails> SupportedRole<T> create(Class<T> clazz, Function<T, String> mapper, String code) {
            return new SupportedRole<>(clazz, mapper, code);
        }

        private Optional<String> tryResolve(OrganizationRole role) {
            return Optional.of(role)
                    .filter(r -> Objects.equals(r.getRoleTypeCd(), code))
                    .map(OrganizationRole::getRoleDetails)
                    .filter(clazz::isInstance)
                    .map(clazz::cast)
                    .map(details -> Optional.ofNullable(mapper.apply(details))
                            .orElse(StringUtils.EMPTY));
        }
    }
}
