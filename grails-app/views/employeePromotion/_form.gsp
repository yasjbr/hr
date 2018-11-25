
<el:formGroup>
    <el:dateField name="actualDueDate"  size="8" class=" isRequired" label="${message(code:'employeePromotion.actualDueDate.label',default:'actualDueDate')}" value="${employeePromotion?.actualDueDate}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="dueDate"  size="8" class=" isRequired" label="${message(code:'employeePromotion.dueDate.label',default:'dueDate')}" value="${employeePromotion?.dueDate}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumPromotionReason"  from="${ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason.values()}" name="dueReason" size="8"  class=" isRequired" label="${message(code:'employeePromotion.dueReason.label',default:'dueReason')}" value="${employeePromotion?.dueReason}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employee" action="autocomplete" name="employee.id" label="${message(code:'employeePromotion.employee.label',default:'employee')}" values="${[[employeePromotion?.employee?.id,employeePromotion?.employee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="managerialOrderNumber" size="8"  class=" isRequired" label="${message(code:'employeePromotion.managerialOrderNumber.label',default:'managerialOrderNumber')}" value="${employeePromotion?.managerialOrderNumber}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="militaryRank" action="autocomplete" name="managerialRank.id" label="${message(code:'employeePromotion.managerialRank.label',default:'managerialRank')}" values="${[[employeePromotion?.managerialRank?.id,employeePromotion?.managerialRank?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="managerialRankDate"  size="8" class=" isRequired" label="${message(code:'employeePromotion.managerialRankDate.label',default:'managerialRankDate')}" value="${employeePromotion?.managerialRankDate}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="militaryRank" action="autocomplete" name="militaryRank.id" label="${message(code:'employeePromotion.militaryRank.label',default:'militaryRank')}" values="${[[employeePromotion?.militaryRank?.id,employeePromotion?.militaryRank?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="militaryRankType" action="autocomplete" name="militaryRankType.id" label="${message(code:'employeePromotion.militaryRankType.label',default:'militaryRankType')}" values="${[[employeePromotion?.militaryRankType?.id,employeePromotion?.militaryRankType?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8"  class="" label="${message(code:'employeePromotion.note.label',default:'note')}" value="${employeePromotion?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="promotionListEmployee" action="autocomplete" name="promotionListEmployee.id" label="${message(code:'employeePromotion.promotionListEmployee.label',default:'promotionListEmployee')}" values="${[[employeePromotion?.promotionListEmployee?.id,employeePromotion?.promotionListEmployee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="request" action="autocomplete" name="requestSource.id" label="${message(code:'employeePromotion.requestSource.label',default:'requestSource')}" values="${[[employeePromotion?.requestSource?.id,employeePromotion?.requestSource?.descriptionInfo?.localName]]}" />
</el:formGroup>