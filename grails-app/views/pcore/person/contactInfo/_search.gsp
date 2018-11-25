
<el:formGroup>
    <el:select valueMessagePrefix="ContactInfoClassification"  from="${ps.police.pcore.enums.v1.ContactInfoClassification.values()}" name="relatedObjectType" size="8"  class=" isRequired" label="${message(code:'contactInfo.relatedObjectType.label',default:'relatedObjectType')}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="organization" action="autocomplete" name="organization.id" label="${message(code:'contactInfo.organization.label',default:'organization')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="person" action="autocomplete" name="person.id" label="${message(code:'contactInfo.person.label',default:'person')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="contactType" action="autocomplete" name="contactType.id" label="${message(code:'contactInfo.contactType.label',default:'contactType')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="contactMethod" action="autocomplete" name="contactMethod.id" label="${message(code:'contactInfo.contactMethod.label',default:'contactMethod')}" />
</el:formGroup>


%{--<el:formGroup>--}%
    %{--<el:dateField name="fromDate"  size="8" class="" label="${message(code:'contactInfo.fromDate.label',default:'fromDate')}" />--}%
%{--</el:formGroup>--}%
%{--<el:formGroup>--}%
    %{--<el:dateField name="toDate"  size="8" class="" label="${message(code:'contactInfo.toDate.label',default:'toDate')}" />--}%
%{--</el:formGroup>--}%

<lay:wall title="${g.message(code: 'contactInfo.relatedType.label')}">
    <g:render template="/pcore/location/wrapper" model="[bean:contactInfo?.address,isSearch:true]" />
</lay:wall>

<el:formGroup>
    <el:textArea name="value" size="8"  class="" label="${message(code:'contactInfo.value.label',default:'value')}" />
</el:formGroup>
