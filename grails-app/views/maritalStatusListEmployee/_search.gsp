<el:formGroup>
    <el:textField name="maritalStatusRequest.id" size="6" class=""
                     label="${message(code: 'maritalStatusRequest.id.label', default: 'id')}"
                     value=""/>
    <g:render template="/employee/wrapper" model="[isSearch     : true,
                                                   withOutForm  : true,
                                                   size         : 6]"/>
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
    <el:textField
            name="financialNumber"
            size="6"
            class=""
            label="${message(code: 'employee.financialNumber.label', default: 'financialNumber')}"
            value=""/>

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
<g:if test="${!isList}">
    <el:formGroup>
        <el:range type="date" name="requestDate" size="6" class="" setMinDateFromFor="dueRequestDate"
                  label="${message(code: 'maritalStatusRequest.requestDate.label', default: 'requestDate')}"/>

        <el:select valueMessagePrefix="EnumListRecordStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}"
                   name="recordStatus" size="6" class=""
                   label="${message(code: 'maritalStatusListEmployee.recordStatus.label', default: 'recordStatus')}"/>
    </el:formGroup>
</g:if>



