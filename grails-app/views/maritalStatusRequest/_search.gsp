<el:formGroup>
    <el:textField name="id" size="6" class=" "
                  label="${message(code: 'maritalStatusRequest.id.label', default: 'id')}"
                  value=""/>

    <g:render template="/employee/wrapper" model="[isSearch   : true,
                                                   withOutForm: true,
                                                   size       : 6]"/>

</el:formGroup>
<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="militaryRank"
            action="autocomplete"
            name="militaryRank.id"
            label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>
    <el:range type="date" name="requestDate" size="6" class="" setMinDateFromFor="dueRequestDate"
              label="${message(code: 'maritalStatusRequest.requestDate.label', default: 'requestDate')}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=""
            controller="pcore"
            action="maritalStatusAutoComplete"
            name="oldMaritalStatusId"
            id="oldMaritalStatusId"
            label="${message(code: 'maritalStatusRequest.oldMaritalStatusId.label', default: 'oldMaritalStatusId')}"/>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=""
            controller="pcore"
            action="maritalStatusAutoComplete"
            name="newMaritalStatusId"
            label="${message(code: 'maritalStatusRequest.newMaritalStatusId.label', default: 'newMaritalStatusId')}"/>
</el:formGroup>
<el:formGroup>
    <el:range type="date" name="maritalStatusDate" size="6" class="" setMinDateFromFor="maritalStatusDate"
              label="${message(code: 'maritalStatusRequest.maritalStatusDate.label', default: 'maritalStatusDate')}"/>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="person"
            action="autocomplete"
            name="relatedPersonId"
            label="${message(code: 'maritalStatusRequest.transientData.relatedPersonDTO.localFullName.label', default: 'relatedPersonId')}"
            values=""/>
</el:formGroup>
<g:render template="/request/wrapperManagerialOrder" />
<g:if test="${!isList}">
    <el:formGroup>
        <el:select valueMessagePrefix="EnumRequestStatus"
                   from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
                   name="requestStatus" size="6" class=""
                   label="${message(code: 'maritalStatusRequest.requestStatus.label', default: 'requestStatus')}"/>

        <sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
            <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                             controller="firm" action="autocomplete"
                             name="firm.id" label="${message(code: 'firm.label', default: 'firm')}"/>
        </sec:ifAnyGranted>
    </el:formGroup>

</g:if>



