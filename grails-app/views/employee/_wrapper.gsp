<g:if test="${disableFormGroupName}">
        <g:if test="${isDisabled}">
            <el:hiddenField name="${name?:"employee.id"}" value="${bean?.id}" />
        </g:if>
        <el:autocomplete
                         isDisabled="${isDisabled?:false}" optionKey="id" optionValue="name" size="${size?:'8'}"
                         class=" ${isSearch?"":"isRequired"}" controller="employee" style="${isHiddenInfo == "true"?("display: none;"):""}"
                         action="autocomplete" name="${name?:"employee.id"}" id="${id?:"employee.id"}"
                         label="${messageValue?:(message(code:'employee.label',default:'employee'))}"
                         paramsGenerateFunction="${paramsGenerateFunction?:""}" onChange="${onChange?:''}"
                         values="${[[bean?.id,bean?.transientData?.personDTO?.localFullName]]}" />
</g:if>
<g:elseif test="${withOutForm}">
    <el:autocomplete
                     isDisabled="${isDisabled?:false}" optionKey="id" optionValue="name" size="${size?:'8'}"
                     class=" ${isSearch?"":"isRequired"}" controller="employee"
                     action="autocomplete" name="${name?:"employee.id"}" id="${id?:"employee.id"}"
                     label="${messageValue?:(message(code:'employee.label',default:'employee'))}"
                     paramsGenerateFunction="${paramsGenerateFunction?:""}" onChange="${onChange?:''}"
                     values="${[[bean?.id,bean?.transientData?.personDTO?.localFullName]]}" />
</g:elseif>
<g:else>
    <el:formGroup id="${formGroupName?:"employeeFormGroup"}" style="${isHiddenInfo == "true"?("display: none;"):""}">
        <g:if test="${isDisabled}">
            <el:hiddenField name="${name?:"employee.id"}" value="${bean?.id}" />
        </g:if>
        <el:autocomplete
                         isDisabled="${isDisabled?:false}" optionKey="id" optionValue="name" size="${size?:'8'}"
                         class=" ${isSearch?"":"isRequired"}" controller="employee"
                         action="autocomplete" name="${name?:"employee.id"}" id="${id?:"employee.id"}"
                         label="${messageValue?:(message(code:'employee.label',default:'employee'))}"
                         paramsGenerateFunction="${paramsGenerateFunction?:""}"
                         multiple="${isMultiple == true ?"true":"false"}" onChange="${onChange?:''}"
                         values="${[[bean?.id,bean?.transientData?.personDTO?.localFullName]]}" />
    </el:formGroup>
</g:else>


