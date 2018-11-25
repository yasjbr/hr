<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employee" action="autocomplete" name="employee.id" label="${message(code:'promotionListEmployee.employee.label',default:'employee')}" values="${[[promotionListEmployee?.employee?.id,promotionListEmployee?.employee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="militaryRank" action="autocomplete" name="militaryRank.id" label="${message(code:'promotionListEmployee.militaryRank.label',default:'militaryRank')}" values="${[[promotionListEmployee?.militaryRank?.id,promotionListEmployee?.militaryRank?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField class=" isRequired" name="promotionList" size="8" label="${message(code:'promotionList.label',default:'promotionList')}" value=""/>
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumPromotionReason"  from="${ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason.values()}" name="promotionReason" size="8"  class=" isRequired" label="${message(code:'promotionListEmployee.promotionReason.label',default:'promotionReason')}" value="${promotionListEmployee?.promotionReason}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class=" isRequired" label="${message(code:'promotionListEmployee.recordStatus.label',default:'recordStatus')}" value="${promotionListEmployee?.recordStatus}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="statusReason" size="8"  class="" label="${message(code:'promotionListEmployee.statusReason.label',default:'statusReason')}" value="${promotionListEmployee?.statusReason}"/>
</el:formGroup>