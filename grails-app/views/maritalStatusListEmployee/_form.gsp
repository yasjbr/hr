
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="maritalStatusList" action="autocomplete" name="maritalStatusList.id" label="${message(code:'maritalStatusListEmployee.maritalStatusList.label',default:'maritalStatusList')}" values="${[[maritalStatusListEmployee?.maritalStatusList?.id,maritalStatusListEmployee?.maritalStatusList?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="maritalStatusRequest" action="autocomplete" name="maritalStatusRequest.id" label="${message(code:'maritalStatusListEmployee.maritalStatusRequest.label',default:'maritalStatusRequest')}" values="${[[maritalStatusListEmployee?.maritalStatusRequest?.id,maritalStatusListEmployee?.maritalStatusRequest?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'maritalStatusListEmployee.orderNo.label',default:'orderNo')}" value="${maritalStatusListEmployee?.orderNo}"/>
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class=" isRequired" label="${message(code:'maritalStatusListEmployee.recordStatus.label',default:'recordStatus')}" value="${maritalStatusListEmployee?.recordStatus}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="statusReason" size="8"  class=" isRequired" label="${message(code:'maritalStatusListEmployee.statusReason.label',default:'statusReason')}" value="${maritalStatusListEmployee?.statusReason}"/>
</el:formGroup>