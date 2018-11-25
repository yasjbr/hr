<g:if test="${disableFormGroupName}">
    <g:if test="${isDisabled}">
        <el:hiddenField name="${name?:"department.id"}" value="${bean?.id}" />
    </g:if>
    <el:autocomplete preventSpaces="true" isDisabled="${isDisabled?:false}" optionKey="id" optionValue="name" size="${size?:'8'}"
                     class=" ${isRequired != null?(isRequired?"isRequired":""):"isRequired"}"
                     controller="department"
                     paramsGenerateFunction="${departmentParamsFunction?:""}"
                     action="autocomplete" name="${name?:"department.id"}" id="${id?:"department.id"}"
                     label="${messageValue?:(message(code:'department.label',default:'department'))}"
                     values="${[[bean?.id,bean?.descriptionInfo?.localName]]}" />
</g:if>
<g:else>
    <el:formGroup id="${formGroupName?:"departmentFormGroup"}">
        <g:if test="${isDisabled}">
            <el:hiddenField name="${name?:"department.id"}" value="${bean?.id}" />
        </g:if>
        <el:autocomplete preventSpaces="false" isDisabled="${isDisabled?:false}" optionKey="id" optionValue="name" size="${size?:'8'}"
                         class=" ${isRequired != null?(isRequired?"isRequired":""):"isRequired"}"
                         controller="department"
                         paramsGenerateFunction="${departmentParamsFunction?:""}"
                         action="autocomplete" name="${name?:"department.id"}" id="${id?:"department.id"}"
                         label="${messageValue?:(message(code:'department.label',default:'department'))}"
                         values="${[[bean?.id,bean?.descriptionInfo?.localName]]}" />
    </el:formGroup>
</g:else>


