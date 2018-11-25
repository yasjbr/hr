<g:if test="${listEmployee?.promotionReason in [ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason.UPDATE_MILITARY_RANK_TYPE]}">
    <el:formGroup>
        <el:autocomplete
                optionKey="id"
                optionValue="name"
                size="6"
                class=" isRequired"
                controller="militaryRankType"
                action="autocomplete"
                values="${[[listEmployee?.militaryRankType?.id, listEmployee?.militaryRankType?.descriptionInfo?.localName]]}"
                name="militaryRankType"
                label="${message(code: 'militaryRankType.label', default: 'militaryRankType')}"/>

        <el:dateField name="militaryRankTypeDate" size="6" class=" isRequired" value="${listEmployee?.militaryRankTypeDate}" label="${message(code: 'employeePromotion.militaryRankTypeDate.label')}" />
    </el:formGroup>
</g:if>



<g:elseif test="${listEmployee?.promotionReason in [ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason.UPDATE_MILITARY_RANK_CLASSIFICATION]}">
    <el:formGroup>
        <el:autocomplete
                optionKey="id"
                optionValue="name"
                size="6"
                class=" isRequired"
                controller="militaryRankClassification"
                action="autocomplete"
                name="militaryRankClassification"
                values="${[[listEmployee?.militaryRankClassification?.id, listEmployee?.militaryRankClassification?.descriptionInfo?.localName]]}"
                label="${message(code: 'militaryRankClassification.label', default: 'militaryRankClassification')}"/>
        <el:dateField name="militaryRankTypeDate" size="6" class=" isRequired" value="${listEmployee?.militaryRankTypeDate}" label="${message(code: 'employeePromotion.militaryRankTypeDate.label')}" />
    </el:formGroup>
</g:elseif>

<g:elseif test="${listEmployee?.promotionReason in [ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason.PERIOD_SETTLEMENT_OLD_ARREST, ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason.PERIOD_SETTLEMENT_EMPLOYMENT_PERIOD]}">
    <el:formGroup>
        <el:dateField name="employmentDate" size="${colSize?:8}" class=" isRequired" value="" label="${message(code: 'employee.employmentDate.label')}" />
    </el:formGroup>
</g:elseif>
<g:else>
    <el:formGroup>
        <el:autocomplete
                optionKey="id"
                optionValue="name"
                size="6"
                class=" isRequired"
                controller="militaryRank"
                action="autocomplete"
                values="${[[listEmployee?.militaryRank?.id, listEmployee?.militaryRank?.descriptionInfo?.localName]]}"
                paramsGenerateFunction="militaryRankParams"
                name="militaryRank"
                label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>

        <el:dateField name="actualDueDate" size="6" class=" isRequired" value="${listEmployee?.actualDueDate}" label="${message(code: 'promotionListEmployee.actualDueDate.label')}" />
    </el:formGroup>
</g:else>
