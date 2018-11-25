<g:if test="${disableFormGroupName}">
        <g:if test="${isDisabled}">
            <el:hiddenField name="${name?:"person.id"}" value="${bean?.id}" />
        </g:if>
        <el:autocomplete preventSpaces="true" isDisabled="${isDisabled?:false}" optionKey="id" optionValue="name" size="${size?:'8'}"
                         class=" ${isSearch?"":"isRequired"}" controller="person" style="${isHiddenInfo == "true"?("display: none;"):""}"
                         action="autocomplete" name="${name?:"person.id"}" id="${id?:"person.id"}"
                         label="${messageValue?:(message(code:'person.label',default:'person'))}"
                         values="${[[bean?.id,bean?.localFullName]]}" />
</g:if>
<g:else>
    <el:formGroup id="${formGroupName?:"personFormGroup"}" style="${isHiddenInfo == "true"?("display: none;"):""}">
        <g:if test="${isDisabled}">
            <el:hiddenField name="${name?:"person.id"}" value="${bean?.id}" />
        </g:if>
        <el:autocomplete preventSpaces="true" isDisabled="${isDisabled?:false}" optionKey="id" optionValue="name" size="${size?:'8'}"
                         class=" ${isSearch?"":"isRequired"}" controller="person"
                         action="autocomplete" name="${name?:"person.id"}" id="${id?:"person.id"}"
                         label="${messageValue?:(message(code:'person.label',default:'person'))}"
                         values="${[[bean?.id,bean?.localFullName]]}" />
    </el:formGroup>
</g:else>

