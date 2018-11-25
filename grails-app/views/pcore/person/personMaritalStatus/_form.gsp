
<g:render template="/pcore/person/wrapper" model="[id:(personCallBackId?:'personId'),
                                             name:'person.id',
                                                   isHiddenInfo:params.isHiddenPersonInfo,
                                             bean:personMaritalStatus?.person,
                                             isDisabled:isPersonDisabled]" />
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="maritalStatus" action="autocomplete" name="maritalStatus.id" label="${message(code:'personMaritalStatus.maritalStatus.label',default:'personMaritalStatus')}" values="${[[personMaritalStatus?.maritalStatus?.id,personMaritalStatus?.maritalStatus?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="fromDate"  size="8" class=" " label="${message(code:'personMaritalStatus.fromDate.label',default:'fromDate')}" value="${personMaritalStatus?.fromDate}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" label="${message(code:'personMaritalStatus.toDate.label',default:'toDate')}" value="${personMaritalStatus?.toDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'personMaritalStatus.note.label',default:'note')}" value="${personMaritalStatus?.note}"/>
</el:formGroup>

<el:formGroup>
    <el:checkboxField name="isCurrent" size="8"  class="" label="${message(code:'personMaritalStatus.isCurrent.label',default:'isCurrent')}" value="${personMaritalStatus?.isCurrent}" isChecked="${personMaritalStatus?.isCurrent}" />
</el:formGroup>