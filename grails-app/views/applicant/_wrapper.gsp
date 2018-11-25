<g:if test="${disableFormGroupName}">
    <g:if test="${isDisabled}">
        <el:hiddenField name="${name?:"applicant.id"}" value="${bean?.id}" />
    </g:if>
    <el:autocomplete preventSpaces="true" isDisabled="${isDisabled?:false}" optionKey="id" optionValue="name" size="${size?:'8'}"
                     class=" ${isRequired != null?(isRequired?"isRequired":""):"isRequired"}"
                     controller="applicant"
                     paramsGenerateFunction="${applicantParamsFunction?:""}"
                     action="autocomplete" name="${name?:"applicant.id"}" id="${id?:"applicant.id"}"
                     label="${messageValue?:(message(code:'applicant.entity',default:'applicant'))}"
                     values="${[[bean?.id,bean?.descriptionInfo?.localName]]}" />
</g:if>
<g:else>
    <el:formGroup id="${formGroupName?:"applicantFormGroup"}">
        <g:if test="${isDisabled}">
            <el:hiddenField name="${name?:"applicant.id"}" value="${bean?.id}" />
        </g:if>
        <el:autocomplete preventSpaces="false" isDisabled="${isDisabled?:false}" optionKey="id" optionValue="name" size="${size?:'8'}"
                         class=" ${isRequired != null?(isRequired?"isRequired":""):"isRequired"}"
                         controller="applicant"
                         paramsGenerateFunction="${applicantParamsFunction?:""}"
                         action="autocomplete" name="${name?:"applicant.id"}" id="${id?:"applicant.id"}"
                         label="${messageValue?:(message(code:'applicant.entity',default:'applicant'))}"
                         values="${[[bean?.id,bean?.descriptionInfo?.localName]]}" />
    </el:formGroup>
</g:else>


