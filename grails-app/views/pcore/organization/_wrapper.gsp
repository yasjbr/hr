<g:if test="${disableFormGroupName}">
    <g:if test="${isDisabled}">
        <el:hiddenField name="${name?:"organization.id"}" value="${bean?.id}" />
    </g:if>
    <el:autocomplete preventSpaces="true" isDisabled="${isDisabled?:false}" optionKey="id" optionValue="name" size="${size?:'8'}"
                     class=" ${isRequired != null?(isRequired?"isRequired":""):"isRequired"}"
                     controller="pcore"
                     paramsGenerateFunction="${organizationParamsFunction?:""}"
                     action="organizationAutoComplete" name="${name?:"organization.id"}" id="${id?:"organization.id"}"
                     label="${messageValue?:(message(code:'organization.label',default:'organization'))}"
                     values="${[[bean?.id,bean?.descriptionInfo?.localName]]}" />
</g:if>
<g:else>
    <el:formGroup id="${formGroupName?:"organizationFormGroup"}">
        <g:if test="${isDisabled}">
            <el:hiddenField name="${name?:"organization.id"}" value="${bean?.id}" />
        </g:if>
        <el:autocomplete preventSpaces="false" isDisabled="${isDisabled?:false}" optionKey="id" optionValue="name" size="${size?:'8'}"
                         class=" ${isRequired != null?(isRequired?"isRequired":""):"isRequired"}"
                         controller="pcore"
                         paramsGenerateFunction="${organizationParamsFunction?:""}"
                         action="organizationAutoComplete" name="${name?:"organization.id"}" id="${id?:"organization.id"}"
                         label="${messageValue?:(message(code:'organization.label',default:'organization'))}"
                         values="${[[bean?.id,bean?.descriptionInfo?.localName]]}" />
    </el:formGroup>
</g:else>


