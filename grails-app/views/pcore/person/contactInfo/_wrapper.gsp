<g:hiddenField name="${fieldName}.id" value="${contactInfo?.id}"/>
<lay:wall title="${g.message(code: 'contactInfo.relatedType.label')}">
    <el:formGroup>
        <el:select valueMessagePrefix="ContactInfoClassification"  from="${ps.police.pcore.enums.v1.ContactInfoClassification.values()}" name="${fieldName}.relatedObjectType" size="8"  class=" ${isRequired}" label="${message(code:'contactInfo.relatedObjectType.label',default:'relatedObjectType')}" value="${contactInfo?.relatedObjectType}" />
    </el:formGroup>
    <script>
        $("#${fieldName}-relatedObjectType").change(function () {
            if ($(this).val() == '${ps.police.pcore.enums.v1.ContactInfoClassification.ORGANIZATION}') {
                $("#${fieldName}-person-id").closest(".pcp-form-control").hide();
                $("#${fieldName}-organization-id").closest(".pcp-form-control").show();
            } else if ($(this).val() == '${ps.police.pcore.enums.v1.ContactInfoClassification.PERSON}') {
                $("#${fieldName}-organization-id").closest(".pcp-form-control").hide();
                $("#${fieldName}-person-id").closest(".pcp-form-control").show();
            } else {
                $("#${fieldName}-organization-id").closest(".pcp-form-control").hide();
                $("#${fieldName}-person-id").closest(".pcp-form-control").hide();
            }
        });
        $(document).ready(function () {
            $("#${fieldName}-relatedObjectType").trigger("change");
        });
    </script>

    <el:formGroup>
        <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="organization" action="autocomplete" name="${fieldName}.organization.id" label="${message(code:'contactInfo.organization.label',default:'organization')}" values="${[[contactInfo?.organization?.id,contactInfo?.organization?.descriptionInfo?.localName]]}" />
    </el:formGroup>
    <el:formGroup>
        <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="person" action="autocomplete" name="${fieldName}.person.id" label="${message(code:'contactInfo.person.label',default:'person')}" values="${[[contactInfo?.person?.id,contactInfo?.person?.localFullName]]}" />
    </el:formGroup>
</lay:wall>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="contactMethod" action="autocomplete" name="${fieldName}.contactMethod.id" label="${message(code:'contactInfo.contactMethod.label',default:'contactMethod')}" values="${[[contactInfo?.contactMethod?.id,contactInfo?.contactMethod?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="contactType" action="autocomplete" name="${fieldName}.contactType.id" label="${message(code:'contactInfo.contactType.label',default:'contactType')}" values="${[[contactInfo?.contactType?.id,contactInfo?.contactType?.descriptionInfo?.localName]]}" />
</el:formGroup>

%{--<el:formGroup>--}%
    %{--<el:dateField name="${fieldName}.fromDate"  size="8" class=" ${isRequired}" label="${message(code:'contactInfo.fromDate.label',default:'fromDate')}" value="${contactInfo?.fromDate}" />--}%
%{--</el:formGroup>--}%
%{--<el:formGroup>--}%
    %{--<el:dateField name="${fieldName}.toDate"  size="8" class=" ${isRequired}" label="${message(code:'contactInfo.toDate.label',default:'toDate')}" value="${contactInfo?.toDate}" />--}%
%{--</el:formGroup>--}%

<lay:wall title="${g.message(code: 'contactInfo.address.label')}">
    <g:render template="/pcore/location/wrapper" model="[location:contactInfo?.address,fieldName:fieldName+'.location',isRequired:isRequired]" />
</lay:wall>
<el:formGroup>
    <el:textArea name="${fieldName}.value" size="8"  class=" ${isRequired}" label="${message(code:'contactInfo.value.label',default:'value')}" value="${contactInfo?.value}"/>
</el:formGroup>
